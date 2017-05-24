package it.nicus.samples.minicrawler;

import org.jsoup.nodes.Document;

import java.util.Optional;

/**
 * Function providing a parsed JSoup Document from a specified source, if a valid document.
 */
@FunctionalInterface
public interface DocumentProvider {

    Optional<Document> get(String source);
}
