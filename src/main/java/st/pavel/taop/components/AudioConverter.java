package st.pavel.taop.components;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import st.pavel.taop.domain.TaopPost;
import st.pavel.taop.service.StorageService;

@Component
public class AudioConverter {

	private final Logger log = LoggerFactory.getLogger(AudioConverter.class);

	private static final String AUDIO_FILE_SUFFIX = ".mp3";

	@Autowired
	private FfmpegExecutor ffmpegExecutor;

	@Autowired
	private StorageService storageService;

	public File convert(@NotNull File source, @NotNull TaopPost post) {
		File result = storageService.getStorageFile(post.getNumber().toString(), post.getTitle() + AUDIO_FILE_SUFFIX);
		try {
			ffmpegExecutor.execute(source, result);
		} catch (Exception e) {
			log.error("Can't transcode audio file for post #" + post.getNumber(), e);
			throw new RuntimeException(e);
		}
		return result;
	}

}
