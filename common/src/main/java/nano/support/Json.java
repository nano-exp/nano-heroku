package nano.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class Json {

    private static final TypeReference<Map<String, ?>> STRING_OBJECT_MAP_TYPE = new TypeReference<>() {
    };
    private static final TypeReference<List<?>> OBJECT_LIST_TYPE = new TypeReference<>() {
    };

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var module = new SimpleModule();
        module.addSerializer(Instant.class, new InstantSerializer());
        module.addSerializer(Date.class, new DateSerializer());
        module.addSerializer(byte[].class, new ByteArraySerializer());
        mapper.registerModule(module);
    }

    public static String encode(Object obj) {
        return invoke(() -> mapper.writeValueAsString(obj));
    }

    public static ByteBuffer encodeToBuffer(Object obj) {
        return invoke(() -> ByteBuffer.wrap((mapper.writeValueAsBytes(obj))));
    }

    public static <T> T decodeValue(String str, Class<T> clazz) {
        return invoke(() -> mapper.readValue(str, clazz));
    }

    public static <T> T decodeValue(InputStream stream, Class<T> clazz) {
        return invoke(() -> mapper.readValue(stream, clazz));
    }

    public static <T> T decodeValue(String str, TypeReference<T> type) {
        return invoke(() -> mapper.readValue(str, type));
    }

    public static <T> T decodeValue(ByteBuffer buf, TypeReference<T> type) {
        return invoke(() -> mapper.readValue(buf.array(), type));
    }

    public static <T> T decodeValue(InputStream stream, TypeReference<T> type) {
        return invoke(() -> mapper.readValue(stream, type));
    }

    public static <T> T decodeValue(ByteBuffer buf, Class<T> clazz) {
        return invoke(() -> mapper.readValue(buf.array(), clazz));
    }

    public static Map<String, ?> decodeValueAsMap(String str) {
        return decodeValue(str, STRING_OBJECT_MAP_TYPE);
    }

    public static List<?> decodeValueAsList(String str) {
        return decodeValue(str, OBJECT_LIST_TYPE);
    }

    public static Map<String, ?> decodeValueAsMap(InputStream stream) {
        return decodeValue(stream, STRING_OBJECT_MAP_TYPE);
    }

    public static List<?> decodeValueAsList(InputStream stream) {
        return decodeValue(stream, OBJECT_LIST_TYPE);
    }

    public static Map<String, ?> decodeValueAsMap(ByteBuffer buf) {
        return decodeValue(buf, STRING_OBJECT_MAP_TYPE);
    }

    public static List<?> decodeValueAsList(ByteBuffer buf) {
        return decodeValue(buf, OBJECT_LIST_TYPE);
    }

    public static <T> T convertValue(Object obj, Class<T> clazz) {
        return invoke(() -> mapper.convertValue(obj, clazz));
    }

    public static <T> T convertValue(Object obj, TypeReference<T> type) {
        return invoke(() -> mapper.convertValue(obj, type));
    }

    private static <T> T invoke(ActionThrowsIOException<T> action) {
        try {
            return action.invoke();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    interface ActionThrowsIOException<T> {

        T invoke() throws IOException;
    }

    private static class InstantSerializer extends JsonSerializer<Instant> {

        @Override
        public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(DateTimeFormatter.ISO_INSTANT.format(value));
        }
    }

    private static class DateSerializer extends JsonSerializer<Date> {

        @Override
        public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(DateTimeFormatter.ISO_INSTANT.format(value.toInstant()));
        }
    }

    private static class ByteArraySerializer extends JsonSerializer<byte[]> {

        private final Base64.Encoder BASE64 = Base64.getEncoder();

        @Override
        public void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(BASE64.encodeToString(value));
        }
    }
}
