package nano.service.util;

import com.jayway.jsonpath.JsonPath;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public abstract class JsonPathModule {

    public static ReadContext parse(@NotNull Object object) {
        return JsonPath.parse(object)::read;
    }

    public static ReadContext parse(@NotNull InputStream inputStream) {
        return JsonPath.parse(inputStream)::read;
    }

    public static ReadContext parse(@NotNull String json) {
        return JsonPath.parse(json)::read;
    }

    public static <T> T read(@NotNull String json, @NotNull String jsonPath) {
        return JsonPath.read(json, jsonPath);
    }

    public static <T> T read(@NotNull Object object, @NotNull String jsonPath) {
        return JsonPath.read(object, jsonPath);
    }

    public static <T> T read(@NotNull InputStream inputStream, @NotNull String jsonPath) {
        try {
            return JsonPath.read(inputStream, jsonPath);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @FunctionalInterface
    public interface ReadContext {

        <T> T read(String jsonPath);
    }
}
