package nano.service.domain.baidu;

import nano.service.nano.AppConfig;
import nano.service.util.JsonPathModule;
import nano.support.http.Fetch;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Translation service
 *
 * @author cbdyzj
 * @since 2020.8.17
 */
@Service
public class TranslationService {

    private static final String TRANSLATION_API = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    private final AppConfig appConfig;

    public TranslationService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public String translate(String input, String from, String to) {
        var appId = this.appConfig.baiduTranslationAppId();
        var secretKey = this.appConfig.baiduTranslationSecretKey();
        var salt = Instant.now().toString();
        var data = (appId + input + salt + secretKey).getBytes(UTF_8);
        var sign = DigestUtils.md5DigestAsHex(data);

        var publisher = Fetch.newFormBodyPublisher();
        publisher.append("q", input);
        publisher.append("appid", appId);
        publisher.append("salt", salt);
        publisher.append("from", from);
        publisher.append("to", to);
        publisher.append("sign", sign);
        var url = URI.create(TRANSLATION_API);
        var request = HttpRequest.newBuilder(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(publisher.build())
                .build();
        var response = Fetch.fetch(request);
        return buildTranslateResult(response.body());
    }

    private static String buildTranslateResult(@NotNull InputStream stream) {
        if (ObjectUtils.isEmpty(stream)) {
            return "The translation result is empty";
        }
        var context = JsonPathModule.parse(stream);
        var result = context.<List<Map<String, String>>>read("$.trans_result");
        if (ObjectUtils.isEmpty(result)) {
            return "Translation error: " + context.read("$.error_msg");
        }
        return result.stream().map(it -> it.get("dst")).collect(Collectors.joining("\n"));
    }
}
