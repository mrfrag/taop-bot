package st.pavel.taop.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
public class FfmpegExecutor {

	private final Logger log = LoggerFactory.getLogger(FfmpegExecutor.class);

	private static final Pattern SUCCESS_PATTERN = Pattern.compile("^\\s*video\\:\\S+\\s+audio\\:\\S+\\s+subtitle\\:\\S+\\s+other streams\\:\\S+\\s+global headers\\:\\S+\\s+muxing overhead\\:\\s+\\S+$", Pattern.CASE_INSENSITIVE);

	@Value("${ffmpeg.path}")
	private String executablePath;

	public void execute(File source, File result) throws IOException {

		Runtime runtime = Runtime.getRuntime();
		Process ffmpeg = runtime.exec(createCommand(source, result));

		Thread killer = new Thread(() -> ffmpeg.destroy());
		runtime.addShutdownHook(killer);

		ExecutionStatus status = new ExecutionStatus();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
			reader.lines().forEach(line -> {
				log.info(line);
				status.setStatusLine(line);
			});

			runtime.removeShutdownHook(killer);

			if (!SUCCESS_PATTERN.matcher(status.getStatusLine()).matches()) {
				throw new RuntimeException(status.getStatusLine());
			}
		}
	}

	private String[] createCommand(File source, File result) {
		String[] command = new String[7];
		command[0] = executablePath;
		command[1] = "-i";
		command[2] = source.getAbsolutePath();
		command[3] = "-ab";
		command[4] = "96000";
		command[5] = "-y";
		command[6] = result.getAbsolutePath();
		return command;
	}

	@Data
	private static class ExecutionStatus {

		private String statusLine;

	}

}
