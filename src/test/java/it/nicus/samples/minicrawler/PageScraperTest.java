package it.nicus.samples.minicrawler;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PageScraperTest {

    private static final String PAGE_RESOURCE_PATH = "/wikipedia_page.html";
    private static final String PAGE_URI = "https://en.wikipedia.org/wiki/Web_crawler";
    private static final String BASE_URI = "https://en.wikipedia.org/";
    private static final String BASE_HOST = "en.wikipedia.org";

    // Read a page from a fixed position in classpath, ignoring the provided URI
    private final Function<String, Optional<Document>> resourcePageParser = (dummy) -> {
        try {
            Path path = Paths.get(this.getClass().getResource(PAGE_RESOURCE_PATH).toURI());
            String content = new String(Files.readAllBytes(path));
            return Optional.of(Jsoup.parse(content, BASE_URI));
        } catch (IOException | URISyntaxException ex) {
            return Optional.empty();
        }
    };

    @Test
    public void allInternalLinks() {

        PageScraper scraper = PageScraper.fetchPage(PAGE_URI, BASE_URI, resourcePageParser);

        Set<String> links = scraper.allInternalLinks();

        assertThat(links, hasSize(429));
        assertThat(links, everyItem(hostEndsWith(BASE_HOST)));
    }

    @Test
    public void allExternalLinks() {
        PageScraper scraper = PageScraper.fetchPage(PAGE_URI, BASE_URI, resourcePageParser);

        Set<String> links = scraper.allExternalLinks();

        assertThat(links, hasSize(130));
        assertThat(links, everyItem(not(hostEndsWith(BASE_HOST))));
    }


    @Test
    public void allImages() {
        PageScraper scraper = PageScraper.fetchPage(PAGE_URI, BASE_URI, resourcePageParser);

        Set<String> images = scraper.allImages();

        assertThat(images, hasSize(11));
    }

    @Test
    public void emptyScraperOnInvalidPage() {
        Function<String, Optional<Document>> invalidPageParser = (dummy) -> Optional.empty();

        PageScraper scraper = PageScraper.fetchPage("http://foo", "foo", invalidPageParser);

        assertThat(scraper.allImages(), empty());
        assertThat(scraper.allExternalLinks(), empty());
        assertThat(scraper.allInternalLinks(), empty());
    }


    private Matcher<String> hostEndsWith(String baseHost) {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(final Object item) {
                final String uri = (String) item;
                return URI.create(uri).getHost().endsWith(baseHost);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("URI host should end with ").appendValue(baseHost);
            }
        };
    }
}