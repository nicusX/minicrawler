package it.nicus.samples.minicrawler;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Scrape a page at a specified URI, identifying images, internal and external links
 */
public class PageScraper {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageScraper.class);

    private Document doc;
    private String baseHost;

    private PageScraper(final Document doc, final String baseHost) {
        this.doc = doc;
        this.baseHost = baseHost;
    }


    /**
     * Scrape a page, provided by a documentProvider.
     *
     * @param uri              URI of the page
     * @param baseUri          base URI of the scraped website. Used to identify local pages.
     * @param documentProvider function providing a Document containing the parsed html page, if any
     * @return a new instance of PageScraper
     */
    public static PageScraper scrape(final String uri, final String baseUri, final DocumentProvider documentProvider) {
        LOGGER.info("Fetching: {}", uri);

        String baseHost = URI.create(baseUri).getHost();

        return documentProvider.get(uri)
                .map(document -> new PageScraper(document, baseHost))
                .orElse(new InvalidPageScraper());
    }

    private static Set<String> extractElementAttributes(final Document doc, final String cssQuery, final String attrName) {
        return doc.select(cssQuery)
                .stream()
                .map(element -> element.attr(attrName))
                .filter(src -> !src.isEmpty())
                .collect(Collectors.toSet());
    }

    /**
     * Set of images found in the page,
     * as <code>img</code> tags.
     *
     * @return Set of image src URI
     */
    public Set<String> allImages() {
        return extractElementAttributes(doc, "img[src]", "abs:src");
    }

    private Stream<String> allLinksStream() {
        return doc.select("a[href]")
                .stream()
                .map(element -> element.attr("abs:href")) // transforms URI into absolute
                .filter(href -> !href.isEmpty());
    }

    /**
     * Set of internal links found in the page,
     * defined as links with host equal to baseUri host
     *
     * @return Set of link URI
     */
    public Set<String> allInternalLinks() {
        return allLinksStream()
                .filter(uri -> isInternalLink(uri)).collect(Collectors.toSet());
    }

    /**
     * Set of external links found in the page,
     * defined as links with host different from baseUri host
     *
     * @return Set of link URI
     */
    public Set<String> allExternalLinks() {
        return allLinksStream()
                .filter(uri -> !isInternalLink(uri)).collect(Collectors.toSet());
    }

    private boolean isInternalLink(final String uriStr) {
        try {
            URI uri = URI.create(uriStr);
            return (uri.getScheme().equals("http") || uri.getScheme().equals("https"))
                    && uri.getHost().endsWith(baseHost);
        } catch (IllegalArgumentException iae) {
            LOGGER.warn("Invalid URI: {}", uriStr);
            return false;
        }
    }

    /**
     * Scraper for an invalid page: empty
     */
    public static class InvalidPageScraper extends PageScraper {
        private InvalidPageScraper() {
            super(null, null);
        }

        @Override
        public Set<String> allInternalLinks() {
            return Collections.emptySet();
        }

        @Override
        public Set<String> allExternalLinks() {
            return Collections.emptySet();
        }

        @Override
        public Set<String> allImages() {
            return Collections.emptySet();
        }
    }
}
