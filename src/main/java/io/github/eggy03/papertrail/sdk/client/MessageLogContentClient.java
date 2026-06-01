package io.github.eggy03.papertrail.sdk.client;

import io.github.eggy03.papertrail.sdk.entity.ErrorEntity;
import io.github.eggy03.papertrail.sdk.entity.MessageLogContentEntity;
import io.github.eggy03.papertrail.sdk.http.HttpServiceEngine;
import io.vavr.control.Either;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.Objects;
import java.util.Optional;

/**
 * Client for managing stored message content via the PaperTrail API.
 */
public final class MessageLogContentClient {

    private static final Logger log = LoggerFactory.getLogger(MessageLogContentClient.class);

    private final HttpServiceEngine engine;

    /**
     * Creates a new {@code MessageLogContentClient} using the specified API base URL.
     *
     * @param baseUrl the base URL of the API; must not be {@code null}
     * @throws NullPointerException if {@code baseUrl} is {@code null}
     */
    public MessageLogContentClient(@NonNull String baseUrl){
        this(new HttpServiceEngine(Objects.requireNonNull(baseUrl, "baseUrl cannot be null")));
    }

    /**
     * Creates a new {@code MessageLogContentClient} using the provided HTTP service engine.
     *
     * @param httpServiceEngine the HTTP service engine to use; must not be {@code null}
     * @throws NullPointerException if {@code httpServiceEngine} is {@code null}
     */
    MessageLogContentClient (@NonNull HttpServiceEngine httpServiceEngine){
        this.engine = Objects.requireNonNull(httpServiceEngine, "httpServiceEngine cannot be null");
    }

    /**
     * Logs a new message's content.
     *
     * @param messageId      the Discord message ID (must not be {@code null})
     * @param messageContent the content of the message (must not be {@code null} but may be empty)
     * @param authorId       the Discord user ID of the message author (must not be {@code null})
     * @return {@code true} if the message was logged successfully, {@code false} otherwise
     */
    public boolean logMessage(@NonNull String messageId, @NonNull String messageContent, @NonNull String authorId) {

        Objects.requireNonNull(messageId, "messageId cannot be null");
        Objects.requireNonNull(messageContent, "messageContent cannot be null");
        Objects.requireNonNull(authorId, "authorId cannot be null");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, MessageLogContentEntity> responseBody = engine.makeRequestWithBody(
                HttpMethod.POST,
                "/api/v1/content/message",
                headers,
                new MessageLogContentEntity(messageId, messageContent, authorId),
                MessageLogContentEntity.class
        );

        // log in case of failure
        responseBody.peekLeft(failure -> log.debug("Failed to log message with ID {}.\nAPI Response: {}", messageId, failure));

        return responseBody.isRight();
    }

    /**
     * Retrieves a logged message by its ID.
     *
     * @param messageId the Discord message ID (must not be {@code null})
     * @return an {@link Optional} containing the message content if found, or empty if not present
     */
    public Optional<MessageLogContentEntity> retrieveMessage (@NonNull String messageId) {

        Objects.requireNonNull(messageId, "messageId cannot be null");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, MessageLogContentEntity> response = engine.makeRequest(
                HttpMethod.GET,
                "/api/v1/content/message/"+messageId,
                headers,
                MessageLogContentEntity.class
        );

        // in case of error entity, log it
        response.peekLeft(error -> log.debug("Message of ID {} could not be retrieved.\nAPI Response: {}", messageId, error));

        // in case of success, return the never null MessageLogContentEntity object or empty optional
        return response.map(Optional::of).getOrElse(Optional.empty());
    }

    /**
     * Updates the content of an already logged message.
     *
     * @param messageId      the Discord message ID (must not be {@code null})
     * @param messageContent the updated message content (must not be {@code null})
     * @param authorId       the Discord user ID of the message author (must not be {@code null})
     * @return {@code true} if the update succeeded, {@code false} otherwise
     */
    public boolean updateMessage (@NonNull String messageId, @NonNull String messageContent, @NonNull String authorId) {

        Objects.requireNonNull(messageId, "messageId cannot be null");
        Objects.requireNonNull(messageContent, "messageContent cannot be null");
        Objects.requireNonNull(authorId, "authorId cannot be null");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, MessageLogContentEntity> responseBody = engine.makeRequestWithBody(
                HttpMethod.PUT,
                "/api/v1/content/message",
                headers,
                new MessageLogContentEntity(messageId, messageContent, authorId),
                MessageLogContentEntity.class
        );

        responseBody.peekLeft(failure -> log.debug("Failed to update message with ID {}.\nAPI Response: {}", messageId, failure));

        return responseBody.isRight();
    }

    /**
     * Deletes a logged message by its ID.
     *
     * @param messageId the Discord message ID (must not be {@code null})
     * @return {@code true} if the deletion succeeded, {@code false} otherwise
     */
    public boolean deleteMessage (@NonNull String messageId) {

        Objects.requireNonNull(messageId, "messageId cannot be null");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, Void> responseBody = engine.makeRequest(
                HttpMethod.DELETE,
                "/api/v1/content/message/"+messageId,
                headers,
                Void.class
        );

        responseBody.peekLeft(failure -> log.debug("Failed to delete message with ID {}.\nAPI Response: {}", messageId, failure));

        return responseBody.isRight();
    }
}
