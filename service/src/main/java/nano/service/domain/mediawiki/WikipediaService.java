package nano.service.domain.mediawiki;

import org.springframework.stereotype.Service;

/**
 * Wikipedia
 *
 * @author cbdyzj
 * @see 2020.8.28
 */
@Service
public class WikipediaService {

    private static final String QUERY_URL = "https://%s.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exlimit=1&explaintext=true&exintro=true&redirects=true&titles=%s";
    private static final String PAGE_URL = "https://%s.m.wikipedia.org/wiki/%s";

    private final MediaWikiFetch mediaWikiFetch = MediaWikiFetch.create(QUERY_URL, PAGE_URL);

    public String fetchWiki(String title, String language) {
        return this.mediaWikiFetch.fetch(title, language);
    }
}
