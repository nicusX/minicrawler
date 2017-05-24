package it.nicus.samples.minicrawler;

import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class CrawlerTest {

    @Test
    public void crawlZeroLevel() {
        String websitePageUri = "https://en.wikipedia.org/wiki/Main_Page";
        String pageResourcePath = "/wikipedia_page.html";

        DocumentProvider getUriDocumentProvider = new FixedResourceDocumentProvider(pageResourcePath, websitePageUri);

        SiteMapEntry siteMap = new Crawler(websitePageUri, getUriDocumentProvider).crawl(0);

        assertThat(siteMap, instanceOf(SiteMapEntry.Page.class));

        Set<SiteMapEntry> children = ((SiteMapEntry.Page) siteMap).getChildren();
        assertThat(children, hasSize(570));
    }

}
