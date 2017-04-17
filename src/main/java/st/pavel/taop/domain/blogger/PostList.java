package st.pavel.taop.domain.blogger;

import java.util.List;

import lombok.Data;

@Data
public class PostList {

	private String kind;

	private List<BloggerPost> items;

	private String etag;

}
