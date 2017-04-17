package st.pavel.taop.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import ru.skuptsov.telegram.bot.platform.client.TelegramBotApi;
import ru.skuptsov.telegram.bot.platform.model.api.methods.send.SendAudio;
import ru.skuptsov.telegram.bot.platform.model.api.methods.send.SendPhoto;
import ru.skuptsov.telegram.bot.platform.model.api.objects.Message;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class TelegramIntegrationTests {

	@Autowired
	private TelegramBotApi telegramBotApi;

	@Test
	public void testImageUrl() {
		Optional<Message> response = telegramBotApi.sendMessageSync(SendPhoto	.builder()
																				.chatId("101629747")
																				.photo("https://raw.githubusercontent.com/golodnyj/TAOP/head/cover%20art/covers%20jpg/taop102.jpg")
																				.build());

		assertTrue(response.isPresent());
		assertTrue(CollectionUtils.isNotEmpty(response.get().getPhoto()));
	}

	@Test
	public void testAudioUrl() {
		Optional<Message> response = telegramBotApi.sendMessageSync(SendAudio	.builder()
																				.chatId("101629747")
																				.audio("http://s67.podbean.com/pb/83166968692ccc738a2ae4ec67bddc6a/58a35844/data1/fs93/832311/uploads/103TheArtOfProgrammingBooksTrafficflow.mp3")
																				.build());

		assertTrue(response.isPresent());
		assertNotNull(response.get().getAudio());
	}

	@Test
	public void testAudioFile() {
		File file = new File(getClass().getClassLoader().getResource("audio_small.mp3").getFile());
		Optional<Message> response = telegramBotApi.sendMessageSync(SendAudio	.builder()
																				.chatId("101629747")
																				.file(file)
																				.duration(1000)
																				.title("символс")
																				.performer("русске")
																				.build());

		assertTrue(response.isPresent());
		assertNotNull(response.get().getAudio());
	}

}
