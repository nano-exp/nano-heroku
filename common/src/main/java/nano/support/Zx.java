package nano.support;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class Zx {

    public static @NotNull CompletableFuture<byte @NotNull []> $(@NotNull String @NotNull ... command) {
        try {
            var process = new ProcessBuilder().command(command).redirectErrorStream(true).start();
            return process.onExit().thenApply(it -> readAllBytes(it::getInputStream));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static byte @NotNull [] readAllBytes(@NotNull Supplier<@NotNull InputStream> inputStreamSupplier) {
        try (var inputStream = inputStreamSupplier.get()) {
            return inputStream.readAllBytes();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
