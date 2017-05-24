package it.nicus.samples.minicrawler;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PageScraperTest {

    private static final String PAGE_RESOURCE_PATH = "/wikipedia_page.html";
    private static final String PAGE_URI = "https://en.wikipedia.org/wiki/Web_crawler";
    private static final String BASE_URI = "https://en.wikipedia.org/";
    private static final String BASE_HOST = "en.wikipedia.org";

    private final DocumentProvider resourcePageDocumentProvider = new FixedResourceDocumentProvider(PAGE_RESOURCE_PATH, BASE_URI);

    @Test
    public void allInternalLinks() {

        PageScraper scraper = PageScraper.scrape(PAGE_URI, BASE_URI, resourcePageDocumentProvider);

        Set<String> links = scraper.allInternalLinks();

        assertThat(links, hasSize(429));
        assertThat(links, everyItem(hostEndsWith(BASE_HOST)));
    }

    @Test
    public void allExternalLinks() {
        PageScraper scraper = PageScraper.scrape(PAGE_URI, BASE_URI, resourcePageDocumentProvider);

        Set<String> links = scraper.allExternalLinks();

        assertThat(links, hasSize(130));
        assertThat(links, everyItem(not(hostEndsWith(BASE_HOST))));
    }


    @Test
    public void allImages() {
        PageScraper scraper = PageScraper.scrape(PAGE_URI, BASE_URI, resourcePageDocumentProvider);

        Set<String> images = scraper.allImages();

        assertThat(images, hasSize(11));
    }

    @Test
    public void emptyScraperOnInvalidPage() {
        DocumentProvider invalidPageDocumentProvider = (dummy) -> Optional.empty();

        PageScraper scraper = PageScraper.scrape("http://foo", "foo", invalidPageDocumentProvider);

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