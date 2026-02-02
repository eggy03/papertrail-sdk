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

@Slf4j
public class AuditLogRegistrationClient {

    private final String baseUrl;

    public AuditLogRegistrationClient(String baseUrl){
        if(baseUrl==null || baseUrl.trim().isEmpty())
            throw new ApiBaseUrlException("Base URL is null or empty");

        this.baseUrl = baseUrl;
    }

    public boolean registerGuild(@NonNull String guildId, @NonNull String channelId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, AuditLogRegistrationEntity> responseBody = HttpServiceEngine.makeRequestWithBody(
                HttpMethod.POST,
                baseUrl+"api/v1/log/audit",
                headers,
                new AuditLogRegistrationEntity(guildId, channelId),
                AuditLogRegistrationEntity.class
        );

        // log in case of failure
        responseBody.peekLeft(failure -> log.debug("Failed to register guild for audit logging.\nAPI Response: {}", failure));

        return responseBody.isRight();
    }

    @NotNull
    public Optional<AuditLogRegistrationEntity> getRegisteredGuild (@NonNull String guildId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, AuditLogRegistrationEntity> response = HttpServiceEngine.makeRequest(
                HttpMethod.GET,
                baseUrl+"api/v1/log/audit/"+guildId,
                headers,
                AuditLogRegistrationEntity.class
        );

        // in case of error entity, log it
        response.peekLeft(error -> log.debug("No guild of the ID: {} is registered.\nAPI Response: {}", guildId, error));

        // in case of success, return the AuditLogRegistrationEntity object or empty optional
        return response.map(Optional::of).getOrElse(Optional.empty());
    }

    public boolean deleteRegisteredGuild (@NonNull String guildId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Either<ErrorEntity, Void> responseBody = HttpServiceEngine.makeRequest(
                HttpMethod.DELETE,
                baseUrl +"api/v1/log/audit/"+guildId,
                headers,
                Void.class
        );

        responseBody.peekLeft(failure -> log.debug("Failed to delete registered guild for audit logging.\nAPI Response: {}", failure));

        return responseBody.isRight();
    }
}
