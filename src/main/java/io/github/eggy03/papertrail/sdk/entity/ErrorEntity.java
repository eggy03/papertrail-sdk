package io.github.eggy03.papertrail.sdk.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@Getter
@Builder(toBuilder = true)
public class ErrorEntity {

    private final int status;

    @NotNull
    private final String error;

    @NotNull
    private final String message;

    @NotNull
    private final String timeStamp;

    @NotNull
    private final String path;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ErrorEntity(
            @JsonProperty("status") int status,
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
}
