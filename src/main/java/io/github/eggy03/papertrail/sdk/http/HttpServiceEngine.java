package io.github.eggy03.papertrail.sdk.http;

import io.github.eggy03.papertrail.sdk.entity.ErrorEntity;
import io.github.eggy03.papertrail.sdk.exception.ApiBaseUrlException;
import io.vavr.control.Either;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Objects;

/**
 * Utility class responsible for executing HTTP requests to the PaperTrail API.
 * <p>
 * This class provides static helper methods for making REST calls using
 * Spring's {@link RestClient} and returns responses wrapped in Vavr's {@link Either}
 * </p>
 */
public class HttpServiceEngine {

    private static final Logger log = LoggerFactory.getLogger(HttpServiceEngine.class);
    private final @NonNull RestClient client;

    /**
     * Initializes a {@link RestClient} with the given base URL.
     * Extra forward slashes '/' are automatically removed from the URL during sanitization.
     * @param baseUrl the URL of the API
     */
    public HttpServiceEngine(String baseUrl) {
        
        if (baseUrl == null)
            throw new ApiBaseUrlException("Base URL cannot be null");

        if(baseUrl.trim().isEmpty())
            throw new ApiBaseUrlException("Base URL cannot be blank or empty");

        //replace extra '/' with empty string
        this.client = RestClient.builder().baseUrl(baseUrl.replaceAll("/+$", "")).build();
    }

    /**
     * Executes an HTTP request without a request body.
     *
     * @param httpMethod            the HTTP method to use (e.g., GET, DELETE)
     * @param path                  the target API path
     * @param headers               the HTTP headers to include in the request
     * @param successResponseClass  the expected response type on success
     * @param <S>                   the success response type
     * @return an {@link Either} containing either an {@link ErrorEntity} on failure
     *         or a deserialized success response on success
     */
    public <S> Either<ErrorEntity, S> makeRequest (
            @NonNull HttpMethod httpMethod,
            @NonNull String path,
            @NonNull HttpHeaders headers,
            @NonNull Class<S> successResponseClass
    ) {
        Objects.requireNonNull(httpMethod, "httpMethod cannot be null");
        Objects.requireNonNull(path, "path cannot be null");
        Objects.requireNonNull(headers, "headers cannot be null");
        Objects.requireNonNull(successResponseClass, "successResponseClass cannot be null");

        try {
            S body = client.method(httpMethod)
                    .uri(builder -> builder.path(path).build())
                    .headers(h-> h.addAll(headers))
                    .retrieve()
                    .toEntity(successResponseClass)
                    .getBody();

            return Either.right(body);
        } catch (HttpClientErrorException e) {
            log.debug("Client error when calling {} {}: {}", httpMethod, path, e.getMessage(), e);
            ErrorEntity error = e.getResponseBodyAs(ErrorEntity.class);
            return Either.left(error);
        } catch (HttpServerErrorException e) {
            log.warn("Server error when calling {} {}: {}", httpMethod, path, e.getMessage(), e);
            ErrorEntity error = e.getResponseBodyAs(ErrorEntity.class);
            return Either.left(error);
        } catch (ResourceAccessException e) {
            log.error("Resource access error when calling {} {}: {}", httpMethod, path, e.getMessage(), e);
            ErrorEntity error =  new ErrorEntity(503, "API Unreachable", e.getMessage(), Instant.now().toString(), path);
            return Either.left(error);
        }
    }

    /**
     * Executes an HTTP request with a request body.
     *
     * @param httpMethod            the HTTP method to use (e.g., POST, PUT)
     * @param path                  the target API path
     * @param headers               the HTTP headers to include in the request
     * @param requestBody           the request body to send
     * @param successResponseClass  the expected response type on success
     * @param <S>                   the success response type
     * @return an {@link Either} containing either an {@link ErrorEntity} on failure
     *         or a deserialized success response on success
     */
    public <S> Either<ErrorEntity, S> makeRequestWithBody (
            @NonNull HttpMethod httpMethod,
            @NonNull String path,
            @NonNull HttpHeaders headers,
            @NonNull Object requestBody,
            @NonNull Class<S> successResponseClass) {

        Objects.requireNonNull(httpMethod, "httpMethod cannot be null");
        Objects.requireNonNull(path, "path cannot be null");
        Objects.requireNonNull(headers, "headers cannot be null");
        Objects.requireNonNull(requestBody, "requestBody cannot be null");
        Objects.requireNonNull(successResponseClass, "successResponseClass cannot be null");

        try {
            S body = client.method(httpMethod)
                    .uri(builder -> builder.path(path).build())
                    .headers(h-> h.addAll(headers))
                    .body(requestBody)
                    .retrieve()
                    .toEntity(successResponseClass)
                    .getBody();

            return Either.right(body);

        } catch (HttpClientErrorException e) {
            log.debug("Client error when calling {} {}: {}", httpMethod, path, e.getMessage(), e);
            ErrorEntity error = e.getResponseBodyAs(ErrorEntity.class);
            return Either.left(error);
        } catch (HttpServerErrorException e) {
            log.error("Server error when calling {} {}: {}", httpMethod, path, e.getMessage(), e);
            ErrorEntity error = e.getResponseBodyAs(ErrorEntity.class);
            return Either.left(error);
        } catch (ResourceAccessException e) {
            log.error("Resource access error when calling {} {}: {}", httpMethod, path, e.getMessage(), e);
            ErrorEntity error =  new ErrorEntity(503, "API Unreachable", e.getMessage(), Instant.now().toString(), path);
            return Either.left(error);
        }
    }
}
