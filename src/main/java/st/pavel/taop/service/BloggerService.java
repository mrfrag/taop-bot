package st.pavel.taop.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.mapdb.Atomic.Var;
import org.mapdb.DB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import st.pavel.taop.components.BloggerApi;
import st.pavel.taop.misc.SerializerDateTime;

/**
 * Checks for new posts.
 * 
 * @author spv
 */
@Service("bloggerService")
public class BloggerService {

	private static final String TAOP_POST_TITLE = "The Art Of Programming";

	private Var<OffsetDateTime> latestUpdateTimeVar;

	private BloggerApi bloggerApi;

	private TaopBotService taopBotService;

	@Autowired
	public BloggerService(BloggerApi bloggerApi, TaopBotService taopBotService, DB db) {
		this.bloggerApi = bloggerApi;
		this.taopBotService = taopBotService;
		this.latestUpdateTimeVar = db.atomicVar("updateTime", new SerializerDateTime()).createOrOpen();
	}

	@Scheduled(cron = "0 */5 * * * *")
	public void lookForPosts() {
		if (latestUpdateTimeVar.get() == null) {
			latestUpdateTimeVar.set(OffsetDateTime.of(2017, 4, 16, 0, 0, 0, 0, ZoneOffset.UTC));
		}
		bloggerApi.listRecentPosts().getItems().stream()
		          .filter(p -> p.getTitle().contains(TAOP_POST_TITLE) && p.getPublished().isAfter(latestUpdateTimeVar.get()))
		          .forEach(p -> taopBotService.serveUpdate(p));
		latestUpdateTimeVar.set(OffsetDateTime.now());
	}

}
