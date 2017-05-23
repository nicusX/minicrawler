package it.nicus.samples.minicrawler;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.net.URI;
import java.util.Set;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class PageScraperTest {

    private static final String PAGE = "https://en.wikipedia.org/wiki/Main_Page";
    private static final String BASE_HOST = "en.wikipedia.org";

    @Test
    public void findInternalLinks()  {
        PageScraper scraper = PageScraper.fetchPage(PAGE, BASE_HOST);

        Set<String> links = scraper.allInternalLinks();

        assertThat(links, not(empty()));
        assertThat(links, everyItem(hostEndsWith(BASE_HOST)));
    }

    @Test
    public void findExternalLinks() {
        PageScraper scraper = PageScraper.fetchPage(PAGE, BASE_HOST);

        Set<String> links = scraper.allExternalLinks();

        assertThat(links, not(empty()));
        assertThat(links, everyItem(not(hostEndsWith(BASE_HOST))));
    }


    @Test
    public void findImages() {
        PageScraper scraper = PageScraper.fetchPage(PAGE, BASE_HOST);

        Set<String> images = scraper.allImages();

        assertThat(images, not(empty()));
    }

    @Test
    public void invalidPage() {
        PageScraper scraper = PageScraper.fetchPage("http://foo", "foo");

        assertThat( scraper.allImages(), empty());
        assertThat( scraper.allExternalLinks(), empty());
        assertThat( scraper.allInternalLinks(), empty());
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

    private static boolean hostEndWith(String uri, String baseHost) {
        return URI.create(uri).getHost().endsWith(baseHost);
    }
}