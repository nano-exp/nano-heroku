package nano.support.http;

import nano.support.Iterables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ByteArrayIteratorsTests {

    @Test
    public void test() {
        var strings = Stream.generate(ThreadLocalRandom.current()::nextInt).limit(3).map(String::valueOf).toArray(String[]::new);
        var bytes = Iterables.compose(
                List.of(strings[0].getBytes(UTF_8)),
                new ByteArrayIterable(() -> new ByteArrayInputStream(strings[1].getBytes(UTF_8))),
                Iterables.map(List.of(strings[2]), it -> List.of(it.getBytes(UTF_8)))
        );
        var stream = new ByteArrayOutputStream();
        for (var it : bytes) {
            stream.writeBytes(it);
        }
        Assertions.assertEquals(String.join("", strings), stream.toString(UTF_8));
    }
}
