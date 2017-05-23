package it.nicus.samples.minicrawler;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An entry of the site map
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
     * A Page of the site
     */
    public static class Page extends SiteMapEntry {
        private Set<SiteMapEntry> children;

        public Page(String uri, Set<SiteMapEntry> children) {
            super(uri);
            this.children = Collections.unmodifiableSet(children);
        }

        public Page(String uri) {
            super(uri);
            this.children = Collections.emptySet();
        }


        public Set<SiteMapEntry> getChildren() {
            return children;
        }
    }


    /**
     * A static resource: Image
     */
    public static class Image extends SiteMapEntry {
        public Image(String uri) {
            super(uri);
        }
    }


}
