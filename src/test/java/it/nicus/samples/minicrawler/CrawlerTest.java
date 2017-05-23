package it.nicus.samples.minicrawler;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class CrawlerTest {

    @Test
    public void crawlZeroLevel() {
        String baseUri = "https://en.wikipedia.org/wiki/Main_Page";

        SiteMapEntry siteMap = new Crawler(baseUri).crawl(0);

        assertThat( siteMap, instanceOf( SiteMapEntry.Page.class ) );

        assertThat( ((SiteMapEntry.Page)siteMap).getChildren(), not(empty()));
    }

    @Test
    public void crawlOneLevel() {
        String baseUri = "https://en.wikipedia.org/wiki/Main_Page";

        SiteMapEntry siteMap = new Crawler(baseUri).crawl(1);

        assertThat( siteMap, instanceOf( SiteMapEntry.Page.class ) );

        assertThat( ((SiteMapEntry.Page)siteMap).getChildren(), not(empty()));
    }
}
