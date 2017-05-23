# Minimalistic web crawler

This minimalistic web crawler starts from a given URI and crawl the website recursively.

The maximum level of recursion is limited.

External links are listed, but not crawled in.

External links are defined as links to domains that are neither the same domain
nor a subdomain of the initial URI.

## How to build and run

TODO

```
gradlew run -Pargs="<base-url> <crawling-levels>"
```

## Known simplifications and room for improvement

* Unit tests are poor and nor real "unit" test: they hit the network and fetch wikipedia pages. Html page fetching should be externalised form PageScraper
* The process is single-threaded, so not very fast. Crawling in might be easily parallelised, but beware of synchronising the shared, mutable set of visited pages.
* Only `<img>` static resources are detected, no css, js etc. Also, any image in CSS is ignored.
* HTML parsing uses https://jsoup.org/ that parses the entire DOM; it is probably an overkill just to extract links and images, irrespective of the DOM structure.
