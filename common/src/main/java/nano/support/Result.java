package nano.support;

public record Result<T>(String error, T payload) {

    public static <U> Result<U> of(U payload) {
        return new Result<>(null, payload);
    }

    public static Result<?> error(String error) {
        return new Result<>(error, null);
    }

    public static <U> Result<U> empty() {
        return of(null);
    }
}
