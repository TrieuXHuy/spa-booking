package com.example.spabooking.controller;

import java.util.List;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class SidebarController {

    @FXML
    private Label roleBadge;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button usersButton;

    @FXML
    private Button customersButton;

    @FXML
    private Button employeesButton;

    @FXML
    private Button servicesButton;

    @FXML
    private Button appointmentsButton;

    @FXML
    private Button invoicesButton;

    @FXML
    private Button smsButton;

    @FXML
    private Button reportsButton;

    @FXML
    private Button logoutButton;

    private Consumer<String> navigationHandler = route -> {
    };
    private Runnable logoutHandler = () -> {
    };
    private List<Button> navButtons;

    @FXML
    private void initialize() {
        navButtons = List.of(
                dashboardButton,
                usersButton,
                customersButton,
                employeesButton,
                servicesButton,
                appointmentsButton,
                invoicesButton,
                smsButton,
                reportsButton
        );
        select(dashboardButton);
    }

    public void setNavigationHandler(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler == null ? route -> {
        } : navigationHandler;
    }

    public void setLogoutHandler(Runnable logoutHandler) {
        this.logoutHandler = logoutHandler == null ? () -> {
        } : logoutHandler;
    }

    public void setRole(String roleName) {
        roleBadge.setText(roleName == null || roleName.isBlank() ? "USER" : roleName.toUpperCase());
    }

    @FXML
    private void showDashboard() {
        navigate("dashboard", dashboardButton);
    }

    @FXML
    private void showUsers() {
        navigate("users", usersButton);
    }

    @FXML
    private void showCustomers() {
        navigate("customers", customersButton);
    }

    @FXML
    private void showEmployees() {
        navigate("employees", employeesButton);
    }

    @FXML
    private void showServices() {
        navigate("services", servicesButton);
    }

    @FXML
    private void showAppointments() {
        navigate("appointments", appointmentsButton);
    }

    @FXML
    private void showInvoices() {
        navigate("invoices", invoicesButton);
    }

    @FXML
    private void showSms() {
        navigate("sms", smsButton);
    }

    @FXML
    private void showReports() {
        navigate("reports", reportsButton);
    }

    @FXML
    private void logout() {
        logoutHandler.run();
    }

    private void navigate(String route, Button source) {
        select(source);
        navigationHandler.accept(route);
    }

    private void select(Button active) {
        for (Button button : navButtons) {
            button.getStyleClass().remove("active");
        }
        active.getStyleClass().add("active");
    }
}
