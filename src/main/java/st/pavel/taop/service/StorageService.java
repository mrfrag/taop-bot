package st.pavel.taop.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StorageService {

	@Value("${storage.root}")
	private String storageRootUri;

	@PostConstruct
	public void init() {
		File root = new File(storageRootUri);
		if (!root.exists()) {
			root.mkdirs();
		}
	}

	public File storeFile(InputStream input, String... path) {
		File result = getStorageFile(path);
		try {
			Files.copy(input, getStorageFile(path).toPath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	public File getStorageFile(String... path) {
		Path result = Paths.get(storageRootUri);
		try {
			for (int i = 0; i < path.length - 1; i++) {
				result = result.resolve(path[i]);
				if (!Files.exists(result)) {
					Files.createDirectory(result);
				}
			}
			return result.resolve(path[path.length - 1]).toFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
