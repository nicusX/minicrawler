package it.nicus.samples.minicrawler;

import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CrawlerIT {
    @Test
    public void crawlOneLevel() {
        String baseUri = "https://en.wikipedia.org/wiki/Main_Page";
        DocumentProvider getUriDocumentProvider = new GetUriDocumentProvider();

        SiteMapEntry siteMap = new Crawler(baseUri, getUriDocumentProvider).crawl(1);

        assertThat(siteMap, instanceOf(SiteMapEntry.Page.class));

        Set<SiteMapEntry> children = ((SiteMapEntry.Page) siteMap).getChildren();
        assertThat(children, not(empty()));

        // Some children should have children
        assertTrue(children.stream()
                .anyMatch(entry -> entry instanceof SiteMapEntry.Page
                        && !((SiteMapEntry.Page) entry).getChildren().isEmpty()));
    }

}
