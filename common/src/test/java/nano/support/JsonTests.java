package nano.support;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTests {

    @Test
    public void testEncode() {
        var subJson = Json.encode(Map.of("foo", "bar"));
        var json = Json.encode(Map.of("baz", subJson));
        var expected = "{\"baz\":\"{\\\"foo\\\":\\\"bar\\\"}\"}";
        assertEquals(expected, json);
    }

    @Test
    public void testDecode() {
        var appleJson = """
                {
                    "color": "red",
                    "tastes": "weird",
                    "shape": "circular"
                }
                """;
        var apple = Json.decodeValue(appleJson, Apple.class);
        var expected = new Apple(null, null, "red", "weird");
        assertEquals(expected, apple);
    }
}
