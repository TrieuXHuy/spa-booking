package com.example.spabooking;

import com.example.spabooking.controller.AdminDashboardController;
import com.example.spabooking.controller.LoginController;
import com.example.spabooking.model.UserSession;
import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static final String APP_TITLE = "Spa Booking Management";
    private static final double DEFAULT_WIDTH = 1200;
    private static final double DEFAULT_HEIGHT = 750;
    private static final double MIN_WIDTH = 1000;
    private static final double MIN_HEIGHT = 650;
    private static MainApp instance;

    private Stage primaryStage;

    public static MainApp getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) throws IOException {
        instance = this;
        primaryStage = stage;
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        showLogin();
    }

    public void showLogin() throws IOException {
        FXMLLoader loader = loadView("login.fxml");
        Parent root = loader.load();
        LoginController controller = loader.getController();
        controller.setMainApp(this);

        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        applyTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public void showAdminDashboard(UserSession session) throws IOException {
        FXMLLoader loader = loadView("admin-dashboard.fxml");
        Parent root = loader.load();
        AdminDashboardController controller = loader.getController();
        controller.setMainApp(this);
        controller.setSession(session);

        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        applyTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private FXMLLoader loadView(String viewName) {
        URL resource = MainApp.class.getResource("/com/example/spabooking/view/" + viewName);
        if (resource == null) {
            throw new IllegalStateException("Không tìm thấy giao diện: " + viewName);
        }
        return new FXMLLoader(resource);
    }

    private void applyTheme(Scene scene) {
        URL css = MainApp.class.getResource("/css/spa-theme.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
