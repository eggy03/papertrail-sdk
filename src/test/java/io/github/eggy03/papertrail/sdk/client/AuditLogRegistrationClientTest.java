package io.github.eggy03.papertrail.sdk.client;

import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import io.github.eggy03.papertrail.sdk.entity.ErrorEntity;
import io.github.eggy03.papertrail.sdk.exception.ApiBaseUrlException;
import io.github.eggy03.papertrail.sdk.http.HttpServiceEngine;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuditLogRegistrationClientTest {

    private static AuditLogRegistrationClient client;

    private final String guildId = "123456789";
    private final String channelId = "987654321";

    static HttpServiceEngine mockEngine = mock(HttpServiceEngine.class);

    @BeforeAll
    static void registerClient() {
        client = new AuditLogRegistrationClient(mockEngine);
    }

    @ParameterizedTest
    @EmptySource
    void testConstructorEmptyBaseUrl(String baseUrl) {
        assertThrows(ApiBaseUrlException.class, () -> new AuditLogRegistrationClient(baseUrl));
    }

    @ParameterizedTest
    @NullSource
    void testConstructorNullBaseUrl(String baseUrl) {
        assertThrows(NullPointerException.class, () -> new AuditLogRegistrationClient(baseUrl));
    }

    @Test
    void registerGuild_success() {

        AuditLogRegistrationEntity responseBody = new AuditLogRegistrationEntity(guildId, channelId);

        when(mockEngine.makeRequestWithBody(
                eq(HttpMethod.POST),
                eq("/api/v1/log/audit"),
                any(HttpHeaders.class),
                any(AuditLogRegistrationEntity.class),
                eq(AuditLogRegistrationEntity.class)
        )).thenReturn(Either.right(responseBody));

        assertThat(client.registerGuild(guildId, channelId)).isTrue();

    }

    @Test
    void registerGuild_error() {

        ErrorEntity errorBody = new ErrorEntity(0, "", "", "", "");

        when(mockEngine.makeRequestWithBody(
                eq(HttpMethod.POST),
                eq("/api/v1/log/audit"),
                any(HttpHeaders.class),
                any(AuditLogRegistrationEntity.class),
                eq(AuditLogRegistrationEntity.class)
        )).thenReturn(Either.left(errorBody));

        assertThat(client.registerGuild(guildId, channelId)).isFalse();
    }

    @Test
    void getRegisteredGuild_success() {

        AuditLogRegistrationEntity responseBody = new AuditLogRegistrationEntity(guildId, channelId);

        when(mockEngine.makeRequest(
                eq(HttpMethod.GET),
                eq("/api/v1/log/audit/" + guildId),
                any(HttpHeaders.class),
                eq(AuditLogRegistrationEntity.class)
        )).thenReturn(Either.right(responseBody));

        assertThat(client.getRegisteredGuild(guildId)).isNotEmpty();
        assertThat(client.getRegisteredGuild(guildId)).get().isEqualTo(responseBody);

    }

    @Test
    void getRegisteredGuild_empty() {

        ErrorEntity responseBody = new ErrorEntity(0, "", "", "", "");

        when(mockEngine.makeRequest(
                eq(HttpMethod.GET),
                eq("/api/v1/log/audit/" + guildId),
                any(HttpHeaders.class),
                eq(AuditLogRegistrationEntity.class)
        )).thenReturn(Either.left(responseBody));

        assertThat(client.getRegisteredGuild(guildId)).isEmpty();
    }



    @Test
    void deleteRegisteredGuild_success() {

        when(mockEngine.makeRequest(
                eq(HttpMethod.DELETE),
                eq("/api/v1/log/audit/" + guildId),
                any(HttpHeaders.class),
                eq(Void.class)
        )).thenReturn(Either.right(null));

        assertThat(client.deleteRegisteredGuild(guildId)).isTrue();
    }

    @Test
    void deleteRegisteredGuild_error() {

        when(mockEngine.makeRequest(
                eq(HttpMethod.DELETE),
                eq("/api/v1/log/audit/" + guildId),
                any(HttpHeaders.class),
                eq(Void.class)
        )).thenReturn(Either.left(new ErrorEntity(0, "", "", "", "")));

        assertThat(client.deleteRegisteredGuild(guildId)).isFalse();

    }
}
