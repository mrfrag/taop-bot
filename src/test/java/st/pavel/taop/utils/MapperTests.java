package st.pavel.taop.utils;

import static org.junit.Assert.assertEquals;

import java.time.OffsetDateTime;

import org.junit.Test;

import st.pavel.taop.domain.TaopPost;
import st.pavel.taop.domain.blogger.BloggerPost;
import st.pavel.taop.misc.Mapper;

public class MapperTests {

	@Test
	public void testMapper() {
		Mapper mapper = new Mapper();

		BloggerPost bloggerPost = new BloggerPost();
		bloggerPost.setUpdated(OffsetDateTime.now());
		bloggerPost.setContent("<b>Content</b>");

		TaopPost taopPost = mapper.map(bloggerPost, TaopPost.class);

		assertEquals(bloggerPost.getUpdated(), taopPost.getCreated());
		assertEquals(bloggerPost.getContent(), taopPost.getContent());
	}

}
