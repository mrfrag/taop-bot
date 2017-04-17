package st.pavel.taop.service.telegram;

import static ru.skuptsov.telegram.bot.platform.client.command.MessageResponse.sendMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.skuptsov.telegram.bot.platform.client.command.MessageResponse;
import ru.skuptsov.telegram.bot.platform.handler.DefaultMessageHandler;
import ru.skuptsov.telegram.bot.platform.model.UpdateEvent;
import st.pavel.taop.components.Messages;

@Component
public class DefaultHandler implements DefaultMessageHandler {
	
	@Autowired
	private Messages messages;

	@Override
	public MessageResponse handle(UpdateEvent event) {
		return sendMessage(messages.get("default"), event);
	}

}
