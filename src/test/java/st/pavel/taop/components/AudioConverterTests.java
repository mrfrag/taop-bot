package st.pavel.taop.components;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;

import st.pavel.taop.domain.TaopPost;
import st.pavel.taop.service.StorageService;

public class AudioConverterTests {

	@Test
	public void testConvert() throws IOException {

		FfmpegExecutor ffmpegExecutor = new FfmpegExecutor();
		setField(ffmpegExecutor, "executablePath", System.getProperty("ffmpeg.path"));

		File output = File.createTempFile("audio", ".mp3");
		System.out.println(output.getAbsolutePath());

		StorageService storageService = mock(StorageService.class);
		when(storageService.getStorageFile(Mockito.anyString(), Mockito.anyString())).thenReturn(output);

		AudioConverter converter = new AudioConverter();
		setField(converter, "ffmpegExecutor", ffmpegExecutor);
		setField(converter, "storageService", storageService);

		TaopPost post = new TaopPost();
		post.setNumber(108l);

		converter.convert(new File(getClass().getClassLoader().getResource("audio.mp3").getFile()), post);

		assertTrue(output.exists());
		assertTrue(output.length() < 52428800);

	}

}
