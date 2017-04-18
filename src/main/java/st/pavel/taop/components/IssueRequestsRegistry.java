package st.pavel.taop.components;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.mapdb.DB;
import org.mapdb.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Component
public class IssueRequestsRegistry {

	private Map<Long, Set<ChatRegistration>> issueRequestsMap;

	@SuppressWarnings("unchecked")
	@Autowired
	public IssueRequestsRegistry(DB db) {
		issueRequestsMap = db.hashMap("issueRequestsMap", Serializer.LONG, Serializer.JAVA).createOrOpen();
	}

	public synchronized boolean register(Long number, ChatRegistration registration) {
		if (number == null) {
			throw new IllegalArgumentException("Post number is required");
		}
		if (issueRequestsMap.containsKey(number)) {
			issueRequestsMap.get(number).add(registration);
			return true;
		} else {
			HashSet<ChatRegistration> registrations = new HashSet<>();
			registrations.add(registration);
			issueRequestsMap.put(number, registrations);
			return false;
		}
	}

	public Iterable<ChatRegistration> evict(Long number) {
		return issueRequestsMap.remove(number);
	}

	@AllArgsConstructor
	@Builder
	@EqualsAndHashCode(of = { "chatId" })
	public static class ChatRegistration implements Serializable {

		private static final long serialVersionUID = 6162025154872687837L;

		@Getter
		@Setter
		private Long chatId;

		@Setter
		private Consumer<Long> callback;

		@Getter
		@Setter
		private boolean notifyOnFail;

		public Optional<Consumer<Long>> getCallback() {
			return Optional.ofNullable(callback);
		}

	}

}
