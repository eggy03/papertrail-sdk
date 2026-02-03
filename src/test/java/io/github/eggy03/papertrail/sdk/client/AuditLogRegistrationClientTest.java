package io.github.eggy03.papertrail.sdk.client;

import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import io.github.eggy03.papertrail.sdk.entity.ErrorEntity;
import io.github.eggy03.papertrail.sdk.exception.ApiBaseUrlException;
import io.github.eggy03.papertrail.sdk.http.HttpServiceEngine;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.MockedStatic;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

class AuditLogRegistrationClientTest {

    private static final String BASE_URL = "https://api.example.com";
    private static AuditLogRegistrationClient client;
    private final String guildId = "123456789";
    private final String channelId = "987654321";

    @BeforeAll
    static void registerClientAndHeader() {
        client = new AuditLogRegistrationClient(BASE_URL);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testConstructor(String baseUrl) {
        assertThrows(ApiBaseUrlException.class, () -> new AuditLogRegistrationClient(baseUrl));
    }

    @Test
    void registerGuild_success() {

        AuditLogRegistrationEntity responseBody = new AuditLogRegistrationEntity(guildId, channelId);
        try (MockedStatic<HttpServiceEngine> mockEngine = mockStatic(HttpServiceEngine.class)) {
            mockEngine.when(() -> {
                HttpServiceEngine.makeRequestWithBody(
                        eq(HttpMethod.POST),
                        eq(BASE_URL + "api/v1/log/audit"),
                        any(HttpHeaders.class),
                        any(AuditLogRegistrationEntity.class),
                        eq(AuditLogRegistrationEntity.class)
                );
            }).thenReturn(Either.right(responseBody));

            assertThat(client.registerGuild(guildId, channelId)).isTrue();
        }
    }

    @Test
    void registerGuild_error() {

        ErrorEntity errorBody = ErrorEntity.builder().build();
        try (MockedStatic<HttpServiceEngine> mockEngine = mockStatic(HttpServiceEngine.class)) {
            mockEngine.when(() -> {
                HttpServiceEngine.makeRequestWithBody(
                        eq(HttpMethod.POST),
                        eq(BASE_URL + "api/v1/log/audit"),
                        any(HttpHeaders.class),
                        any(AuditLogRegistrationEntity.class),
                        eq(AuditLogRegistrationEntity.class)
                );
            }).thenReturn(Either.left(errorBody));

            assertThat(client.registerGuild(guildId, channelId)).isFalse();
        }
    }

    @Test
    void getRegisteredGuild_success() {

        AuditLogRegistrationEntity responseBody = new AuditLogRegistrationEntity(guildId, channelId);
        try (MockedStatic<HttpServiceEngine> mockEngine = mockStatic(HttpServiceEngine.class)) {
            mockEngine.when(() -> {
                HttpServiceEngine.makeRequest(
                        eq(HttpMethod.GET),
                        eq(BASE_URL + "api/v1/log/audit/" + guildId),
                        any(HttpHeaders.class),
                        eq(AuditLogRegistrationEntity.class)
                );
            }).thenReturn(Either.right(responseBody));

            assertThat(client.getRegisteredGuild(guildId)).isNotEmpty();
            assertThat(client.getRegisteredGuild(guildId)).get().isEqualTo(responseBody);
        }
    }

    @Test
    void getRegisteredGuild_empty() {

        ErrorEntity responseBody = ErrorEntity.builder().build();
        try (MockedStatic<HttpServiceEngine> mockEngine = mockStatic(HttpServiceEngine.class)) {
            mockEngine.when(() -> {
                HttpServiceEngine.makeRequest(
                        eq(HttpMethod.GET),
                        eq(BASE_URL + "api/v1/log/audit/" + guildId),
                        any(HttpHeaders.class),
                        eq(AuditLogRegistrationEntity.class)
                );
            }).thenReturn(Either.left(responseBody));

            assertThat(client.getRegisteredGuild(guildId)).isEmpty();
        }
    }

    @Test
    void deleteRegisteredGuild_success() {

        try (MockedStatic<HttpServiceEngine> mockEngine = mockStatic(HttpServiceEngine.class)) {

            mockEngine.when(() -> {
                HttpServiceEngine.makeRequest(
                        eq(HttpMethod.DELETE),
                        eq(BASE_URL + "api/v1/log/audit/" + guildId),
                        any(HttpHeaders.class),
                        eq(Void.class)
                );
            }).thenReturn(Either.right(null));

            assertThat(client.deleteRegisteredGuild(guildId)).isTrue();
        }
    }

    @Test
    void deleteRegisteredGuild_error() {

        try (MockedStatic<HttpServiceEngine> mockEngine = mockStatic(HttpServiceEngine.class)) {

            mockEngine.when(() -> {
                HttpServiceEngine.makeRequest(
                        eq(HttpMethod.DELETE),
                        eq(BASE_URL + "api/v1/log/audit/" + guildId),
                        any(HttpHeaders.class),
                        eq(Void.class)
                );
            }).thenReturn(Either.left(ErrorEntity.builder().build()));

            assertThat(client.deleteRegisteredGuild(guildId)).isFalse();
        }
    }
}
