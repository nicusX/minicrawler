package it.nicus.samples.minicrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class Crawler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    private final String baseUri;
    private final String baseHost;
    private final Set<String> visited = new HashSet<>(); // This is mutated! No synchronisation problem as long as we are single-threaded


    public static void main(String[] args) {
        // TODO Validate command line args

        final String baseUrl = (args.length>0) ? args[0] : "https://en.wikipedia.org/wiki/Main_Page";

        final int levels = ( args.length > 1 ) ? (Integer.parseInt(args[1])) : 1;

        LOGGER.info("Crawling from {} down to {} levels", baseUrl, levels);

        final Crawler crawler = new Crawler(baseUrl);
        final SiteMapEntry.Page siteMap = crawler.crawl(levels);

        LOGGER.info("Crawling complete");

        SiteMapPrinter.printMap(siteMap);
    }


    public Crawler(final String baseUri) {
        this.baseHost = URI.create(baseUri).getHost();
        this.baseUri = baseUri;
    }

    public SiteMapEntry.Page crawl(final int maxLevels) {
        return crawlIn(baseUri, maxLevels);
    }

    private SiteMapEntry.Page crawlIn(final String uri, final int maxDeeperLevels)  {

        LOGGER.info("Reading {}", uri);

        // Fetch and parse the URI
        final PageScraper scraper = PageScraper.fetchPage(uri, baseHost);

        // Find all images
        final Set<String> images = scraper.allImages();
        LOGGER.info("Found {} images", images.size());
        final Set<SiteMapEntry> imgesEntries = images.stream()
                .map( imgSrc -> new SiteMapEntry.Image(imgSrc) )
                .collect(Collectors.toSet());


        // Find all external links
        final Set<String> externalLinks = scraper.allExternalLinks();
        LOGGER.info("Found {} external links", externalLinks.size());
        final Set<SiteMapEntry> externalLinkEntries = externalLinks.stream()
                .map( link -> new SiteMapEntry.Page(link) )
                .collect(Collectors.toSet());


        // Find all internal links
        final Set<String> internalLinks = scraper.allInternalLinks();
        LOGGER.info("Found {} internal links", internalLinks.size());
        final Set<SiteMapEntry> internalLinkEntries = internalLinks.stream()
                .map( link-> crawlDeeperUnvisitedLinks(link, maxDeeperLevels))
                .collect(Collectors.toSet());


        final Set<SiteMapEntry> children = new HashSet<>();
        children.addAll(imgesEntries);
        children.addAll(externalLinkEntries);
        children.addAll(internalLinkEntries);

        return new SiteMapEntry.Page(uri, children);
    }



    private SiteMapEntry crawlDeeperUnvisitedLinks(final String uri, final int maxDeeperLevels) {

        if( visited.contains(uri)) {
            LOGGER.debug("Page {} already visited", uri);
            return new SiteMapEntry.Page(uri);
        } else if ( maxDeeperLevels <= 0 ) {
            LOGGER.debug("Not crawling deeper");
            return new SiteMapEntry.Page(uri);
        } else {
            LOGGER.debug("New page found {}", uri);

            // Mark page as visited
            visited.add(uri);

            return crawlIn(uri, maxDeeperLevels - 1);
        }
    }
}
