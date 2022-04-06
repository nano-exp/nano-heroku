package nano.service.domain.scripting;

import org.graalvm.polyglot.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class Scripting {

    public @Nullable String eval(@NotNull String script) {
        var context = getContext();
        try (context) {
            var value = context.eval("js", script);
            return value != null ? value.asString() : "null";
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private static @NotNull Context getContext() {
        return Context.create("js");
    }
}
