package st.pavel.taop.service.telegram;

import static ru.skuptsov.telegram.bot.platform.client.command.MessageResponse.sendChatAction;
import static ru.skuptsov.telegram.bot.platform.client.command.MessageResponse.sendMessage;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.skuptsov.telegram.bot.platform.client.command.MessageResponse;
import ru.skuptsov.telegram.bot.platform.handler.ConditionMessageHandler;
import ru.skuptsov.telegram.bot.platform.model.UpdateEvent;
import ru.skuptsov.telegram.bot.platform.model.api.methods.send.SendChatAction;
import ru.skuptsov.telegram.bot.platform.model.api.methods.send.SendChatAction.ActionTypes;
import st.pavel.taop.components.Messages;
import st.pavel.taop.service.TaopBotService;

@Component
public class IssueCommandHandler implements ConditionMessageHandler {

	public static final String ISSUE_COMMAND_HOOK = "/issue";

	@Autowired
	private TaopBotService taopBotService;

	@Autowired
	private Messages messages;

	@Override
	public boolean isSuitableForProcessingEvent(UpdateEvent event) {
		return parseNumber(event).isPresent();
	}

	@Override
	public MessageResponse handle(UpdateEvent event) {
		Long number = parseNumber(event).get();
		Long chatId = event.getUpdate().getMessage().getChat().getId();
		if (number <= 100) {
			return sendMessage(messages.get("issue.before.hundred"), event);
		} else {
			return sendChatAction(SendChatAction.builder()
			                                    .chatId(chatId.toString())
			                                    .action(ActionTypes.TYPING)
			                                    .build())
			                                             .setCallback(message -> taopBotService.serveIssueCommand(number, chatId));
		}
	}

	private Optional<Long> parseNumber(UpdateEvent event) {
		String[] words = event.getUpdate().getMessage().getText().split(" ");
		if (words.length > 1 && words[0].equals(ISSUE_COMMAND_HOOK) && StringUtils.isNumeric(words[1])) {
			return Optional.of(Long.parseLong(words[1]));
		} else if (StringUtils.isNumeric(words[0])) {
			return Optional.of(Long.parseLong(words[0]));
		}
		return Optional.empty();
	}

}
