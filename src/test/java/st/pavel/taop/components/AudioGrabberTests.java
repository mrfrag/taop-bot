package st.pavel.taop.components;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.mockito.Mockito;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import st.pavel.taop.domain.TaopPost;
import st.pavel.taop.service.StorageService;

public class AudioGrabberTests {

	@Test
	public void testDownload() throws UnsupportedTagException, InvalidDataException, IOException, InterruptedException, ExecutionException {

		File audioFile = new File(getClass().getClassLoader().getResource("audio.mp3").getFile());

		TaopPost post = new TaopPost();
		post.setNumber(129l);

		StorageService storageService = mock(StorageService.class);
		when(storageService.storeFile(Mockito.anyObject(), Mockito.anyString(), Mockito.anyString())).thenReturn(audioFile);

		AudioConverter audioConverter = mock(AudioConverter.class);
		when(audioConverter.convert(Mockito.any(File.class), Mockito.any(TaopPost.class))).thenReturn(audioFile);

		AudioGrabber audioGrabber = new AudioGrabber();
		setField(audioGrabber, "storageService", storageService);
		setField(audioGrabber, "audioConverter", audioConverter);

		audioGrabber.downloadAudio(post);

		assertNotNull(post.getDuration());
	}

}
