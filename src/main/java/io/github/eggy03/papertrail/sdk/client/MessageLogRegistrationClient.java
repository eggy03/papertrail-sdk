package io.github.eggy03.papertrail.sdk.client;

import io.github.eggy03.papertrail.sdk.entity.ErrorEntity;
import io.github.eggy03.papertrail.sdk.entity.MessageLogRegistrationEntity;
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

public final class MessageLogRegistrationClient {

    private static final Logger log = LoggerFactory.getLogger(MessageLogRegistrationClient.class);

    private final HttpServiceEngine engine;

    /**
     * Creates a new {@code MessageLogRegistrationClient} using the specified API base URL.
     *
     * @param baseUrl the base URL of the API; must not be {@code null}
     * @throws NullPointerException if {@code baseUrl} is {@code null}
     */
    public MessageLogRegistrationClient(@NonNull String baseUrl){
        this(new HttpServiceEngine(Objects.requireNonNull(baseUrl, "baseUrl cannot be null")));
    }

    /**
     * Creates a new {@code MessageLogRegistrationClient} using the provided HTTP service engine.
     *
     * @param httpServiceEngine the HTTP service engine to use; must not be {@code null}
     * @throws NullPointerException if {@code httpServiceEngine} is {@code null}
     */
    MessageLogRegistrationClient (@NonNull HttpServiceEngine httpServiceEngine){
        this.engine = Objects.requireNonNull(httpServiceEngine, "httpServiceEngine cannot be null");
    }

    /**
     * Registers a guild for message logging.
     *
     * @param guildId   the Discord guild ID (must not be {@code null})
     * @param channelId the Discord channel ID where message logs should be sent (must not be {@code null})
     * @return {@code true} if the registration succeeded, {@code false} otherwise
     */
    public boolean registerGuild(@NonNull String guildId, @NonNull String channelId) {

        Objects.requireNonNull(guildId, "guildId cannot be null");
        Objects.requireNonNull(channelId, "channelId cannot be null");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, MessageLogRegistrationEntity> responseBody = engine.makeRequestWithBody(
                HttpMethod.POST,
                "/api/v1/log/message",
                headers,
                new MessageLogRegistrationEntity(guildId, channelId),
                MessageLogRegistrationEntity.class
        );

        // log in case of failure
        responseBody.peekLeft(failure -> log.debug("Failed to register guild for message logging.\nAPI Response: {}", failure));

        return responseBody.isRight();
    }


    /**
     * Retrieves the message log registration for a guild, if one exists.
     *
     * @param guildId the Discord guild ID (must not be {@code null})
     * @return an {@link Optional} containing the registration if found, or empty if not registered
     */
    public Optional<MessageLogRegistrationEntity> getRegisteredGuild (@NonNull String guildId) {

        Objects.requireNonNull(guildId, "guildId cannot be null");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, MessageLogRegistrationEntity> response = engine.makeRequest(
                HttpMethod.GET,
                "/api/v1/log/message/"+guildId,
                headers,
                MessageLogRegistrationEntity.class
        );

        // in case of error entity, log it
        response.peekLeft(error -> log.debug("No guild of the ID: {} is registered.\nAPI Response: {}", guildId, error));

        // in case of success, return the MessageLogRegistrationEntity object or empty optional
        return response.map(Optional::of).getOrElse(Optional.empty());
    }

    /**
     * Deletes the message log registration for a guild.
     *
     * @param guildId the Discord guild ID (must not be {@code null})
     * @return {@code true} if the deletion succeeded, {@code false} otherwise
     */
    public boolean deleteRegisteredGuild (@NonNull String guildId) {

        Objects.requireNonNull(guildId, "guildId cannot be null");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, Void> responseBody = engine.makeRequest(
                HttpMethod.DELETE,
                "/api/v1/log/message/"+guildId,
                headers,
                Void.class
        );

        responseBody.peekLeft(failure -> log.debug("Failed to delete registered guild for message logging.\nAPI Response: {}", failure));

        return responseBody.isRight();
    }
}
