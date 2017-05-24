package it.nicus.samples.minicrawler;

import org.apache.commons.lang3.StringUtils;

/**
 * Print the site map to stdout
 */
public class SiteMapPrinter {

    public static void printMap(SiteMapEntry.Page siteMap) {
        printPage(siteMap, 0);
    }

    private static void printPage(SiteMapEntry.Page page, int level) {
        System.out.println(padding(level) + "Link: " + page.getUri());

        page.getChildren().forEach(child -> printChild(child, level));
    }

    private static void printResource(SiteMapEntry resource, int level) {
        System.out.println(padding(level) + "Resource: " + resource.getUri());
    }

    private static void printChild(SiteMapEntry child, int level) {
        if (child instanceof SiteMapEntry.Page) {
            printPage((SiteMapEntry.Page) child, level + 1);
        } else {
            printResource(child, level + 1);
        }
    }

    private static String padding(int pad) {
        return (pad > 0) ? (StringUtils.repeat("    ", pad) + "+->") : "";
    }
}
