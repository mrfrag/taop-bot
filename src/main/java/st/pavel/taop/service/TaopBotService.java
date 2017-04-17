package st.pavel.taop.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ru.skuptsov.telegram.bot.platform.client.TelegramBotApi;
import ru.skuptsov.telegram.bot.platform.model.api.methods.BotApiMethod.ParseModes;
import ru.skuptsov.telegram.bot.platform.model.api.methods.send.SendMessage;
import ru.skuptsov.telegram.bot.platform.model.api.methods.send.SendPhoto;
import ru.skuptsov.telegram.bot.platform.model.api.objects.Message;
import st.pavel.taop.components.AudioGrabber;
import st.pavel.taop.components.BloggerApi;
import st.pavel.taop.components.GitHubGrabber;
import st.pavel.taop.components.IssueRequestsRegistry;
import st.pavel.taop.components.IssueRequestsRegistry.ChatRegistration;
import st.pavel.taop.components.Messages;
import st.pavel.taop.domain.Chat;
import st.pavel.taop.domain.TaopPost;
import st.pavel.taop.domain.blogger.BloggerPost;
import st.pavel.taop.misc.Mapper;
import st.pavel.taop.repository.ChatRepository;
import st.pavel.taop.repository.TaopPostRepository;

/**
 * Handles information from the blogger, prepares data to be posted to Telegram
 * 
 * @author spv
 *
 */
@Service("taopService")
public class TaopBotService {

	private final Logger log = LoggerFactory.getLogger(TaopBotService.class);

	@Autowired
	private TelegramBotApi telegramBotApi;

	@Autowired
	private TaopPostRepository taopPostRepository;

	@Autowired
	private Mapper mapper;

	@Autowired
	private ChatRepository chatRepository;

	@Autowired
	private BloggerApi bloggerApi;

	@Autowired
	private GitHubGrabber gitHubGrabber;

	@Autowired
	private AudioGrabber audioGrabber;

	@Autowired
	private Messages messages;

	@Autowired
	private IssueRequestsRegistry issueRequestsRegistry;

	@Async
	public void serveUpdate(BloggerPost bloggerPost) {
		try {
			TaopPost taopPost = mapper.map(bloggerPost, TaopPost.class);
			Map<Boolean, List<Chat>> chats = StreamSupport.stream(chatRepository.findAll().spliterator(), false).collect(Collectors.partitioningBy(chat -> chat.isMuted()));
			chats.get(false).forEach(chat -> serveIssue(taopPost, ChatRegistration	.builder()
																					.chatId(chat.getId())
																					.build()));
			chats.get(true).forEach(chat -> chat.pushBlogUpdate(taopPost.getNumber()));
		} catch (Exception e) {
			log.error("Unable to serve podcast update.", e);
		}
	}

	@Async
	public void serveIssueCommand(final Long number, final Long chatId) {
		doServeIssueCommand(number, chatId, null);
	}

	@Async
	public void serveIssuePollCommand(final Long number, final Long chatId) {
		int unreadIssuesCount = chatRepository.findOne(chatId).getBlogUpdates().size();
		doServeIssueCommand(number, chatId, id -> telegramBotApi.sendMessageSync(SendMessage.builder()
																							.chatId(chatId.toString())
																							.text(messages.getCountLocalized("chat.unread", unreadIssuesCount))
																							.build()));
	}

	private void serveIssue(TaopPost taopPost, ChatRegistration registration) {

		if (issueRequestsRegistry.register(taopPost.getNumber(), registration)) {
			return;
		}

		if (taopPostRepository.exists(taopPost.getNumber())) {
			sendPostBroadcast(taopPostRepository.findOne(taopPost.getNumber()));
			return;
		}

		try {
			gitHubGrabber.downloadContent(taopPost);

			log.info("Issue {} downloaded.", taopPost.getTitle());

			taopPost.setAudio(audioGrabber.downloadAudio(taopPost));

			log.info("Issue {} audio downloaded.", taopPost.getTitle());

			taopPost = taopPostRepository.save(taopPost);
		} catch (Exception e) {
			log.error("Error processing blogger post.", e);

			if (taopPost.getAudio() != null) {
				taopPost.getAudio().delete();
			}

			sendPostErrorBroadcast(taopPost);

		}

		sendPostBroadcast(taopPost);

	}

	private void doServeIssueCommand(final Long number, Long chatId, Consumer<Long> callback) {
		TaopPost post = taopPostRepository.findOne(number);

		if (post != null) {
			sendPost(post, chatId);
			return;
		}

		log.info("Issue #{} is not found in local db, downloading...", number);

		BloggerPost bloggerPost = null;
		try {
			bloggerPost = bloggerApi.getPost(number);
			telegramBotApi.sendMessageSync(SendMessage	.builder()
														.chatId(chatId.toString())
														.text(messages.get("issue.processing"))
														.build());
		} catch (Exception e) {
			log.error("Error while serving issue command.", e);

			telegramBotApi.sendMessageAsync(SendMessage	.builder()
														.chatId(chatId.toString())
														.text(messages.get("issue.error"))
														.build());

			return;
		}
		serveIssue(mapper.map(bloggerPost, TaopPost.class), ChatRegistration.builder()
																			.chatId(chatId)
																			.callback(callback)
																			.notifyOnFail(true)
																			.build());

	}

	private void sendPost(TaopPost post, Long chatId) {
		String chatIdString = chatId.toString();

		telegramBotApi.sendMessageSync(SendMessage	.builder()
													.chatId(chatIdString)
													.text(post.getTitle())
													.build());
		telegramBotApi.sendMessageSync(SendPhoto.builder()
												.chatId(chatIdString)
												.photo(post.getCover())
												.build());
		telegramBotApi.sendMessageSync(SendMessage	.builder()
													.chatId(chatIdString)
													.text(post.getContent())
													.parseMode(ParseModes.MARKDOWN)
													.build());
		sendAudio(post, chatId);

	}

	private void sendPostBroadcast(TaopPost post) {
		issueRequestsRegistry.evict(post.getNumber()).forEach(chatRegistration -> {
			sendPost(post, chatRegistration.getChatId());
			chatRegistration.getCallback().ifPresent(callback -> callback.accept(chatRegistration.getChatId()));
		});
	}

	private void sendPostErrorBroadcast(TaopPost taopPost) {
		StreamSupport	.stream(issueRequestsRegistry.evict(taopPost.getNumber()).spliterator(), false)
						.filter(ChatRegistration::isNotifyOnFail)
						.forEach(registration -> {
							telegramBotApi.sendMessageAsync(SendMessage	.builder()
																		.chatId(registration.getChatId().toString())
																		.text(messages.get("issue.error"))
																		.build());
						});
	}

	private void sendAudio(TaopPost post, Long chatId) {
		Optional<Message> response = telegramBotApi.sendMessageSync(post.createSendAudioMessage(chatId));

		log.info("{} audio sent.", post.getTitle());

		response.ifPresent(message -> {
			log.info("Got response: {}", response.get().toString());

			if (!post.isAudioUploaded()) {
				post.setAudioFileId(response.get().getAudio().getFileId());
				taopPostRepository.save(post);
			}
		});
	}

}
