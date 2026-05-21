package com.example.spabooking.controller;

import com.example.spabooking.MainApp;
import com.example.spabooking.client.AuthClient;
import com.example.spabooking.client.dto.LoginResponse;
import com.example.spabooking.session.SessionManager;
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
    private Button registerButton;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label errorLabel;

    private final AuthClient authClient = new AuthClient();
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
        usernameField.clear();
        passwordField.clear();

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
            showInlineError("Vui lòng nhập tên đăng nhập.");
            usernameField.requestFocus();
            return;
        }
        if (password.isBlank()) {
            showInlineError("Vui lòng nhập mật khẩu.");
            passwordField.requestFocus();
            return;
        }

        setLoading(true);
        authClient.login(username, password)
                .thenAccept(response -> Platform.runLater(() -> openDashboard(response)))
                .exceptionally(error -> {
                    Platform.runLater(() -> {
                        setLoading(false);
                        showInlineError(resolveMessage(error));
                    });
                    return null;
                });
    }

    @FXML
    private void handleOpenRegister() {
        clearError();
        try {
            mainApp.showRegister();
        } catch (Exception e) {
            showAlert("Lỗi giao diện", "Không thể mở màn hình đăng ký.");
        }
    }

    private void openDashboard(LoginResponse response) {
        setLoading(false);
        if (response == null || response.user() == null) {
            showInlineError(AuthClient.INVALID_LOGIN_MESSAGE);
            return;
        }

        try {
            SessionManager.login(response);
            mainApp.showDashboardByRole();
        } catch (IllegalArgumentException e) {
            SessionManager.clear();
            showInlineError(e.getMessage());
        } catch (Exception e) {
            SessionManager.clear();
            showInlineError("Không thể mở dashboard.");
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
                ? AuthClient.INVALID_LOGIN_MESSAGE
                : current.getMessage();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
