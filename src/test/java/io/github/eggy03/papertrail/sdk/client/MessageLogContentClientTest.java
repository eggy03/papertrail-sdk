package io.github.eggy03.papertrail.sdk.client;

import io.github.eggy03.papertrail.sdk.entity.ErrorEntity;
import io.github.eggy03.papertrail.sdk.entity.MessageLogContentEntity;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

class MessageLogContentClientTest {

    private static final String BASE_URL = "https://api.example.com";
    private static MessageLogContentClient client;
    private final String messageId = "123456789";
    private final String messageContent = "test";
    private final String authorId = "987654321";

    @BeforeAll
    static void setClient() {
        client = new MessageLogContentClient(BASE_URL);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testConstructor(String baseUrl) {
        assertThatThrownBy(() -> new MessageLogContentClient(baseUrl)).isInstanceOf(ApiBaseUrlException.class);
    }

    @Test
    void testLogMessage_success() {

        try (MockedStatic<HttpServiceEngine> mockEngine = mockStatic(HttpServiceEngine.class)) {

            MessageLogContentEntity responseBody = new MessageLogContentEntity(messageId, messageContent, authorId);
            mockEngine.when(() -> {
                HttpServiceEngine.makeRequestWithBody(
                        eq(HttpMethod.POST),
                        eq(BASE_URL + "api/v1/content/message"),
                        any(HttpHeaders.class),
                        any(MessageLogContentEntity.class),
                        eq(MessageLogContentEntity.class)
                );
            }).thenReturn(Either.right(responseBody));

            assertThat(client.logMessage(messageId, messageContent, authorId)).isTrue();

        }
    }

    @Test
    void testLogMessage_failure() {
        try (MockedStatic<HttpServiceEngine> mockEngine = mockStatic(HttpServiceEngine.class)) {

            mockEngine.when(() -> {
                HttpServiceEngine.makeRequestWithBody(
                        eq(HttpMethod.POST),
                        eq(BASE_URL + "api/v1/content/message"),
                        any(HttpHeaders.class),
                        any(MessageLogContentEntity.class),
                        eq(MessageLogContentEntity.class)
                );
            }).thenReturn(Either.left(ErrorEntity.builder().build()));

            assertThat(client.logMessage(messageId, messageContent, authorId)).isFalse();
        }
    }

    @Test
    void testRetrieveMessage_success() {
        try (MockedStatic<HttpServiceEngine> mockEngine = mockStatic(HttpServiceEngine.class)) {

            MessageLogContentEntity responseBody = new MessageLogContentEntity(messageId, messageContent, authorId);
            mockEngine.when(() -> {
                HttpServiceEngine.makeRequest(
                        eq(HttpMethod.GET),
                        eq(BASE_URL + "api/v1/content/message/" + messageId),
                        any(HttpHeaders.class),
                        eq(MessageLogContentEntity.class)
                );
            }).thenReturn(Either.right(responseBody));

            assertThat(client.retrieveMessage(messageId)).isNotEmpty();
            assertThat(client.retrieveMessage(messageId)).get().isEqualTo(responseBody);
        }
    }

    @Test
    void testRetrieveMessage_empty() {
        try (MockedStatic<HttpServiceEngine> mockEngine = mockStatic(HttpServiceEngine.class)) {

            mockEngine.when(() -> {
                HttpServiceEngine.makeRequest(
                        eq(HttpMethod.GET),
                        eq(BASE_URL + "api/v1/content/message/" + messageId),
                        any(HttpHeaders.class),
                        eq(MessageLogContentEntity.class)
                );
            }).thenReturn(Either.left(ErrorEntity.builder().build()));

            assertThat(client.retrieveMessage(messageId)).isEmpty();
        }
    }

    @Test
    void testUpdateMessage_success() {
        try (MockedStatic<HttpServiceEngine> mockEngine = mockStatic(HttpServiceEngine.class)) {

            MessageLogContentEntity responseBody = new MessageLogContentEntity(messageId, messageContent, authorId);
            mockEngine.when(() -> {
                HttpServiceEngine.makeRequestWithBody(
                        eq(HttpMethod.PUT),
                        eq(BASE_URL + "api/v1/content/message"),
                        any(HttpHeaders.class),
                        any(MessageLogContentEntity.class),
                        eq(MessageLogContentEntity.class)
                );
            }).thenReturn(Either.right(responseBody));

            assertThat(client.updateMessage(messageId, messageContent, authorId)).isTrue();
        }
    }

    @Test
    void testUpdateMessage_failure() {
        try (MockedStatic<HttpServiceEngine> mockEngine = mockStatic(HttpServiceEngine.class)) {

            mockEngine.when(() -> {
                HttpServiceEngine.makeRequestWithBody(
                        eq(HttpMethod.PUT),
                        eq(BASE_URL + "api/v1/content/message"),
                        any(HttpHeaders.class),
                        any(MessageLogContentEntity.class),
                        eq(MessageLogContentEntity.class)
                );
            }).thenReturn(Either.left(ErrorEntity.builder().build()));

            assertThat(client.updateMessage(messageId, messageContent, authorId)).isFalse();
        }
    }

    void testDeleteMessage_success() {
        try (MockedStatic<HttpServiceEngine> mockEngine = mockStatic(HttpServiceEngine.class)) {

            mockEngine.when(() -> {
                HttpServiceEngine.makeRequest(
                        eq(HttpMethod.DELETE),
                        eq(BASE_URL + "api/v1/content/message/" + messageId),
                        any(HttpHeaders.class),
                        eq(Void.class)
                );
            }).thenReturn(Either.right(null));

            assertThat(client.deleteMessage(messageId)).isTrue();
        }
    }

    @Test
    void testDeleteMessage_failure() {
        try (MockedStatic<HttpServiceEngine> mockEngine = mockStatic(HttpServiceEngine.class)) {

            mockEngine.when(() -> {
                HttpServiceEngine.makeRequest(
                        eq(HttpMethod.DELETE),
                        eq(BASE_URL + "api/v1/content/message/" + messageId),
                        any(HttpHeaders.class),
                        eq(Void.class)
                );
            }).thenReturn(Either.left(ErrorEntity.builder().build()));

            assertThat(client.deleteMessage(messageId)).isFalse();
        }
    }


}
