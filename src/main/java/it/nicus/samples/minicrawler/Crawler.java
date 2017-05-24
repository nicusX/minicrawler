package it.nicus.samples.minicrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Recursively crawl local links, down to max level, and produce a site map.
 */
public class Crawler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    private final String baseUri;
    private final Set<String> visited = Collections.synchronizedSet(new HashSet<>()); // This is mutated! Synchronised to parallelise crawling
    private final DocumentProvider documentProvider;

    /**
     * <p>Create the crawler from a starting URI.</p>
     * <p>It will use the DocumentProvider to actually fetch and parse the pages.</p>
     * <p>Does not start the crawling process</p>
     *
     * @param baseUri          starting URI
     * @param documentProvider DocumentProvider to provide the JSoup Document for the specified URI
     */
    public Crawler(final String baseUri, final DocumentProvider documentProvider) {
        this.baseUri = baseUri;
        this.documentProvider = documentProvider;
    }

    /**
     * Starts the crawling process, down to a maximum level of nested pages.
     *
     * @param maxLevels maximum level of nested local pages to crawl in.
     * @return the resulting site map.
     */
    public SiteMapEntry.Page crawl(final int maxLevels) {
        return crawlIn(baseUri, maxLevels);
    }

    private SiteMapEntry.Page crawlIn(final String uri, final int maxDeeperLevels) {

        LOGGER.info("Reading {}", uri);

        // Fetch and parse the URI
        final PageScraper scraper = PageScraper.scrape(uri, baseUri, documentProvider);

        // Find all images
        final Set<String> images = scraper.allImages();
        LOGGER.info("Found {} images", images.size());
        final Set<SiteMapEntry> imgesEntries = images.stream()
                .map(imgSrc -> SiteMapEntry.image(imgSrc))
                .collect(Collectors.toSet());


        // Find all external links
        final Set<String> externalLinks = scraper.allExternalLinks();
        LOGGER.info("Found {} external links", externalLinks.size());
        final Set<SiteMapEntry> externalLinkEntries = externalLinks.stream()
                .map(link -> SiteMapEntry.page(link))
                .collect(Collectors.toSet());


        // Find all internal links
        final Set<String> internalLinks = scraper.allInternalLinks();
        LOGGER.info("Found {} internal links", internalLinks.size());
        final Set<SiteMapEntry> internalLinkEntries = internalLinks.parallelStream() // crawling is parallelised
                .map(link -> crawlDeeperUnvisitedLinks(link, maxDeeperLevels))
                .collect(Collectors.toSet());


        final Set<SiteMapEntry> children = new HashSet<>();
        children.addAll(imgesEntries);
        children.addAll(externalLinkEntries);
        children.addAll(internalLinkEntries);

        return SiteMapEntry.page(uri, children);
    }


    private SiteMapEntry crawlDeeperUnvisitedLinks(final String uri, final int maxDeeperLevels) {

        if (visited.contains(uri)) {
            LOGGER.debug("Page {} already visited", uri);
            return SiteMapEntry.page(uri);
        } else if (maxDeeperLevels <= 0) {
            LOGGER.debug("Not crawling deeper");
            return SiteMapEntry.page(uri);
        } else {
            LOGGER.debug("New page found {}", uri);

            // Mark page as visited
            visited.add(uri);

            return crawlIn(uri, maxDeeperLevels - 1);
        }
    }
}
