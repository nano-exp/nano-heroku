package nano.support;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OnionTests {

    @Test
    public void testCompose() throws Exception {
        Onion.Middleware<List<String>> m0 = (ctx, nxt) -> {
            assertEquals(0, ctx.size());
            ctx.add("0");
            nxt.next();
        };
        Onion.Middleware<List<String>> m1 = (ctx, nxt) -> {
            assertEquals(ctx.get(0), "0");
            ctx.add("1");
            nxt.next();
        };
        Onion.Middleware<List<String>> m2 = (ctx, nxt) -> {
            assertEquals(ctx.get(0), "0");
            assertEquals(ctx.get(1), "1");
            nxt.next();
        };

        var onion = new Onion<List<String>>();
        onion.use(m0);
        onion.use(m1);
        onion.use(m2);
        onion.handle(new ArrayList<>());
    }
}
