package st.pavel.taop.domain;

import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@KeySpace("chats")
@NoArgsConstructor
public class Chat implements Serializable {

	private static final long serialVersionUID = -8042607763933026087L;

	@Id
	private Long id;

	private boolean muted;

	private Deque<Long> blogUpdates;

	public Chat(Long id) {
		this.id = id;
	}

	public void pushBlogUpdate(Long number) {
		if (blogUpdates == null) {
			blogUpdates = new LinkedList<>();
		}
		blogUpdates.add(number);
	}

	public Optional<Long> pollBlogUpdate() {
		if (blogUpdates != null && !blogUpdates.isEmpty()) {
			return Optional.of(blogUpdates.poll());
		}
		return Optional.empty();
	}

}
