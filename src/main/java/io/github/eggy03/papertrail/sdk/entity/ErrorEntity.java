package io.github.eggy03.papertrail.sdk.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;

/**
 * Represents a structured error response returned by the PaperTrail API.
 */
public final class ErrorEntity {

    /**
     * The HTTP status code associated with the error.
     */
    private final int status;

    /**
     * A short error identifier or type, often the underlying exception class
     */
    private final @NonNull String error;

    /**
     * A human-readable error message describing what went wrong.
     */
    private final @NonNull String message;

    /**
     * The timestamp indicating when the error occurred.
     */
    private final @NonNull String timeStamp;

    /**
     * The request path that caused the error.
     */
    private final @NonNull String path;

    /**
     * Creates a new {@code ErrorEntity}.
     *
     * @param status    the HTTP status code
     * @param error     a short error identifier or type (must not be {@code null})
     * @param message   a human-readable error message (must not be {@code null})
     * @param timeStamp the timestamp of the error occurrence (must not be {@code null})
     * @param path      the request path that caused the error (must not be {@code null})
     */
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ErrorEntity(@JsonProperty("status") int status,
                       @JsonProperty("error") @NonNull String error,
                       @JsonProperty("message") @NonNull String message,
                       @JsonProperty("timeStamp") @NonNull String timeStamp,
                       @JsonProperty("path") @NonNull String path
    ) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timeStamp = timeStamp;
        this.path = path;
    }

    /**
     * The HTTP status code associated with the error.
     */
    public int getStatus() {
        return status;
    }

    /**
     * A short error identifier or type, often the underlying exception class
     */
    public @NonNull String getError() {
        return error;
    }

    /**
     * A human-readable error message describing what went wrong.
     */
    public @NonNull String getMessage() {
        return message;
    }

    /**
     * The timestamp indicating when the error occurred.
     */
    public @NonNull String getTimeStamp() {
        return timeStamp;
    }

    /**
     * The request path that caused the error.
     */
    public @NonNull String getPath() {
        return path;
    }
}
