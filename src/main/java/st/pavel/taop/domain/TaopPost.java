package st.pavel.taop.domain;

import java.io.File;
import java.io.Serializable;
import java.time.OffsetDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;

import lombok.Data;
import ru.skuptsov.telegram.bot.platform.model.api.methods.send.SendAudio;

@Data
@KeySpace("posts")
public class TaopPost implements Serializable {

	private static final long serialVersionUID = 2520347213870396396L;

	@Id
	private Long number;

	private Long id;

	private OffsetDateTime created;

	private String link;

	private String title;

	private String content;

	private String cover;

	private File audio;

	private String audioFileId;

	private Long duration;

	public SendAudio createSendAudioMessage(Long chatId) {
		SendAudio.SendAudioBuilder result = SendAudio.builder().chatId(chatId.toString());
		if (audioFileId == null) {
			result.file(audio);
			result.title(title);
			result.duration(duration.intValue());
			result.performer("golodnyj");
		} else {
			result.audio(audioFileId);
		}
		return result.build();
	}

	public boolean isAudioUploaded() {
		return audioFileId != null;
	}

}
