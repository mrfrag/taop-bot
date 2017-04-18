package st.pavel.taop.components;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.time.OffsetDateTime;

import org.junit.Before;
import org.junit.Test;
import org.mapdb.DBMaker;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;

import st.pavel.taop.domain.TaopPost;

public class GitHubGrabberTests {

	private static final String POST_CONTENT = "\u003cp\u003e\u003cbr\u003e\u003c/p\u003e\u003cdiv class=\"separator\" style=\"clear: both; text-align: center;\"\u003e\u003ca href=\"https://lh3.googleusercontent.com/-PnsTEuAT2jQ/WLT1lJtRX_I/AAAAAAAAGMM/1OsSa76TIpo/s9999/taop131_full.jpg\"\u003e\u003cimg src=\"https://lh3.googleusercontent.com/-KecsfzdxjQ8/WLT1kzGKFWI/AAAAAAAAGMI/Ua9rRguF8LA/s9999/taop131.jpg\" width=\"400\" style=\"max-width: 100%;\"\u003e\u003c/a\u003e\u003c/div\u003e\u003cp\u003eНеобходимое и достаточное\u003cbr\u003e\u003ca href=\"http://bit.ly/TAOP131g\" target=\"_blank\"\u003eSoftware Engineering at Google\u003c/a\u003e \u003cbr\u003e\u003ca href=\"http://bit.ly/TAOP131dl\" target=\"_blank\"\u003eSiraj Raval's Deep Learning\u003c/a\u003e\u003c/p\u003e\u003cp\u003e\u003ca href=\"http://bit.ly/TAOP131cl\" target=\"_blank\"\u003eDaniel Higginbotham — Clojure for the Brave and True: Learn the Ultimate Language and Become a Better Programmer\u003c/a\u003e\u003c/p\u003e\u003cp\u003e\u003ca href=\"http://bit.ly/TAOP131book\" target=\"_blank\"\u003eБ. Алан Уоллис — Революция внимания. Пробуждение силы сосредоточенного ума\u003c/a\u003e\u003cbr\u003e\u003c/p\u003e\u003cdiv class=\"custom-html-block\"\u003e\u003ciframe src=\"https://www.podbean.com/media/player/9kg87-6827cf?from=yiiadmin\" data-link=\"https://www.podbean.com/media/player/9kg87-6827cf?from=yiiadmin\" height=\"100\" width=\"100%\" frameborder=\"0\" scrolling=\"no\" data-name=\"pb-iframe-player\"\u003e\u003c/iframe\u003e\u003c/div\u003e\u003cp\u003eБлагодарности патронам: Sergey Kiselev, Sergii Zhuk, Aleksandr Kiriushin, Nikolay Ushmodin, Pavel Drobushevich, Pavel Sitnikov, Bogdan Storozhuk, B7W, Лагуновский Иван, Sergey Vinyarsky, Yakov Krainov, Sergey Petrov\u003c/p\u003e\u003cp\u003e\u003ca href=\"http://bit.ly/TAOPpatron\" target=\"_blank\"\u003eПоддержи подкаст\u003c/a\u003e\u003cbr\u003e\u003ca href=\"http://bit.ly/TAOPiTunes\" target=\"_blank\"\u003eПодпишись в iTunes\u003c/a\u003e\u003cbr\u003e\u003ca href=\"http://bit.ly/TAOPrss\" target=\"_blank\"\u003eПодпишись без iTunes\u003c/a\u003e\u003cbr\u003e\u003ca href=\"http://bit.ly/TAOP131mp3\" target=\"_blank\"\u003eСкачай подкаст\u003c/a\u003e\u003cbr\u003e\u003ca href=\"http://bit.ly/oldtaop\" target=\"_blank\"\u003eСтарые выпуски\u003c/a\u003e\u003c/p\u003e";

	private static final String EXPECTED_IMG_URL = "https://lh3.googleusercontent.com/-KecsfzdxjQ8/WLT1kzGKFWI/AAAAAAAAGMI/Ua9rRguF8LA/s9999/taop131.jpg";

	private static final String EXPECTED_PATRONS_LIST = "Sergey Kiselev, Sergii Zhuk, Aleksandr Kiriushin, Nikolay Ushmodin, Pavel Drobushevich, Pavel Sitnikov, Bogdan Storozhuk, B7W, Лагуновский Иван, Sergey Vinyarsky, Yakov Krainov, Sergey Petrov";

	@Mock
	private ListenableFuture<Response> future;

	@Mock
	private Response response;

	@Mock
	private AsyncHttpClient httpClient;

	@Mock
	private BoundRequestBuilder boundRequestBuilder;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test() throws Exception {

		PatronsRegistry patronsRegistry = new PatronsRegistry(DBMaker.memoryDB().make());

		when(response.getStatusCode()).thenReturn(404);
		when(future.get()).thenReturn(response);
		when(boundRequestBuilder.execute()).thenReturn(future);
		when(httpClient.prepareGet(Mockito.anyString())).thenReturn(boundRequestBuilder);

		GitHubGrabber grabber = new GitHubGrabber();
		grabber.init();

		setField(grabber, "patronsRegistry", patronsRegistry);
		setField(grabber, "httpClient", httpClient);

		TaopPost post = new TaopPost();
		post.setContent(POST_CONTENT);
		post.setNumber(131l);
		post.setCreated(OffsetDateTime.now());

		grabber.downloadContent(post);

		assertEquals(EXPECTED_IMG_URL, post.getCover());
		assertEquals(EXPECTED_PATRONS_LIST, patronsRegistry.get());

	}

}
