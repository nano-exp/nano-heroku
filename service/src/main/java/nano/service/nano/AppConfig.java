package nano.service.nano;

import lombok.Builder;
import nano.service.nano.model.Bot;
import org.jetbrains.annotations.NotNull;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

public record AppConfig(
        String nanoApi,
        String nanoApiKey,
        String baiduTranslationAppId,
        String baiduTranslationSecretKey,
        Map<String, Bot> bots
) {

    @Builder
    public AppConfig {
    }

    public Bot getBot(String name) {
        return Objects.requireNonNull(this.bots.get(name));
    }

    public String withNanoApi(@NotNull String endpoint) {
        try {
            var base = new URL(this.nanoApi);
            return new URL(base, endpoint).toString();
        } catch (MalformedURLException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
