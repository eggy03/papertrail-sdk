package io.github.eggy03.papertrail.sdk.exception;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Thrown when the API url is null or empty
 */
public class ApiBaseUrlException extends RuntimeException {

    public ApiBaseUrlException() {
        super();
    }

    public ApiBaseUrlException(@NonNull String message) {
        super(message);
    }

    public ApiBaseUrlException(@NonNull String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public ApiBaseUrlException(@Nullable Throwable cause) {
        super(cause);
    }

}
