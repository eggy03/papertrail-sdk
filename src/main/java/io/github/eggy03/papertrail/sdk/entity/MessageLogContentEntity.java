package io.github.eggy03.papertrail.sdk.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a simplified version of Discord's message object, stripped of all metadata except message and author ID.
 * <p>
 * Immutable and thread-safe
 * </p>
 */
public final class MessageLogContentEntity {

    /**
     * The unique ID of the Discord message.
     */
    private final @NonNull String messageId;

    /**
     * The plain-text content of the message.
     */
    private final @NonNull String messageContent;

    /**
     * The unique ID of the author of the message.
     */
    private final @NonNull String authorId;

    /**
     * Creates a new {@code MessageLogContentEntity}.
     *
     * @param messageId      the Discord message ID (must not be {@code null})
     * @param messageContent the message content in plain text (must not be {@code null})
     * @param authorId       the Discord user ID of the message author (must not be {@code null})
     */
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public MessageLogContentEntity(@JsonProperty("messageId") @NonNull String messageId,
                                   @JsonProperty("messageContent") @NonNull String messageContent,
                                   @JsonProperty("authorId") @NonNull String authorId
    ) {
        this.messageId = Objects.requireNonNull(messageId, "messageId cannot be null");
        this.messageContent = Objects.requireNonNull(messageContent, "messageContent cannot be null");
        this.authorId = Objects.requireNonNull(authorId, "authorId cannot be null");
    }

    /**
     * The unique ID of the Discord message.
     */
    public @NonNull String getMessageId() {
        return messageId;
    }

    /**
     * The plain-text content of the message.
     */
    public @NonNull String getMessageContent() {
        return messageContent;
    }

    /**
     * The unique ID of the author of the message.
     */
    public @NonNull String getAuthorId() {
        return authorId;
    }
}
