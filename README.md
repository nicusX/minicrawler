# Minimalistic web crawler

Minimalistic web site crawler.

Starting from a specified URI, crawls the website recursively, down to a
maximum number of levels.

The site maps identifies internal and external links and static resources
(only images, at the moment).

The crawling is limited to internal links only.
Internal links are defined as links to a domain that is either the same or
a subdomain of the initial page.

Each page is crawled at most once, so the crawler doesn't get captured in
circular link paths.

Recursive crawling is parallelised, to improve performance.

At the end of the process, a simple map is printed to *stdout*.

## How to build and run

Requires Java 8. Gradle is embedded so not required.

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

Only `<img>` tags are currently detected as static resources.
No CSS, JS, nor images included in CSS.
Identifying other static resource types is not hard.

The implementation is not memory efficient.
HTML scraping uses https://jsoup.org/ and parses the entire DOM of each page.
Also, the current implementation retains in memory the DOM of each page.
Some refactoring would allow to drop it as soon as all elements to be mapped
are extracted.

A single Integration Test, hitting the network, currently runs with unit tests.
It should be run as a separate task (not a big issue, though, as it takes 2-3 sec).

The site map printout is very minimalistic.