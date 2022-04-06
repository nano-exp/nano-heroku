package nano.service.domain.baidu;

import nano.support.http.Fetch;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.net.URLEncoder;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Baidu encyclopedia service
 *
 * @author cbdyzj
 * @since 2020.8.28
 */
@Service
public class BaiduWikiService {

    private static final Pattern pattern = Pattern.compile("<meta name=\"description\" content=\"(?<desc>.+)\">");

    private static final String QUERY_URL = "https://baike.baidu.com/item/%s";

    public String fetchWiki(String title) {
        String encodeTitle = URLEncoder.encode(title, UTF_8);
        var body = Fetch.fetchString(QUERY_URL.formatted(encodeTitle));
        if (ObjectUtils.isEmpty(body)) {
            return null;
        }
        var m = pattern.matcher(body);
        if (!m.find()) {
            return null;
        }
        var extract = m.group("desc");
        if (ObjectUtils.isEmpty(extract)) {
            return null;
        }
        return extract + "\n" + QUERY_URL.formatted(encodeTitle);
    }
}
