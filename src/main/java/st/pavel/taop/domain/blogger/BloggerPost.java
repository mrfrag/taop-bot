package st.pavel.taop.domain.blogger;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Data;

@Data
public class BloggerPost {

	private String kind;

	private Long id;

	private BlogId blog;

	private OffsetDateTime published;

	private OffsetDateTime updated;

	private String etag;

	private String url;

	private String selfLink;

	private String title;

	private String content;

	private BloggerAuthor author;

	private Replies replies;

	private List<String> labels;

}
