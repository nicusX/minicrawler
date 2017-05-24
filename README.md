# Minimalistic web crawler

Minimalistic web site crawler (not for real use).

Starting from a specified URI, crawls the website recursively, down to a
maximum number of levels, and prints a map of links and static resources
(only images, at the moment).

The crawling is limited to internal pages only.
Internal pages are defined as links to a domain that is either the same or
a subdomain of the starting page.

Each page is crawled at most once, so the crawler doesn't get captured in
circular link paths.

Recursive crawling is parallelised, to improve performance.

At the end of the process, a simple map is printed to *stdout*, showing
links and static resources.

All links are printed as absolute, regardless how they appear in the page.

Internally, uses uses JSoup https://jsoup.org/ for html parsing.
Note that only static HTML is parsed.


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

Currently, only `<img>` tags are identified as static resources.
No CSS, JS, nor images included in CSS.
Adding additional static resource types, directly referenced by the html page,
would not be hard.

The implementation is not memory efficient.
JSoup and parses the entire DOM of the page and DOMs
(`Document`) of all page are retained in memory.
An easy change should be dropping the parsed `Document` before crawling
into children pages.
JSoup might be probably replaced with something more efficient, possibly
just searching links and resources using Regexp, but managing relative
links would be a bit more complex (JSoup automatically makes links absolute).

A single Integration Test, hitting the network, currently runs with unit tests.
It should be run as a separate task (not a big issue, though, as it takes 2-3 sec).

The site map printout is very minimalistic.