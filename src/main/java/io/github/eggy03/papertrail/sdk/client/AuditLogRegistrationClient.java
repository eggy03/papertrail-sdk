package io.github.eggy03.papertrail.sdk.client;

import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import io.github.eggy03.papertrail.sdk.entity.ErrorEntity;
import io.github.eggy03.papertrail.sdk.exception.ApiBaseUrlException;
import io.github.eggy03.papertrail.sdk.http.HttpServiceEngine;
import io.vavr.control.Either;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.Optional;

/**
 * Client for managing audit log registrations via the PaperTrail API.
 */
@Slf4j
public class AuditLogRegistrationClient {

    private final HttpServiceEngine engine;

    /**
     * Creates a new {@code AuditLogRegistrationClient}.
     *
     * @param baseUrl the base URL of the PaperTrail API (must not be {@code null} or blank)
     * @throws ApiBaseUrlException if the base URL is {@code null} or empty
     */
    public AuditLogRegistrationClient(@NonNull String baseUrl){
        this.engine = new HttpServiceEngine(baseUrl);
    }

    /**
     * Mostly for testing purposes
     */
    AuditLogRegistrationClient (@NonNull HttpServiceEngine httpServiceEngine){
        this.engine = httpServiceEngine;
    }

    /**
     * Registers a guild for audit logging.
     *
     * @param guildId   the Discord guild ID (must not be {@code null})
     * @param channelId the Discord channel ID where audit logs should be sent (must not be {@code null})
     * @return {@code true} if the registration succeeded, {@code false} otherwise
     */
    public boolean registerGuild(@NonNull String guildId, @NonNull String channelId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, AuditLogRegistrationEntity> responseBody = engine.makeRequestWithBody(
                HttpMethod.POST,
                "/api/v1/log/audit",
                headers,
                new AuditLogRegistrationEntity(guildId, channelId),
                AuditLogRegistrationEntity.class
        );

        // log in case of failure
        responseBody.peekLeft(failure -> log.debug("Failed to register guild for audit logging.\nAPI Response: {}", failure));

        return responseBody.isRight();
    }

    /**
     * Retrieves the audit log registration for a guild, if one exists.
     *
     * @param guildId the Discord guild ID (must not be {@code null})
     * @return an {@link Optional} containing the registration if found, or empty if not registered
     */
    @NotNull
    public Optional<AuditLogRegistrationEntity> getRegisteredGuild (@NonNull String guildId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, AuditLogRegistrationEntity> response = engine.makeRequest(
                HttpMethod.GET,
                "/api/v1/log/audit/"+guildId,
                headers,
                AuditLogRegistrationEntity.class
        );

        // in case of error entity, log it
        response.peekLeft(error -> log.debug("No guild of the ID: {} is registered.\nAPI Response: {}", guildId, error));

        // in case of success, return the AuditLogRegistrationEntity object or empty optional
        return response.map(Optional::of).getOrElse(Optional.empty());
    }

    /**
     * Deletes the audit log registration for a guild.
     *
     * @param guildId the Discord guild ID (must not be {@code null})
     * @return {@code true} if the deletion succeeded, {@code false} otherwise
     */
    public boolean deleteRegisteredGuild (@NonNull String guildId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, Void> responseBody = engine.makeRequest(
                HttpMethod.DELETE,
                "/api/v1/log/audit/"+guildId,
                headers,
                Void.class
        );

        responseBody.peekLeft(failure -> log.debug("Failed to delete registered guild for audit logging.\nAPI Response: {}", failure));

        return responseBody.isRight();
    }
}
