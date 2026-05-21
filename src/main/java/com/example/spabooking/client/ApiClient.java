package com.example.spabooking.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ApiClient {

    public static final String CONNECTION_ERROR =
            "Không thể kết nối tới server. Vui lòng kiểm tra backend đã chạy chưa.";

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    private static final Duration TIMEOUT = Duration.ofSeconds(12);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public ApiClient() {
        this(System.getProperty("spabooking.api.base-url",
                System.getenv().getOrDefault("SPABOOKING_API_BASE_URL", DEFAULT_BASE_URL)));
    }

    public ApiClient(String baseUrl) {
        this.baseUrl = normalizeBaseUrl(baseUrl);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public <T> CompletableFuture<T> get(String path, Class<T> responseType) {
        return send(buildRequest(path).GET().build(), responseType);
    }

    public <T> CompletableFuture<T> get(String path, Map<String, ?> query, Class<T> responseType) {
        return get(path + toQueryString(query), responseType);
    }

    public <T> CompletableFuture<T> post(String path, Object body, Class<T> responseType) {
        return send(buildJsonRequest(path, body).POST(jsonBody(body)).build(), responseType);
    }

    public <T> CompletableFuture<T> put(String path, Object body, Class<T> responseType) {
        return send(buildJsonRequest(path, body).PUT(jsonBody(body)).build(), responseType);
    }

    public CompletableFuture<Void> delete(String path) {
        return send(buildRequest(path).DELETE().build(), Void.class);
    }

    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    public String baseUrl() {
        return baseUrl;
    }

    private <T> CompletableFuture<T> send(HttpRequest request, Class<T> responseType) {
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> parseResponse(response, responseType))
                .exceptionally(error -> {
                    Throwable cause = unwrap(error);
                    if (cause instanceof ConnectException || cause instanceof IOException) {
                        throw new ApiException(CONNECTION_ERROR, cause);
                    }
                    if (cause instanceof ApiException apiException) {
                        throw apiException;
                    }
                    throw new ApiException("Có lỗi xảy ra khi gọi API.", cause);
                });
    }

    private <T> T parseResponse(HttpResponse<String> response, Class<T> responseType) {
        int statusCode = response.statusCode();
        String body = response.body();
        if (statusCode < 200 || statusCode >= 300) {
            throw new ApiException(extractErrorMessage(body, statusCode), statusCode, body);
        }
        if (responseType == Void.class || body == null || body.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(body, responseType);
        } catch (JsonProcessingException e) {
            throw new ApiException("Dữ liệu phản hồi từ server không hợp lệ.", e);
        }
    }

    private HttpRequest.Builder buildRequest(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + normalizePath(path)))
                .timeout(TIMEOUT)
                .header("Accept", "application/json");
    }

    private HttpRequest.Builder buildJsonRequest(String path, Object body) {
        Objects.requireNonNull(body, "body");
        return buildRequest(path).header("Content-Type", "application/json");
    }

    private HttpRequest.BodyPublisher jsonBody(Object body) {
        try {
            return HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            throw new ApiException("Không thể tạo dữ liệu gửi lên server.", e);
        }
    }

    private String extractErrorMessage(String body, int statusCode) {
        if (body != null && !body.isBlank()) {
            try {
                var node = objectMapper.readTree(body);
                if (node.hasNonNull("message")) {
                    return node.get("message").asText();
                }
                if (node.hasNonNull("error")) {
                    return node.get("error").asText();
                }
            } catch (JsonProcessingException ignored) {
                return body;
            }
        }
        return "API trả về lỗi HTTP " + statusCode + ".";
    }

    private String toQueryString(Map<String, ?> query) {
        if (query == null || query.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder("?");
        query.forEach((key, value) -> {
            if (value != null) {
                if (builder.length() > 1) {
                    builder.append('&');
                }
                builder.append(encode(key)).append('=').append(encode(String.valueOf(value)));
            }
        });
        return builder.length() == 1 ? "" : builder.toString();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String normalizeBaseUrl(String value) {
        String url = value == null || value.isBlank() ? DEFAULT_BASE_URL : value.trim();
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "";
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    private Throwable unwrap(Throwable error) {
        Throwable current = error;
        while (current.getCause() != null
                && (current instanceof java.util.concurrent.CompletionException
                || current instanceof java.util.concurrent.ExecutionException)) {
            current = current.getCause();
        }
        return current;
    }

    public static class ApiException extends RuntimeException {
        private final int statusCode;
        private final String responseBody;

        public ApiException(String message, Throwable cause) {
            super(message, cause);
            this.statusCode = -1;
            this.responseBody = null;
        }

        public ApiException(String message, int statusCode, String responseBody) {
            super(message);
            this.statusCode = statusCode;
            this.responseBody = responseBody;
        }

        public int statusCode() {
            return statusCode;
        }

        public String responseBody() {
            return responseBody;
        }
    }
}
