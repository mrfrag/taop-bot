package st.pavel.taop.components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;
import com.overzealous.remark.Options;
import com.overzealous.remark.Remark;

import st.pavel.taop.domain.TaopPost;

@Component
public class GitHubGrabber {

	private static final String GITHUB_BASE_URL = "https://raw.githubusercontent.com/golodnyj/TAOP/head";

	private static final String POST_CONTENT_URL_TEMPLATE = "/themes/%d/%s.md";

	private static final String POST_COVER_PATH_URL = "/cover%20art/covers%20jpg";

	private static final String POST_COVER_URL_TEMPLATE = "/taop%d.jpg";

	private static final String PATRONS_SUFFIX = "Благодарности патронам:";

	private PatronsRegistry patronsRegistry;

	private AsyncHttpClient httpClient;

	@PostConstruct
	public void init() {
		httpClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().setFollowRedirect(true).build());
	}

	public TaopPost downloadContent(TaopPost post) throws Exception {
		return getGitHubContent(post, post.getCreated().getYear())
		                                                          .orElseGet(() -> getGitHubContent(post, post.getCreated().getYear() - 1)
		                                                                                                                                  .orElseGet(() -> convertContent(post)));
	}

	private String buildGitHubResourceUrl(String path, @NotNull String template, Object... args) {
		StringBuilder builder = new StringBuilder(GITHUB_BASE_URL);
		if (StringUtils.isNotBlank(path)) {
			builder.append(path);
		}
		return builder.append(String.format(template, args)).toString();
	}

	private Optional<TaopPost> getGitHubContent(TaopPost post, Integer year) {
		try {
			Response response = httpClient.prepareGet(buildGitHubResourceUrl(null, POST_CONTENT_URL_TEMPLATE, year, StringUtils.trim(post.getTitle()))).execute().get();
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				post.setContent(response.getResponseBody());
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getResponseBodyAsStream()))) {
					reader.lines()
					      .filter(line -> line.startsWith(PATRONS_SUFFIX))
					      .findFirst()
					      .ifPresent(line -> patronsRegistry.update(post.getNumber(), StringUtils.trim(StringUtils.substringAfter(line, PATRONS_SUFFIX))));
				}
				post.setCover(buildGitHubResourceUrl(POST_COVER_PATH_URL, POST_COVER_URL_TEMPLATE, post.getNumber()));
				return Optional.of(post);
			} else {
				return Optional.empty();
			}
		} catch (InterruptedException | ExecutionException | IOException e) {
			return Optional.empty();
		}
	}

	/**
	 * Called if we didn't find content in GitHub repo.
	 * 
	 * @param post
	 * @return
	 */
	private TaopPost convertContent(TaopPost post) {
		Document document = Jsoup.parseBodyFragment(post.getContent());

		Elements elements = document.select("div.separator > a > img");
		post.setCover(elements.first().attr("src"));

		document.select("div.separator").first().remove();

		Elements pElements = document.select("p");

		Options options = Options.github();
		options.inlineLinks = true;
		options.hardwraps = true;

		Remark remark = new Remark(options);

		pElements.stream()
		         .filter(element -> element.hasText() && element.text().startsWith(PATRONS_SUFFIX))
		         .findFirst()
		         .ifPresent(element -> patronsRegistry.update(post.getNumber(), StringUtils.trim(StringUtils.substringAfter(element.text(), PATRONS_SUFFIX))));

		post.setContent(remark.convert(document));
		return post;
	}

}
