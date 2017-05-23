package it.nicus.samples.minicrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PageScraper {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageScraper.class);


    private Document doc;
    private String baseHost;

    private PageScraper(final Document doc, final String baseHost) {
        this.doc = doc;
        this.baseHost = baseHost;
    }


    public static PageScraper fetchPage(final String uri, final String baseHost)  {
        LOGGER.info("Fetching: {}", uri);
        try {
            return new PageScraper(Jsoup.connect(uri).get(), baseHost);
        } catch (IOException ioe) {
            LOGGER.warn("Unable to retrieve page: {}", uri);
            return new EmptyPageScraper();
        }
    }

    public Set<String> allImages() {
        return extractElementAttributes(doc, "img[src]", "abs:src");
    }


    private Stream<String> allLinksStream() {
        return doc.select("a[href]")
                .stream()
                .map( element -> element.attr("abs:href"))
                .filter( href -> !href.isEmpty());
    }

    public Set<String> allInternalLinks() {
        return allLinksStream()
                .filter( uri -> isInternal(uri) ).collect(Collectors.toSet());
    }

    public Set<String> allExternalLinks() {
        // TODO Not very efficient scrolling all links twice
        return allLinksStream()
                .filter( uri -> !isInternal(uri) ).collect(Collectors.toSet());
    }

    private  boolean isInternal(final String uriStr) {
        try {
            URI uri = URI.create(uriStr);
            return ( uri.getScheme().equals("http") ||  uri.getScheme().equals("https") )
                    && uri.getHost().endsWith(baseHost);
        } catch (IllegalArgumentException iae) {
            LOGGER.warn("Invalid URI: {}", uriStr);
            return false;
        }
    }



    private static Set<String> extractElementAttributes(final Document doc, final String cssQuery, final String attrName) {
        return doc.select(cssQuery)
                .stream()
                .map( element -> element.attr(attrName))
                .filter( src -> !src.isEmpty())
                .collect(Collectors.toSet());
    }

    public static class EmptyPageScraper extends PageScraper {
        private EmptyPageScraper() {
            super(null,null);
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
