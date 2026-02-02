package io.github.eggy03.papertrail.sdk.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@Getter
@Builder(toBuilder = true)
public class MessageLogContentEntity {

    @NotNull
    private String messageId;

    @NotNull
    private String messageContent;

    @NotNull
    private String authorId;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public MessageLogContentEntity(@JsonProperty("messageId") @NonNull String messageId,
                                   @JsonProperty("messageContent") @NonNull String messageContent,
                                   @JsonProperty("authorId") @NonNull String authorId) {
        this.messageId = messageId;
        this.messageContent = messageContent;
        this.authorId = authorId;
    }
}
