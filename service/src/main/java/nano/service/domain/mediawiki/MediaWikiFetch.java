package nano.service.domain.mediawiki;

import nano.support.http.Fetch;
import nano.service.util.JsonPathModule;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.net.URLEncoder;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public interface MediaWikiFetch {

    String fetch(String title, String language);

    static MediaWikiFetch create(String queryUrl, String pageUrl) {
        return (title, language) -> {
            var encodedTitle = URLEncoder.encode(title, UTF_8);
            return fetchWiki(
                    queryUrl.formatted(language, encodedTitle),
                    pageUrl.formatted(language, encodedTitle)
            );
        };
    }

    private static String fetchWiki(String url, String pageUrl) {
        var body = Fetch.fetchString(url);
        if (ObjectUtils.isEmpty(body)) {
            return null;
        }
        var extractList = JsonPathModule.<List<String>>read(body, "$.query.pages.*.extract");
        if (CollectionUtils.isEmpty(extractList)) {
            return null;
        }
        var extract = extractList.get(0);
        if (ObjectUtils.isEmpty(extract)) {
            return pageUrl;
        }
        return extract + "\n" + pageUrl;
    }
}
