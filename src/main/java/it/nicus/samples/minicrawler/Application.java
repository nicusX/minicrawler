package it.nicus.samples.minicrawler;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Application entry point.</p>
 * <p>
 * <p>Expects two command line parameters:
 * <ol>
 * <li>initial page URI (default = https://en.wikipedia.org/wiki/Main_Page)</li>
 * <li>maximum number of nested levels (default = 1, 0 = single page only)</li>
 * </ol>
 * </p>
 */
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        if ( args.length < 1 ) {
            System.err.println("Parameters:\n\tstart URI\n\tmax nested crawling levels (default=1)");
            System.exit(1);
        }

        final String baseUrl = args[0];
        final int levels = (args.length > 1) ? NumberUtils.toInt(args[1], 1) : 1;

        LOGGER.info("Crawling from {} down to {} levels", baseUrl, levels);

        final Crawler crawler = new Crawler(baseUrl, new GetUriDocumentProvider());
        final SiteMapEntry.Page siteMap = crawler.crawl(levels);

        LOGGER.info("Crawling complete");

        SiteMapPrinter.printMap(siteMap);
    }
}
