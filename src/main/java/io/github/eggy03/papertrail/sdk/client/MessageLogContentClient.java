package io.github.eggy03.papertrail.sdk.client;

import io.github.eggy03.papertrail.sdk.entity.ErrorEntity;
import io.github.eggy03.papertrail.sdk.entity.MessageLogContentEntity;
import io.github.eggy03.papertrail.sdk.exception.ApiBaseUrlException;
import io.github.eggy03.papertrail.sdk.http.HttpServiceEngine;
import io.vavr.control.Either;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.Optional;

/**
 * Client for managing stored message content via the PaperTrail API.
 */
@Slf4j
public class MessageLogContentClient {

    private final String baseUrl;

    /**
     * Creates a new {@code MessageLogContentClient}.
     *
     * @param baseUrl the base URL of the PaperTrail API (must not be {@code null} or blank)
     * @throws ApiBaseUrlException if the base URL is {@code null} or empty
     */
    public MessageLogContentClient(String baseUrl){
        if(baseUrl==null || baseUrl.trim().isEmpty())
            throw new ApiBaseUrlException("Base URL is null or empty");

        this.baseUrl = baseUrl;
    }

    /**
     * Logs a new message's content.
     *
     * @param messageId      the Discord message ID (must not be {@code null})
     * @param messageContent the content of the message (may be {@code null} or empty if redacted)
     * @param authorId       the Discord user ID of the message author (must not be {@code null})
     * @return {@code true} if the message was logged successfully, {@code false} otherwise
     */
    public boolean logMessage(@NonNull String messageId, String messageContent, @NonNull String authorId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, MessageLogContentEntity> responseBody = HttpServiceEngine.makeRequestWithBody(
                HttpMethod.POST,
                baseUrl + "api/v1/content/message",
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
    @NotNull
    public Optional<MessageLogContentEntity> retrieveMessage (@NonNull String messageId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, MessageLogContentEntity> response = HttpServiceEngine.makeRequest(
                HttpMethod.GET,
                baseUrl+"api/v1/content/message/"+messageId,
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, MessageLogContentEntity> responseBody = HttpServiceEngine.makeRequestWithBody(
                HttpMethod.PUT,
                baseUrl+"api/v1/content/message",
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, Void> responseBody = HttpServiceEngine.makeRequest(
                HttpMethod.DELETE,
                baseUrl+"api/v1/content/message/"+messageId,
                headers,
                Void.class
        );

        responseBody.peekLeft(failure -> log.debug("Failed to delete message with ID {}.\nAPI Response: {}", messageId, failure));

        return responseBody.isRight();
    }
}
