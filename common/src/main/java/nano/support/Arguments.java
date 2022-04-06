package nano.support;

import org.jetbrains.annotations.NotNull;

public class Arguments {

    private final Object[] args;

    public Arguments(Object @NotNull ... args) {
        this.args = args;
    }

    public <T> T get(int index, @NotNull Class<T> clazz) {
        this.checkIndex(index);
        return clazz.cast(this.get(index));
    }

    public Object get(int index) {
        return this.args[index];
    }

    public int length() {
        return this.args.length;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= this.length()) {
            throw new IllegalArgumentException("The index exceeds the length of args");
        }
    }
}
