package com.example.spabooking.client;

import com.example.spabooking.client.ApiClient.ApiException;
import com.example.spabooking.client.dto.LoginRequest;
import com.example.spabooking.client.dto.LoginResponse;
import com.example.spabooking.client.dto.RegisterRequest;
import com.example.spabooking.client.dto.RegisterResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class AuthClient {

    public static final String INVALID_LOGIN_MESSAGE = "Tên đăng nhập hoặc mật khẩu không đúng";
    public static final String REGISTER_FAILED_MESSAGE = "Đăng ký thất bại. Vui lòng kiểm tra lại thông tin.";

    private final ApiClient apiClient;

    public AuthClient() {
        this(new ApiClient());
    }

    public AuthClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<LoginResponse> login(String username, String password) {
        return apiClient.post("/api/auth/login", new LoginRequest(username, password), LoginResponse.class)
                .handle((response, error) -> {
                    if (error == null) {
                        return response;
                    }
                    Throwable cause = unwrap(error);
                    if (cause instanceof ApiException apiException
                            && ApiClient.CONNECTION_ERROR.equals(apiException.getMessage())) {
                        throw new CompletionException(apiException);
                    }
                    throw new CompletionException(new ApiException(INVALID_LOGIN_MESSAGE, cause));
                });
    }

    public CompletableFuture<RegisterResponse> register(RegisterRequest request) {
        return apiClient.post("/api/auth/register", request, RegisterResponse.class)
                .handle((response, error) -> {
                    if (error == null) {
                        return response;
                    }
                    Throwable cause = unwrap(error);
                    if (cause instanceof ApiException apiException
                            && ApiClient.CONNECTION_ERROR.equals(apiException.getMessage())) {
                        throw new CompletionException(apiException);
                    }
                    if (cause instanceof ApiException apiException) {
                        throw new CompletionException(new ApiException(resolveRegisterMessage(apiException), apiException));
                    }
                    throw new CompletionException(new ApiException(REGISTER_FAILED_MESSAGE, cause));
                });
    }

    private Throwable unwrap(Throwable error) {
        Throwable current = error;
        while (current instanceof CompletionException && current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    private String resolveRegisterMessage(ApiException apiException) {
        String message = apiException.getMessage();
        if (message == null
                || message.isBlank()
                || message.trim().startsWith("{")
                || "Not Found".equalsIgnoreCase(message)
                || "Bad Request".equalsIgnoreCase(message)
                || "Internal Server Error".equalsIgnoreCase(message)
                || message.startsWith("API trả về lỗi HTTP")) {
            return REGISTER_FAILED_MESSAGE;
        }
        return message;
    }
}
