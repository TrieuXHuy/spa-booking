package com.example.spabooking.controller;

import com.example.spabooking.MainApp;
import com.example.spabooking.client.ApiClient;
import com.example.spabooking.model.UserSession;
import java.io.IOException;
import java.util.concurrent.CompletionException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML
    private VBox loginCard;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label errorLabel;

    private final ApiClient apiClient = new ApiClient();
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        loadingIndicator.setVisible(false);
        loadingIndicator.setManaged(false);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        loginCard.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });
    }

    @FXML
    private void handleLogin() {
        clearError();
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (username.isBlank()) {
            showInlineError("Vui lòng nhập email hoặc số điện thoại.");
            usernameField.requestFocus();
            return;
        }
        if (password.isBlank()) {
            showInlineError("Vui lòng nhập mật khẩu.");
            passwordField.requestFocus();
            return;
        }

        setLoading(true);
        apiClient.post("/api/auth/login", new LoginRequest(username, password), LoginResponse.class)
                .thenAccept(response -> Platform.runLater(() -> openDashboard(response)))
                .exceptionally(error -> {
                    Platform.runLater(() -> {
                        setLoading(false);
                        showInlineError(resolveMessage(error));
                    });
                    return null;
                });
    }

    private void openDashboard(LoginResponse response) {
        setLoading(false);
        if (response == null || response.user() == null) {
            showInlineError("Thông tin đăng nhập không hợp lệ.");
            return;
        }
        UserResponse user = response.user();
        String roleName = user.role() == null ? "" : user.role().name();
        UserSession session = new UserSession(
                user.id(),
                user.username(),
                user.fullName(),
                user.email(),
                user.phone(),
                roleName
        );

        try {
            mainApp.showAdminDashboard(session);
        } catch (IOException e) {
            showAlert("Lỗi giao diện", "Không thể mở dashboard.");
        }
    }

    private void setLoading(boolean loading) {
        loginButton.setDisable(loading);
        usernameField.setDisable(loading);
        passwordField.setDisable(loading);
        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        usernameField.getStyleClass().remove("field-error");
        passwordField.getStyleClass().remove("field-error");
    }

    private void showInlineError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private String resolveMessage(Throwable error) {
        Throwable current = error;
        while (current instanceof CompletionException && current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() == null || current.getMessage().isBlank()
                ? "Đăng nhập thất bại. Vui lòng thử lại."
                : current.getMessage();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private record LoginRequest(String username, String password) {
    }

    private record LoginResponse(String message, UserResponse user) {
    }

    private record UserResponse(
            Long id,
            String username,
            String fullName,
            String email,
            String phone,
            Boolean active,
            RoleResponse role
    ) {
    }

    private record RoleResponse(Long id, String name, String description) {
    }
}
