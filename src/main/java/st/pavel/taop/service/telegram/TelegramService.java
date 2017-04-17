package st.pavel.taop.service.telegram;

import static ru.skuptsov.telegram.bot.platform.client.command.MessageResponse.sendChatAction;
import static ru.skuptsov.telegram.bot.platform.client.command.MessageResponse.sendMessage;

import java.util.Optional;

import org.mapdb.Atomic.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ru.skuptsov.telegram.bot.platform.client.command.MessageResponse;
import ru.skuptsov.telegram.bot.platform.handler.annotation.MessageHandler;
import ru.skuptsov.telegram.bot.platform.handler.annotation.MessageMapping;
import ru.skuptsov.telegram.bot.platform.model.UpdateEvent;
import ru.skuptsov.telegram.bot.platform.model.api.methods.send.SendChatAction;
import ru.skuptsov.telegram.bot.platform.model.api.methods.send.SendChatAction.ActionTypes;
import st.pavel.taop.components.Messages;
import st.pavel.taop.domain.Chat;
import st.pavel.taop.domain.PatronsRecord;
import st.pavel.taop.repository.ChatRepository;
import st.pavel.taop.service.TaopBotService;

/**
 * Handles telegram commands.
 * 
 * @author spv
 *
 */
@MessageHandler
public class TelegramService {

	@Autowired
	private ChatRepository chatRepository;

	@Autowired
	private TaopBotService taopBotService;

	@Autowired
	private Messages messages;

	@Autowired
	@Qualifier("patronsRecord")
	private Var<PatronsRecord> patronsRecord;

	@MessageMapping(text = "/pop")
	public MessageResponse refreshFeed(UpdateEvent updateEvent) {
		Long chatId = updateEvent.getUpdate().getMessage().getChat().getId();
		Chat chat = chatRepository.findOne(chatId);
		Optional<Long> update = chat.pollBlogUpdate();
		if (update.isPresent()) {
			return sendChatAction(SendChatAction.builder()
												.action(ActionTypes.TYPING)
												.chatId(chatId.toString())
												.build())
															.setCallback(message -> taopBotService.serveIssuePollCommand(update.get(), chatId));
		} else {
			return sendMessage(messages.get("chat.empty"), updateEvent);
		}
	}

	@MessageMapping(text = "/start")
	public MessageResponse startChat(UpdateEvent updateEvent) {
		chatRepository.save(new Chat(updateEvent.getUpdate().getMessage().getChat().getId()));
		return sendMessage(messages.get("hello"), updateEvent);
	}

	@MessageMapping(text = "/issue")
	public MessageResponse handleIssueCommand(UpdateEvent updateEvent) {
		return sendMessage(messages.get("issue.number"), updateEvent);
	}

	@MessageMapping(text = "/mute")
	public MessageResponse handleMuteCommand(UpdateEvent updateEvent) {
		Chat chat = chatRepository.findOne(updateEvent.getUpdate().getMessage().getChat().getId());
		chat.setMuted(true);
		chatRepository.save(chat);
		return sendMessage(messages.get("chat.mute"), updateEvent);
	}

	@MessageMapping(text = "/unmute")
	public MessageResponse handleUnmuteCommand(UpdateEvent updateEvent) {
		Chat chat = chatRepository.findOne(updateEvent.getUpdate().getMessage().getChat().getId());
		chat.setMuted(false);
		chatRepository.save(chat);
		return sendMessage(messages.get("chat.unmute"), updateEvent);
	}

	@MessageMapping(text = "/patrons")
	public MessageResponse handlePatronsCommand(UpdateEvent updateEvent) {
		return sendMessage(patronsRecord.get().getPatrons(), updateEvent);
	}

}
