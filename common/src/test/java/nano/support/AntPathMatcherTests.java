package nano.support;

import org.junit.jupiter.api.Test;
import org.springframework.util.AntPathMatcher;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AntPathMatcherTests {

    @Test
    public void testMatch() {
        var matcher = new AntPathMatcher();
        var variables = matcher.extractUriTemplateVariables("/hello/{name}", "/hello/world");
        assertEquals(Map.of("name", "world"), variables);
        assertTrue(matcher.match("/**/*.jsx", "/hello/world.jsx"));
        assertTrue(matcher.match("/**/*.jsx", "/nano.jsx"));
    }
}
