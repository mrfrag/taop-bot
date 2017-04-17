package st.pavel.taop.components;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;

import st.pavel.taop.domain.TaopPost;
import st.pavel.taop.service.StorageService;

@Component
public class AudioGrabber {

	private static final String MP3_SHORT_URL_PATTERN = "http://bit.ly/TAOP%dmp3";

	private static final String AUDIO_FILE_NAME = "audio.mp3";

	/**
	 * 50 MB in bytes
	 */
	private static final int MAX_FILE_SIZE = 50 * 1024 * 1024;

	@Autowired
	private StorageService storageService;

	@Autowired
	private AudioConverter audioConverter;

	public File downloadAudio(TaopPost post) throws IOException, UnsupportedTagException, InvalidDataException, InterruptedException, ExecutionException {
		try (AsyncHttpClient httpClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().setFollowRedirect(true).build())) {
			Response response = httpClient.prepareGet(String.format(MP3_SHORT_URL_PATTERN, post.getNumber())).execute().get();
			File audioFile = storageService.storeFile(response.getResponseBodyAsStream(), post.getNumber().toString(), AUDIO_FILE_NAME);
			post.setDuration(new Mp3File(audioFile).getLengthInSeconds());
			if (audioFile.length() > MAX_FILE_SIZE) {
				return audioConverter.convert(audioFile, post);
			}
			return audioFile;
		}
	}

}
