package nano.support;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public abstract class LanguageUtils {

    private static final Predicate<String> CHINESE = Pattern.compile("[\u4e00-\u9fa5]").asPredicate();
    private static final Predicate<String> RUSSIAN = Pattern.compile("[\u0400-\u04ff]").asPredicate();

    public static boolean containsChinese(String text) {
        return CHINESE.test(text);
    }

    public static boolean containsRussian(String text) {
        return RUSSIAN.test(text);
    }

}
