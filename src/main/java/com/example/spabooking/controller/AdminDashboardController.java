package com.example.spabooking.controller;

import com.example.spabooking.MainApp;
import com.example.spabooking.model.UserSession;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AdminDashboardController {

    @FXML
    private SidebarController sidebarController;

    @FXML
    private Label headerTitle;

    @FXML
    private Label headerSubtitle;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userRoleLabel;

    @FXML
    private StackPane contentArea;

    private MainApp mainApp;
    private UserSession session;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setSession(UserSession session) {
        this.session = session;
        userNameLabel.setText(session.displayName());
        userRoleLabel.setText(session.roleName());
        sidebarController.setRole(session.roleName());
        showDashboard();
    }

    @FXML
    private void initialize() {
        sidebarController.setNavigationHandler(this::handleNavigation);
        sidebarController.setLogoutHandler(this::handleLogout);
    }

    private void handleNavigation(String route) {
        switch (route) {
            case "dashboard" -> showDashboard();
            case "users" -> showPlaceholder("Users", "Quản lý tài khoản và phân quyền người dùng.");
            case "customers" -> showPlaceholder("Customers", "Quản lý hồ sơ, liên hệ và lịch sử đặt lịch của khách hàng.");
            case "employees" -> showPlaceholder("Employees", "Quản lý nhân viên, lịch làm việc và phân công.");
            case "services" -> showPlaceholder("Services", "Quản lý dịch vụ, giá tiền, thời lượng và trạng thái kinh doanh.");
            case "appointments" -> showPlaceholder("Appointments", "Theo dõi lịch hẹn và cập nhật trạng thái xử lý.");
            case "invoices" -> showPlaceholder("Invoices", "Quản lý hóa đơn, phương thức và trạng thái thanh toán.");
            case "sms" -> showPlaceholder("SMS", "Theo dõi nhắc lịch SMS mô phỏng và trạng thái gửi.");
            case "reports" -> showPlaceholder("Reports", "Tổng hợp doanh thu và hiệu quả dịch vụ.");
            default -> showDashboard();
        }
    }

    private void showDashboard() {
        headerTitle.setText("Dashboard");
        headerSubtitle.setText("Tổng quan vận hành Spa/Salon");
        contentArea.getChildren().setAll(createDashboardContent());
    }

    private VBox createDashboardContent() {
        VBox wrapper = new VBox(22);
        wrapper.getStyleClass().add("content-stack");
        wrapper.getChildren().addAll(
                createSectionTitle("Tổng quan hôm nay", "Các chỉ số nền tảng sẽ được nối API ở bước tiếp theo."),
                new DashboardMetricGrid(
                        new Metric("Lịch chờ xác nhận", "0", "Appointments"),
                        new Metric("Khách hàng", "0", "Customers"),
                        new Metric("Doanh thu", "0 đ", "Invoices"),
                        new Metric("SMS đã gửi", "0", "SMS")
                ),
                createSectionTitle("Luồng nghiệp vụ", "Khách chọn dịch vụ, tạo lịch, xác nhận, hoàn thành và thanh toán."),
                DashboardMetricGrid.workflow()
        );
        return wrapper;
    }

    private VBox createSectionTitle(String title, String subtitle) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("muted-label");
        VBox box = new VBox(4, titleLabel, subtitleLabel);
        return box;
    }

    private void showPlaceholder(String title, String subtitle) {
        headerTitle.setText(title);
        headerSubtitle.setText(subtitle);

        Label emptyIcon = new Label("✦");
        emptyIcon.getStyleClass().add("empty-icon");
        Label emptyTitle = new Label(title);
        emptyTitle.getStyleClass().add("empty-title");
        Label emptyText = new Label("Màn hình CRUD/API chi tiết sẽ được xây ở bước tiếp theo theo đúng phạm vi bạn yêu cầu.");
        emptyText.getStyleClass().add("empty-text");
        emptyText.setWrapText(true);

        VBox emptyState = new VBox(12, emptyIcon, emptyTitle, emptyText);
        emptyState.getStyleClass().add("empty-state");
        contentArea.getChildren().setAll(emptyState);
    }

    private void handleLogout() {
        try {
            mainApp.showLogin();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi giao diện");
            alert.setHeaderText(null);
            alert.setContentText("Không thể quay lại màn hình đăng nhập.");
            alert.showAndWait();
        }
    }

    public record Metric(String label, String value, String hint) {
    }
}
