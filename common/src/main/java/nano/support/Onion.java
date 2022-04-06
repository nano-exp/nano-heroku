package nano.support;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * Onion is just like an Onion
 *
 * @param <T> Context
 * @author cbdyzj
 * @since 2018.3.23
 */
public class Onion<T> {

    private Middleware<T> core = (ctx, nxt) -> nxt.next();

    public final void use(@NotNull Middleware<T> middleware) {
        Objects.requireNonNull(middleware, "Middleware must be not null");
        this.core = compose(this.core, middleware);
    }

    public void handle(@NotNull T context) throws Exception {

        this.core.via(context, () -> {
        });
    }

    public interface Middleware<T> {

        void via(@NotNull T context, @NotNull Next next) throws Exception;
    }

    public interface Next {

        void next() throws Exception;
    }

    @SafeVarargs
    public static @NotNull <U> Middleware<U> compose(@NotNull Middleware<U> @NotNull ... middlewares) {
        return Arrays.stream(middlewares).reduce((ctx, nxt) -> nxt.next(), (before, after) -> (ctx, nxt) -> before.via(ctx, () -> after.via(ctx, nxt)));
    }
}
