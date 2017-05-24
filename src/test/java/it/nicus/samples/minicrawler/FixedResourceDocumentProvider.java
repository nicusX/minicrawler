package it.nicus.samples.minicrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Read a fixed page from classpath, regardless of provided source.
 * Parse as if the page is coming from a given website URI (required by JSoup to generate absolute links).
 */
public class FixedResourceDocumentProvider implements DocumentProvider {

    private final String pageResourcePath;
    private final String websitePageUri;

    public FixedResourceDocumentProvider(String pageResourcePath, String websitePageUri) {
        this.pageResourcePath = pageResourcePath;
        this.websitePageUri = websitePageUri;
    }

    @Override
    public Optional<Document> get(String source) {
        try {
            Path path = Paths.get(this.getClass().getResource(pageResourcePath).toURI());
            String content = new String(Files.readAllBytes(path));
            return Optional.of(Jsoup.parse(content, websitePageUri));
        } catch (IOException | URISyntaxException ex) {
            return Optional.empty();
        }
    }
}
