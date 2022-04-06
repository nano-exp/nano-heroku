package nano.service.util;

import ua_parser.Parser;

import java.util.Objects;

public abstract class UserAgentParserModule {

    private static final Parser parser = new Parser();

    public static String parseToString(String s) {
        return Objects.requireNonNull(parser.parse(s)).toString();
    }
}
