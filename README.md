# Minimalistic web crawler

Minimalistic web site crawler.

Starting from a specified URI, crawls the website recursively, down to a maximum number of levels.

External links are listed, but not crawled in.
External links are defined as links to domains that are neither the same domain
nor a subdomain of the initial URI.

The crawler doesn't get captured in circular links path, as each web page is never crawled twice.

At the end of the process, a simple map is printed to *stdout*.

## How to build and run

Requires Java 8

To build the project (Linux, OS X)
```
./gradlew build
```

To run the crawler against a website:
```
./gradlew run -Pargs="<site-url> <crawling-levels>"
```
Where:
* `site-url`: is the URI of the page to start crawling from.
* `crawling-levels`: maximum number of nested pages the process will crawl in. Use `0` to scan the targeted page only.

Example: crawl from wikipedia home page down to one level.
```
./gradlew run -Pargs="https://en.wikipedia.org/wiki/Main_Page 1"
```


## Known simplifications and room for improvement

The crawling process in single-threaded and not very fast.
Some form of parallelisation may be introduced relatively easy,
but care must be taken as a single mutable collection of visited page is
shared across all scraping processes (must be synchronised).

Only `<img>` tags are currently detected as static resources. No CSS, JS, nor images included in CSS.
Mapping different types of static resources is an easy addition.

Unit tests are poor as they hit the network: they are not real unit tests.
A small refactoring would be required to externalise the http fetching process
from `PageScraper` class, enabling proper testing based on static resources saved
as project test data.

Command line parameters are not validated. Particularly, the crawling nesting level should be
limited to a reasonable maximum.

The implementation is not memory efficient.
HTML scraping uses https://jsoup.org/ that parses in memory the entire DOM of the page.
This is not very efficient and memory consuming. Also, the current implementation doesn't drop
the DOM document once used to extract all links and static resources.

