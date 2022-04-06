package nano.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArgumentsTests {

    @Test
    public void testGet() {
        var bytes = new byte[1];
        var args = new Object[]{1, "a", false, bytes};
        var arguments = new Arguments(args);
        assertEquals(1, arguments.get(0, Integer.class));
        assertEquals("a", arguments.get(1, String.class));
        assertEquals(false, arguments.get(2, Boolean.class));
        assertEquals(bytes, arguments.get(3, byte[].class));
    }
}
