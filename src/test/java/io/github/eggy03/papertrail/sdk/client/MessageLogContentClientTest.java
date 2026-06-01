package io.github.eggy03.papertrail.sdk.client;

import io.github.eggy03.papertrail.sdk.entity.ErrorEntity;
import io.github.eggy03.papertrail.sdk.entity.MessageLogContentEntity;
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

class MessageLogContentClientTest {

    private static MessageLogContentClient client;

    private final String messageId = "123456789";
    private final String messageContent = "test";
    private final String authorId = "987654321";

    static HttpServiceEngine mockEngine = mock(HttpServiceEngine.class);

    @BeforeAll
    static void registerClient() {
        client = new MessageLogContentClient(mockEngine);
    }

    @ParameterizedTest
    @EmptySource
    void testConstructorEmptyBaseUrl(String baseUrl) {
        assertThrows(ApiBaseUrlException.class, () -> new MessageLogContentClient(baseUrl));
    }

    @ParameterizedTest
    @NullSource
    void testConstructorNullBaseUrl(String baseUrl) {
        assertThrows(NullPointerException.class, () -> new MessageLogContentClient(baseUrl));
    }

    @Test
    void testLogMessage_success() {

        MessageLogContentEntity responseBody = new MessageLogContentEntity(messageId, messageContent, authorId);
        when(mockEngine.makeRequestWithBody(
                eq(HttpMethod.POST),
                eq("/api/v1/content/message"),
                any(HttpHeaders.class),
                any(MessageLogContentEntity.class),
                eq(MessageLogContentEntity.class)
        )).thenReturn(Either.right(responseBody));

        assertThat(client.logMessage(messageId, messageContent, authorId)).isTrue();
    }

    @Test
    void testLogMessage_failure() {

        when(mockEngine.makeRequestWithBody(
                eq(HttpMethod.POST),
                eq("/api/v1/content/message"),
                any(HttpHeaders.class),
                any(MessageLogContentEntity.class),
                eq(MessageLogContentEntity.class)
        )).thenReturn(Either.left(new ErrorEntity(0, "", "", "", "")));

        assertThat(client.logMessage(messageId, messageContent, authorId)).isFalse();
    }

    @Test
    void testRetrieveMessage_success() {

        MessageLogContentEntity responseBody = new MessageLogContentEntity(messageId, messageContent, authorId);
        when(mockEngine.makeRequest(
                eq(HttpMethod.GET),
                eq("/api/v1/content/message/" + messageId),
                any(HttpHeaders.class),
                eq(MessageLogContentEntity.class)
        )).thenReturn(Either.right(responseBody));

        assertThat(client.retrieveMessage(messageId)).isNotEmpty();
        assertThat(client.retrieveMessage(messageId)).get().isEqualTo(responseBody);
    }

    @Test
    void testRetrieveMessage_empty() {

        when(mockEngine.makeRequest(
                eq(HttpMethod.GET),
                eq("/api/v1/content/message/" + messageId),
                any(HttpHeaders.class),
                eq(MessageLogContentEntity.class)
        )).thenReturn(Either.left(new ErrorEntity(0, "", "", "", "")));

        assertThat(client.retrieveMessage(messageId)).isEmpty();
    }

    @Test
    void testUpdateMessage_success() {

        MessageLogContentEntity responseBody = new MessageLogContentEntity(messageId, messageContent, authorId);
        when(mockEngine.makeRequestWithBody(
                eq(HttpMethod.PUT),
                eq("/api/v1/content/message"),
                any(HttpHeaders.class),
                any(MessageLogContentEntity.class),
                eq(MessageLogContentEntity.class)
        )).thenReturn(Either.right(responseBody));

        assertThat(client.updateMessage(messageId, messageContent, authorId)).isTrue();
    }

    @Test
    void testUpdateMessage_failure() {

        when(mockEngine.makeRequestWithBody(
                eq(HttpMethod.PUT),
                eq("/api/v1/content/message"),
                any(HttpHeaders.class),
                any(MessageLogContentEntity.class),
                eq(MessageLogContentEntity.class)
        )).thenReturn(Either.left(new ErrorEntity(0, "", "", "", "")));

        assertThat(client.updateMessage(messageId, messageContent, authorId)).isFalse();
    }

    @Test
    void testDeleteMessage_success() {

        when(mockEngine.makeRequest(
                eq(HttpMethod.DELETE),
                eq("/api/v1/content/message/" + messageId),
                any(HttpHeaders.class),
                eq(Void.class)
        )).thenReturn(Either.right(null));

        assertThat(client.deleteMessage(messageId)).isTrue();
    }

    @Test
    void testDeleteMessage_failure() {

        when(mockEngine.makeRequest(
                eq(HttpMethod.DELETE),
                eq("/api/v1/content/message/" + messageId),
                any(HttpHeaders.class),
                eq(Void.class)
        )).thenReturn(Either.left(new ErrorEntity(0, "", "", "", "")));

        assertThat(client.deleteMessage(messageId)).isFalse();
    }
}
