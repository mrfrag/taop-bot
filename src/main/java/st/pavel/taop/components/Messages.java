package st.pavel.taop.components;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

@Component
public class Messages {

	@Autowired
	private MessageSource messageSource;

	private MessageSourceAccessor messageSourceAccessor;

	@PostConstruct
	public void init() {
		messageSourceAccessor = new MessageSourceAccessor(messageSource, new Locale("ru"));
	}

	public String get(String code, Object... args) {
		return messageSourceAccessor.getMessage(code, args);
	}

	public String getCountLocalized(String code, Integer count) {
		if (count == 0) {
			return get(code + ".none");
		} else if (count == 1) {
			return get(code + ".one");
		} else if (count >= 5) {
			return get(code + ".fivemore", count);
		} else {
			return get(code + ".fourless", count);
		}
	}

}
