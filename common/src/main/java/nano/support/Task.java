package nano.support;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Task
 *
 * @author cbdyzj
 * @since 2020.12.11
 */
@FunctionalInterface
public interface Task {

    void execute(@NotNull Map<String, ?> options);
}
