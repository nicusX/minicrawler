package it.nicus.samples.minicrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;


/**
 * Implementation of DocumentProvider, GETing the html content from an URI.
 * Any error returns as empty.
 */
public class GetUriDocumentProvider implements DocumentProvider  {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetUriDocumentProvider.class);

    @Override
    public Optional<Document> get(String uri) {
        try {
            LOGGER.trace("Fetching: {}", uri);
            return Optional.of(Jsoup.connect(uri).get());
        } catch (IOException exc) {
            LOGGER.warn("Error fetching {}: {}", uri, exc.getMessage());
            return Optional.empty();
        }
    }
}
