package it.nicus.samples.minicrawler;

import java.util.Collections;
import java.util.Set;

/**
 * An entry of the site map tree.
 */
public abstract class SiteMapEntry {
    private final String uri;

    public SiteMapEntry(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return uri.toString();
    }


    /**
     * A node of the site map representing a page.
     */
    public static class Page extends SiteMapEntry {
        private Set<SiteMapEntry> children;

        private Page(String uri, Set<SiteMapEntry> children) {
            super(uri);
            this.children = Collections.unmodifiableSet(children);
        }

        public Set<SiteMapEntry> getChildren() {
            return children;
        }
    }


    /**
     * A node of the site map representing an image
     */
    public static class Image extends SiteMapEntry {
        private Image(String uri) {
            super(uri);
        }
    }


    public static Page page(String uri, Set<SiteMapEntry> children) {
        return new Page(uri, children);
    }

    public static Page page(String uri) {
        return new Page(uri, Collections.emptySet());
    }

    public static Image image(String uri) {
        return new Image(uri);
    }
}
