package it.nicus.samples.minicrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

/**
 * GET html content from an URI and parse to JSoup Document.
 *
 * On any error returns an empty.
 */
public class GetUriDocumentParser implements Function<String, Optional<Document>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetUriDocumentParser.class);

    @Override
    public Optional<Document> apply(String uri) {
        try {
            LOGGER.trace("Fetching: {}", uri);
            return Optional.of(Jsoup.connect(uri).get());
        } catch (IOException exc) {
            LOGGER.warn("Error fetching {}: {}", uri, exc.getMessage());
            return Optional.empty();
        }
    }
}
