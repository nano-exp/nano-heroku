package nano.support;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.net.URLConnection.getFileNameMap;
import static nano.support.Sugar.findFirst;

public abstract class MoreContentType {

    private final static String DEFAULT_CONTENT_TYPE = "application/octet‑stream";

    private static @NotNull Predicate<String> compilePredicate(@NotNull String regex) {
        return Pattern.compile(regex).asPredicate();
    }

    private static final Map<Predicate<String>, String> CONTENT_TYPE_MAP = Map.of(
            compilePredicate("\\.(?i)webp$"), "image/webp",
            compilePredicate("\\.(?i)webm"), "video/webm"
    );

    public static @NotNull String getContentType(String s) {
        if (s == null) {
            return DEFAULT_CONTENT_TYPE;
        }
        var contentType = getFileNameMap().getContentTypeFor(s);
        if (contentType != null) {
            return contentType;
        }
        var find = findFirst(CONTENT_TYPE_MAP.entrySet(), it -> it.getKey().test(s));
        if (find != null) {
            return find.getValue();
        }
        return DEFAULT_CONTENT_TYPE;
    }
}
