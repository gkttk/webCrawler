import com.github.gkttk.webCrawler.Application;
import com.github.gkttk.webCrawler.configuration.WebConfig;
import com.github.gkttk.webCrawler.crawler.WebCrawler;
import com.github.gkttk.webCrawler.dto.CrawlingResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@ContextConfiguration(classes = WebConfig.class)
public class TestCrawler {

    @LocalServerPort
    private int port;

    @Autowired
    private WebCrawler webCrawler;

    @Test
    public void testCrawl() throws IOException {
        String testURL = "http://localhost:" + port + "/test";
        int testMaxDepth = 0;
        Set<String> testWords = new HashSet<>(Arrays.asList("Hello", "Bye"));

        int expectedCountEachWord = 10;

        CrawlingResult testCrawlingResult = new CrawlingResult(testURL, testMaxDepth, testWords);
        CrawlingResult crawlResult = webCrawler.crawl(testCrawlingResult);
        Assertions.assertAll(
                () -> Assertions.assertNotNull(crawlResult),
                () -> Assertions.assertEquals(crawlResult.getURL(), testURL),
                () -> Assertions.assertEquals(crawlResult.getMaxDepth(), testMaxDepth),
                () -> Assertions.assertEquals(crawlResult.getWords(), testWords),
                () -> Assertions.assertEquals(1, crawlResult.getVisitedPages().size())
        );

        for (Map.Entry<String, Integer> entry : crawlResult.getCountWordsOnPages().entrySet()) {
            Assertions.assertEquals(expectedCountEachWord, entry.getValue());
        }

    }
}
