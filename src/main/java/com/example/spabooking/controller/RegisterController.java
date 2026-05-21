package com.example.spabooking.controller;

import com.example.spabooking.MainApp;
import com.example.spabooking.client.AuthClient;
import com.example.spabooking.client.dto.RegisterRequest;
import java.util.concurrent.CompletionException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

public class RegisterController {

    @FXML
    private VBox registerCard;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<String> genderComboBox;

    @FXML
    private Label errorLabel;

    @FXML
    private Button registerButton;

    @FXML
    private Button backButton;

    @FXML
    private ProgressIndicator loadingIndicator;

    private final AuthClient authClient = new AuthClient();
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        genderComboBox.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        genderComboBox.getSelectionModel().select("Nữ");
        loadingIndicator.setVisible(false);
        loadingIndicator.setManaged(false);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        registerCard.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleRegister();
            }
        });
    }

    @FXML
    private void handleRegister() {
        clearError();

        String fullName = valueOf(fullNameField);
        String username = valueOf(usernameField);
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        String confirmPassword = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText();
        String email = valueOf(emailField);
        String phone = valueOf(phoneField);
        String gender = genderComboBox.getValue();

        String validationError = validate(fullName, username, password, confirmPassword, email, phone);
        if (validationError != null) {
            showInlineError(validationError);
            return;
        }

        setLoading(true);
        RegisterRequest request = new RegisterRequest(fullName, username, password, email, phone, gender);
        authClient.register(request)
                .thenAccept(response -> Platform.runLater(this::showSuccessAndBackToLogin))
                .exceptionally(error -> {
                    Platform.runLater(() -> {
                        setLoading(false);
                        showInlineError(resolveMessage(error));
                    });
                    return null;
                });
    }

    @FXML
    private void handleBackToLogin() {
        openLogin();
    }

    private String validate(String fullName, String username, String password,
            String confirmPassword, String email, String phone) {
        if (fullName.isBlank()) {
            fullNameField.requestFocus();
            return "Họ và tên là bắt buộc.";
        }
        if (username.isBlank()) {
            usernameField.requestFocus();
            return "Tên đăng nhập là bắt buộc.";
        }
        if (password.isBlank()) {
            passwordField.requestFocus();
            return "Mật khẩu là bắt buộc.";
        }
        if (!confirmPassword.equals(password)) {
            confirmPasswordField.requestFocus();
            return "Xác nhận mật khẩu phải khớp với mật khẩu.";
        }
        if (email.isBlank()) {
            emailField.requestFocus();
            return "Email là bắt buộc.";
        }
        if (phone.isBlank()) {
            phoneField.requestFocus();
            return "Số điện thoại là bắt buộc.";
        }
        return null;
    }

    private void showSuccessAndBackToLogin() {
        setLoading(false);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Đăng ký thành công");
        alert.setHeaderText(null);
        alert.setContentText("Đăng ký tài khoản thành công. Vui lòng đăng nhập.");
        alert.showAndWait();
        openLogin();
    }

    private void openLogin() {
        try {
            mainApp.showLogin();
        } catch (Exception e) {
            showInlineError("Không thể quay về màn hình đăng nhập.");
        }
    }

    private void setLoading(boolean loading) {
        registerButton.setDisable(loading);
        backButton.setDisable(loading);
        fullNameField.setDisable(loading);
        usernameField.setDisable(loading);
        passwordField.setDisable(loading);
        confirmPasswordField.setDisable(loading);
        emailField.setDisable(loading);
        phoneField.setDisable(loading);
        genderComboBox.setDisable(loading);
        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
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
        String message = current.getMessage();
        return message == null || message.isBlank()
                ? AuthClient.REGISTER_FAILED_MESSAGE
                : message;
    }

    private String valueOf(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }
}
