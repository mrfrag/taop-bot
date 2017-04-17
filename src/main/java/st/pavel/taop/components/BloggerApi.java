package st.pavel.taop.components;

import java.util.Collections;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.ImmutableMap;

import st.pavel.taop.domain.blogger.BloggerPost;
import st.pavel.taop.domain.blogger.PostList;

@Component("bloggerApi")
public class BloggerApi {

	private static final String API_URL = "https://www.googleapis.com/blogger/v3/blogs/14998367/posts";

	private static final String QUERY_PATTERN = "-art-of-programming";

	@Autowired
	private RestTemplate restTemplate;

	@Value("${blogger.api.key}")
	private String key;

	public PostList listRecentPosts() {
		return restTemplate.getForObject(API_URL + "?key={key}", PostList.class, Collections.singletonMap("key", key));
	}

	public BloggerPost getPost(Long number) {
		PostList result = restTemplate.getForObject(API_URL + "/search?q={q}&key={key}", PostList.class, ImmutableMap.<String, String> builder()
		                                                                                                             .put("q", number + QUERY_PATTERN)
		                                                                                                             .put("key", key)
		                                                                                                             .build());
		if (CollectionUtils.isNotEmpty(result.getItems())) {
			return result.getItems().get(0);
		} else {
			throw new IllegalArgumentException(String.format("post %d doesn't exists.", number));
		}
	}

}
