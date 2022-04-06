package nano.support;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static nano.support.Sugar.render;
import static org.junit.jupiter.api.Assertions.*;

public class SugarTests {

    @Test
    public void testRender() {
        assertEquals("hello world", render("hello ${name}", Map.of("name", "world")));
        assertEquals("1-2-3", render("${a}-${b}-${c}", Map.of("a", 1, "b", 2, "c", 3)));
        assertThrows(NullPointerException.class, () -> render("${name}", new HashMap<>()));
    }
}
