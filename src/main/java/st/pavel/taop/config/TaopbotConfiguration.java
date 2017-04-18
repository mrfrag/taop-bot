package st.pavel.taop.config;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

import org.mapdb.Atomic.Var;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.keyvalue.core.KeyValueTemplate;
import org.springframework.data.mapdb.MapDbKeyValueAdapter;
import org.springframework.data.mapdb.config.EnableMapDbRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import ru.skuptsov.telegram.bot.platform.config.BotPlatformConfiguration;
import st.pavel.taop.components.IssueRequestsRegistry.ChatRegistration;
import st.pavel.taop.domain.PatronsRecord;
import st.pavel.taop.misc.Mapper;
import st.pavel.taop.misc.SerializerDateTime;
import st.pavel.taop.misc.SerializerPatronsRecord;
import st.pavel.taop.repository.TaopPostRepository;

@Configuration
@EnableWebMvc
@EnableAsync
@EnableScheduling
@EnableMapDbRepositories(basePackageClasses = { TaopPostRepository.class })
@Import({ BotPlatformConfiguration.class })
public class TaopbotConfiguration {

	private static final String DB_NAME = "taopbot.db";

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public KeyValueTemplate keyValueTemplate(DB mapDb) {
		return new KeyValueTemplate(new MapDbKeyValueAdapter(mapDb));
	}

	@Bean
	public DB mapDb() {
		return DBMaker.fileDB(DB_NAME).checksumHeaderBypass().closeOnJvmShutdown().make();
	}

	@Bean
	public Mapper mapper() {
		return new Mapper();
	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasenames("i18n/messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setFallbackToSystemLocale(true);
		messageSource.setCacheSeconds(3600);
		messageSource.setAlwaysUseMessageFormat(false);
		return messageSource;
	}

}
