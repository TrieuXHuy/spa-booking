package com.example.spabooking.controller;

import com.example.spabooking.MainApp;
import com.example.spabooking.client.AdminClient;
import com.example.spabooking.client.dto.AppointmentDto;
import com.example.spabooking.client.dto.AppointmentRequestDto;
import com.example.spabooking.client.dto.AppointmentServiceDto;
import com.example.spabooking.client.dto.AppointmentStatusRequestDto;
import com.example.spabooking.client.dto.CustomerDto;
import com.example.spabooking.client.dto.CustomerRequestDto;
import com.example.spabooking.client.dto.EmployeeDto;
import com.example.spabooking.client.dto.EmployeeRequestDto;
import com.example.spabooking.client.dto.EmployeeScheduleDto;
import com.example.spabooking.client.dto.EmployeeScheduleRequestDto;
import com.example.spabooking.client.dto.InvoiceDto;
import com.example.spabooking.client.dto.InvoicePaymentRequestDto;
import com.example.spabooking.client.dto.InvoiceRequestDto;
import com.example.spabooking.client.dto.InvoiceStatusRequestDto;
import com.example.spabooking.client.dto.RoleDto;
import com.example.spabooking.client.dto.ServiceDto;
import com.example.spabooking.client.dto.ServiceRequestDto;
import com.example.spabooking.client.dto.SmsReminderDto;
import com.example.spabooking.client.dto.SmsReminderRequestDto;
import com.example.spabooking.client.dto.SmsReminderStatusRequestDto;
import com.example.spabooking.client.dto.UserDto;
import com.example.spabooking.client.dto.UserRequestDto;
import com.example.spabooking.client.dto.UserUpdateRequestDto;
import com.example.spabooking.model.UserSession;
import com.example.spabooking.session.SessionManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AdminDashboardController {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final List<String> APPOINTMENT_STATUSES = List.of(
            "Chờ xác nhận", "Đã xác nhận", "Đang thực hiện", "Hoàn thành", "Đã hủy", "Khách không đến");
    private static final List<String> PAYMENT_METHODS = List.of("Tiền mặt", "Chuyển khoản", "Thẻ ngân hàng", "Ví điện tử");
    private static final List<String> PAYMENT_STATUSES = List.of("Chưa thanh toán", "Đã thanh toán", "Đã hủy", "Hoàn tiền");
    private static final List<String> SMS_STATUSES = List.of("Chưa gửi", "Đã gửi", "Gửi lỗi");

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

    private final AdminClient adminClient = new AdminClient();
    private final ObservableList<UserDto> users = FXCollections.observableArrayList();
    private final ObservableList<RoleDto> roles = FXCollections.observableArrayList();
    private final ObservableList<CustomerDto> customers = FXCollections.observableArrayList();
    private final ObservableList<EmployeeDto> employees = FXCollections.observableArrayList();
    private final ObservableList<ServiceDto> services = FXCollections.observableArrayList();
    private final ObservableList<AppointmentDto> appointments = FXCollections.observableArrayList();
    private final ObservableList<InvoiceDto> invoices = FXCollections.observableArrayList();
    private final ObservableList<EmployeeScheduleDto> schedules = FXCollections.observableArrayList();
    private final ObservableList<SmsReminderDto> smsReminders = FXCollections.observableArrayList();

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setSession(UserSession session) {
        userNameLabel.setText(session.displayName());
        userRoleLabel.setText(session.roleName());
        sidebarController.setRole(session.roleName());
        showDashboard();
    }

    @FXML
    private void initialize() {
        sidebarController.setNavigationHandler(this::handleNavigation);
        sidebarController.setLogoutHandler(this::handleLogout);
        loadRoles();
    }

    private void handleNavigation(String route) {
        switch (route) {
            case "dashboard" -> showDashboard();
            case "users" -> showUsers();
            case "customers" -> showCustomers();
            case "employees" -> showEmployees();
            case "services" -> showServices();
            case "schedules" -> showSchedules();
            case "appointments" -> showAppointments();
            case "invoices" -> showInvoices();
            case "sms" -> showSms();
            case "reports" -> showReports();
            default -> showDashboard();
        }
    }

    private void showUsers() {
        headerTitle.setText("Users");
        headerSubtitle.setText("Quản lý tài khoản, vai trò và trạng thái người dùng.");
        FilteredList<UserDto> filtered = new FilteredList<>(users, item -> true);
        TableView<UserDto> table = table("Chưa có tài khoản nào.");
        table.getColumns().setAll(
                column("ID", 62, user -> text(user.id())),
                column("Tài khoản", 130, UserDto::username),
                column("Họ tên", 180, UserDto::fullName),
                column("Liên hệ", 220, user -> joinLines(user.email(), user.phone())),
                column("Vai trò", 110, UserDto::roleName),
                column("Trạng thái", 120, user -> activeText(user.active())),
                column("Ngày tạo", 140, user -> dateTime(user.createdAt()))
        );
        table.setItems(filtered);
        Runnable refresh = () -> runLoad(adminClient.findUsers(), users::setAll, "Không thể tải danh sách tài khoản");
        Node content = crudContent(
                "Danh sách tài khoản",
                "Thêm, sửa, xóa tài khoản và phân quyền người dùng.",
                "Tìm theo tài khoản, họ tên, email, số điện thoại, vai trò",
                filtered,
                table,
                user -> searchable(user.username(), user.fullName(), user.email(), user.phone(), user.roleName()),
                "Thêm tài khoản",
                () -> showUserDialog(null, refresh),
                () -> showUserDialog(table.getSelectionModel().getSelectedItem(), refresh),
                () -> deleteSelected(table.getSelectionModel().getSelectedItem(), "tài khoản", user -> adminClient.deleteUser(user.id()), refresh),
                refresh);
        contentArea.getChildren().setAll(content);
        refresh.run();
    }

    private void showCustomers() {
        headerTitle.setText("Customers");
        headerSubtitle.setText("Quản lý hồ sơ, liên hệ và lịch sử đặt lịch của khách hàng.");
        FilteredList<CustomerDto> filtered = new FilteredList<>(customers, item -> true);
        TableView<CustomerDto> table = table("Chưa có khách hàng nào.");
        table.getColumns().setAll(
                column("ID", 62, item -> text(item.id())),
                column("Họ tên", 180, CustomerDto::fullName),
                column("Liên hệ", 220, item -> joinLines(item.email(), item.phone())),
                column("Tài khoản", 120, CustomerDto::username),
                column("Giới tính", 90, CustomerDto::gender),
                column("Ngày sinh", 110, item -> date(item.dateOfBirth())),
                column("Ghi chú", 220, CustomerDto::note)
        );
        table.setItems(filtered);
        Runnable refresh = () -> runLoad(adminClient.findCustomers(), customers::setAll, "Không thể tải khách hàng");
        Node content = crudContent(
                "Danh sách khách hàng",
                "Lưu hồ sơ khách hàng phục vụ đặt lịch và chăm sóc.",
                "Tìm theo họ tên, số điện thoại, email, tài khoản",
                filtered,
                table,
                item -> searchable(item.fullName(), item.phone(), item.email(), item.username(), item.note()),
                "Thêm khách hàng",
                () -> showCustomerDialog(null, refresh),
                () -> showCustomerDialog(table.getSelectionModel().getSelectedItem(), refresh),
                () -> deleteSelected(table.getSelectionModel().getSelectedItem(), "khách hàng", item -> adminClient.deleteCustomer(item.id()), refresh),
                refresh);
        contentArea.getChildren().setAll(content);
        refresh.run();
    }

    private void showEmployees() {
        headerTitle.setText("Employees");
        headerSubtitle.setText("Quản lý nhân viên, hồ sơ kỹ năng và trạng thái làm việc.");
        FilteredList<EmployeeDto> filtered = new FilteredList<>(employees, item -> true);
        TableView<EmployeeDto> table = table("Chưa có nhân viên nào.");
        table.getColumns().setAll(
                column("ID", 62, item -> text(item.id())),
                column("Họ tên", 170, EmployeeDto::fullName),
                column("Tài khoản", 120, EmployeeDto::username),
                column("Liên hệ", 210, item -> joinLines(item.email(), item.phone())),
                column("Vị trí", 140, EmployeeDto::position),
                column("Kỹ năng", 220, EmployeeDto::skillNote),
                column("Trạng thái", 120, item -> activeText(item.active()))
        );
        table.setItems(filtered);
        Runnable refresh = () -> runLoad(adminClient.findEmployees(), employees::setAll, "Không thể tải nhân viên");
        Node content = crudContent(
                "Danh sách nhân viên",
                "Gắn nhân viên với tài khoản EMPLOYEE và quản lý thông tin làm việc.",
                "Tìm theo họ tên, tài khoản, liên hệ, vị trí, kỹ năng",
                filtered,
                table,
                item -> searchable(item.fullName(), item.username(), item.phone(), item.email(), item.position(), item.skillNote()),
                "Thêm nhân viên",
                () -> showEmployeeDialog(null, refresh),
                () -> showEmployeeDialog(table.getSelectionModel().getSelectedItem(), refresh),
                () -> deleteSelected(table.getSelectionModel().getSelectedItem(), "nhân viên", item -> adminClient.deleteEmployee(item.id()), refresh),
                refresh);
        contentArea.getChildren().setAll(content);
        refresh.run();
    }

    private void showServices() {
        headerTitle.setText("Services");
        headerSubtitle.setText("Quản lý dịch vụ, giá tiền, thời lượng và trạng thái kinh doanh.");
        FilteredList<ServiceDto> filtered = new FilteredList<>(services, item -> true);
        TableView<ServiceDto> table = table("Chưa có dịch vụ nào.");
        table.getColumns().setAll(
                column("ID", 62, item -> text(item.id())),
                column("Dịch vụ", 180, ServiceDto::name),
                column("Giá", 120, item -> money(item.price())),
                column("Thời lượng", 110, item -> text(item.durationMinutes()) + " phút"),
                column("Trạng thái", 120, item -> activeText(item.active())),
                column("Mô tả", 360, ServiceDto::description)
        );
        table.setItems(filtered);
        Runnable refresh = () -> runLoad(adminClient.findServices(), services::setAll, "Không thể tải dịch vụ");
        Node content = crudContent(
                "Danh sách dịch vụ",
                "Thiết lập dịch vụ spa/salon để khách đặt lịch.",
                "Tìm theo tên, mô tả, giá, trạng thái",
                filtered,
                table,
                item -> searchable(item.name(), item.description(), money(item.price()), activeText(item.active())),
                "Thêm dịch vụ",
                () -> showServiceDialog(null, refresh),
                () -> showServiceDialog(table.getSelectionModel().getSelectedItem(), refresh),
                () -> deleteSelected(table.getSelectionModel().getSelectedItem(), "dịch vụ", item -> adminClient.deleteService(item.id()), refresh),
                refresh);
        contentArea.getChildren().setAll(content);
        refresh.run();
    }

    private void showAppointments() {
        headerTitle.setText("Appointments");
        headerSubtitle.setText("Theo dõi lịch hẹn, dịch vụ, nhân viên và trạng thái xử lý.");
        FilteredList<AppointmentDto> filtered = new FilteredList<>(appointments, item -> true);
        TableView<AppointmentDto> table = table("Chưa có lịch hẹn nào.");
        table.getColumns().setAll(
                column("ID", 62, item -> text(item.id())),
                column("Khách hàng", 160, AppointmentDto::customerName),
                column("Nhân viên", 150, item -> valueOrEmpty(item.employeeName())),
                column("Ngày", 110, item -> date(item.appointmentDate())),
                column("Giờ", 110, item -> timeRange(item.startTime(), item.endTime())),
                column("Dịch vụ", 230, item -> serviceNames(item.services())),
                column("Trạng thái", 140, AppointmentDto::status),
                column("Ghi chú", 180, AppointmentDto::note)
        );
        table.setItems(filtered);
        Runnable refresh = () -> runLoad(adminClient.findAppointments(), appointments::setAll, "Không thể tải lịch hẹn");
        Node content = crudContent(
                "Danh sách lịch hẹn",
                "Tạo lịch, cập nhật dịch vụ/nhân viên và chuyển trạng thái theo luồng nghiệp vụ.",
                "Tìm theo khách hàng, nhân viên, dịch vụ, ngày, trạng thái",
                filtered,
                table,
                item -> searchable(item.customerName(), item.employeeName(), date(item.appointmentDate()), item.status(), serviceNames(item.services()), item.note()),
                "Thêm lịch hẹn",
                () -> showAppointmentDialog(null, refresh),
                () -> showAppointmentDialog(table.getSelectionModel().getSelectedItem(), refresh),
                () -> deleteSelected(table.getSelectionModel().getSelectedItem(), "lịch hẹn", item -> adminClient.deleteAppointment(item.id()), refresh),
                refresh);

        Button statusButton = button("Đổi trạng thái", "secondary-button",
                () -> showAppointmentStatusDialog(table.getSelectionModel().getSelectedItem(), refresh));
        ((HBox) ((VBox) ((VBox) content).getChildren().get(1)).getChildren().get(0)).getChildren().add(statusButton);

        contentArea.getChildren().setAll(content);
        refresh.run();
        ensureReferenceDataLoaded();
    }

    private void showInvoices() {
        headerTitle.setText("Invoices");
        headerSubtitle.setText("Quản lý hóa đơn, thanh toán và trạng thái công nợ.");
        FilteredList<InvoiceDto> filtered = new FilteredList<>(invoices, item -> true);
        TableView<InvoiceDto> table = table("Chưa có hóa đơn nào.");
        table.getColumns().setAll(
                column("ID", 62, item -> text(item.id())),
                column("Lịch hẹn", 90, item -> text(item.appointmentId())),
                column("Khách hàng", 160, InvoiceDto::customerName),
                column("Tổng tiền", 120, item -> money(item.totalAmount())),
                column("Giảm giá", 110, item -> money(item.discountAmount())),
                column("Cần trả", 120, item -> money(item.finalAmount())),
                column("Phương thức", 130, InvoiceDto::paymentMethod),
                column("Trạng thái", 140, InvoiceDto::paymentStatus),
                column("Ngày tạo", 140, item -> dateTime(item.createdAt()))
        );
        table.setItems(filtered);
        Runnable refresh = () -> runLoad(adminClient.findInvoices(), invoices::setAll, "Không thể tải hóa đơn");
        Node content = crudContent(
                "Danh sách hóa đơn",
                "Tạo hóa đơn cho lịch đã hoàn thành và cập nhật thanh toán.",
                "Tìm theo khách hàng, lịch hẹn, phương thức, trạng thái",
                filtered,
                table,
                item -> searchable(text(item.appointmentId()), item.customerName(), item.paymentMethod(), item.paymentStatus(), money(item.finalAmount())),
                "Tạo hóa đơn",
                () -> showInvoiceDialog(refresh),
                () -> showPayInvoiceDialog(table.getSelectionModel().getSelectedItem(), refresh),
                () -> deleteSelected(table.getSelectionModel().getSelectedItem(), "hóa đơn", item -> adminClient.deleteInvoice(item.id()), refresh),
                refresh);

        HBox toolbar = (HBox) ((VBox) ((VBox) content).getChildren().get(1)).getChildren().get(0);
        ((Button) toolbar.getChildren().get(3)).setText("Thanh toán");
        Button statusButton = button("Đổi trạng thái", "secondary-button",
                () -> showInvoiceStatusDialog(table.getSelectionModel().getSelectedItem(), refresh));
        toolbar.getChildren().add(statusButton);

        contentArea.getChildren().setAll(content);
        refresh.run();
        ensureReferenceDataLoaded();
    }

    private void showSchedules() {
        headerTitle.setText("Schedules");
        headerSubtitle.setText("Quản lý ca làm việc của nhân viên theo ngày.");
        FilteredList<EmployeeScheduleDto> filtered = new FilteredList<>(schedules, item -> true);
        TableView<EmployeeScheduleDto> table = table("Chưa có lịch làm việc nào.");
        table.getColumns().setAll(
                column("ID", 62, item -> text(item.id())),
                column("Nhân viên", 180, EmployeeScheduleDto::employeeName),
                column("Ngày làm", 120, item -> date(item.workDate())),
                column("Giờ làm", 140, item -> timeRange(item.startTime(), item.endTime())),
                column("Ghi chú", 360, EmployeeScheduleDto::note)
        );
        table.setItems(filtered);
        Runnable refresh = () -> runLoad(adminClient.findEmployeeSchedules(), schedules::setAll, "Không thể tải lịch làm việc");
        Node content = crudContent(
                "Lịch làm nhân viên",
                "Thiết lập ca làm để phân công nhân viên cho lịch hẹn.",
                "Tìm theo nhân viên, ngày, giờ, ghi chú",
                filtered,
                table,
                item -> searchable(item.employeeName(), date(item.workDate()), timeRange(item.startTime(), item.endTime()), item.note()),
                "Thêm ca làm",
                () -> showScheduleDialog(null, refresh),
                () -> showScheduleDialog(table.getSelectionModel().getSelectedItem(), refresh),
                () -> deleteSelected(table.getSelectionModel().getSelectedItem(), "ca làm", item -> adminClient.deleteEmployeeSchedule(item.id()), refresh),
                refresh);
        contentArea.getChildren().setAll(content);
        refresh.run();
        ensureReferenceDataLoaded();
    }

    private void showSms() {
        headerTitle.setText("SMS");
        headerSubtitle.setText("Quản lý nhắc lịch qua SMS cho khách hàng.");
        FilteredList<SmsReminderDto> filtered = new FilteredList<>(smsReminders, item -> true);
        TableView<SmsReminderDto> table = table("Chưa có SMS nhắc lịch nào.");
        table.getColumns().setAll(
                column("ID", 62, item -> text(item.id())),
                column("Lịch hẹn", 90, item -> text(item.appointmentId())),
                column("Khách hàng", 150, SmsReminderDto::customerName),
                column("Số điện thoại", 130, SmsReminderDto::phone),
                column("Nội dung", 300, SmsReminderDto::message),
                column("Trạng thái", 120, SmsReminderDto::status),
                column("Ngày gửi", 140, item -> dateTime(item.sentAt())),
                column("Lỗi", 180, SmsReminderDto::errorMessage)
        );
        table.setItems(filtered);
        Runnable refresh = () -> runLoad(adminClient.findSmsReminders(), smsReminders::setAll, "Không thể tải SMS");
        Node content = crudContent(
                "Danh sách SMS nhắc lịch",
                "Tạo nội dung nhắc lịch, đánh dấu gửi thành công hoặc ghi nhận lỗi.",
                "Tìm theo khách hàng, số điện thoại, nội dung, trạng thái",
                filtered,
                table,
                item -> searchable(text(item.appointmentId()), item.customerName(), item.phone(), item.message(), item.status(), item.errorMessage()),
                "Tạo SMS",
                () -> showSmsDialog(refresh),
                () -> showSmsStatusDialog(table.getSelectionModel().getSelectedItem(), refresh),
                () -> deleteSelected(table.getSelectionModel().getSelectedItem(), "SMS", item -> adminClient.deleteSmsReminder(item.id()), refresh),
                refresh);

        HBox toolbar = (HBox) ((VBox) ((VBox) content).getChildren().get(1)).getChildren().get(0);
        ((Button) toolbar.getChildren().get(3)).setText("Đổi trạng thái");

        contentArea.getChildren().setAll(content);
        refresh.run();
        ensureReferenceDataLoaded();
    }

    private void showReports() {
        headerTitle.setText("Reports");
        headerSubtitle.setText("Tổng hợp dữ liệu vận hành từ khách hàng, lịch hẹn, hóa đơn và SMS.");
        Runnable render = () -> {
            long activeEmployees = employees.stream().filter(item -> Boolean.TRUE.equals(item.active())).count();
            long activeServices = services.stream().filter(item -> Boolean.TRUE.equals(item.active())).count();
            long pendingAppointments = appointments.stream().filter(item -> "Chờ xác nhận".equals(item.status())).count();
            long completedAppointments = appointments.stream().filter(item -> "Hoàn thành".equals(item.status())).count();
            long paidInvoices = invoices.stream().filter(item -> "Đã thanh toán".equals(item.paymentStatus())).count();
            long sentSms = smsReminders.stream().filter(item -> "Đã gửi".equals(item.status())).count();
            BigDecimal revenue = invoices.stream()
                    .filter(item -> "Đã thanh toán".equals(item.paymentStatus()))
                    .map(InvoiceDto::finalAmount)
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Button refreshButton = button("Làm mới báo cáo", "primary-button", () -> refreshAllData(() -> Platform.runLater(this::showReports)));
            HBox toolbar = new HBox(refreshButton);
            toolbar.setAlignment(Pos.CENTER_RIGHT);

            TextArea summary = new TextArea();
            summary.setEditable(false);
            summary.setWrapText(true);
            summary.setText("""
                    Trạng thái vận hành
                    - Lịch chờ xác nhận: %s
                    - Lịch đã hoàn thành: %s
                    - Hóa đơn đã thanh toán: %s
                    - SMS đã gửi: %s

                    Gợi ý xử lý
                    - Xác nhận các lịch đang chờ trước khi tạo SMS nhắc lịch.
                    - Chỉ tạo hóa đơn khi lịch hẹn đã hoàn thành.
                    - Theo dõi hóa đơn chưa thanh toán để chốt công nợ cuối ngày.
                    """.formatted(pendingAppointments, completedAppointments, paidInvoices, sentSms));
            VBox.setVgrow(summary, Priority.ALWAYS);

            VBox wrapper = new VBox(18,
                    createSectionTitle("Báo cáo tổng quan", "Các chỉ số được tổng hợp trực tiếp từ dữ liệu hiện có."),
                    toolbar,
                    new DashboardMetricGrid(
                            new Metric("Khách hàng", text(customers.size()), "Customers"),
                            new Metric("Nhân viên hoạt động", text(activeEmployees), "Employees"),
                            new Metric("Dịch vụ đang bán", text(activeServices), "Services"),
                            new Metric("Doanh thu đã thu", money(revenue), "Invoices")
                    ),
                    summary);
            wrapper.getStyleClass().add("content-stack");
            contentArea.getChildren().setAll(wrapper);
        };
        if (customers.isEmpty() && employees.isEmpty() && services.isEmpty() && appointments.isEmpty() && invoices.isEmpty()) {
            refreshAllData(() -> Platform.runLater(render));
        } else {
            render.run();
        }
    }

    private <T> Node crudContent(String title, String subtitle, String prompt, FilteredList<T> filtered,
            TableView<T> table, Function<T, String> searchText, String createLabel, Runnable createAction,
            Runnable editAction, Runnable deleteAction, Runnable refreshAction) {
        TextField searchField = new TextField();
        searchField.setPromptText(prompt);
        searchField.getStyleClass().add("admin-search-field");
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                filtered.setPredicate(item -> matches(searchText.apply(item), newValue)));

        Label summaryLabel = new Label();
        summaryLabel.getStyleClass().add("muted-label");
        Runnable updateSummary = () -> summaryLabel.setText("Hiển thị " + filtered.size() + " bản ghi");
        filtered.addListener((javafx.collections.ListChangeListener<T>) change -> updateSummary.run());

        Button refreshButton = button("Làm mới", "secondary-button", refreshAction);
        Button createButton = button(createLabel, "primary-button", createAction);
        Button editButton = button("Sửa", "secondary-button", editAction);
        Button deleteButton = button("Xóa", "danger-button", deleteAction);

        HBox toolbar = new HBox(10, searchField, refreshButton, createButton, editButton, deleteButton);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        VBox.setVgrow(table, Priority.ALWAYS);
        VBox panel = new VBox(14, toolbar, summaryLabel, table);
        panel.getStyleClass().add("admin-panel");
        VBox.setVgrow(panel, Priority.ALWAYS);
        updateSummary.run();

        VBox wrapper = new VBox(18, createSectionTitle(title, subtitle), panel);
        wrapper.getStyleClass().add("content-stack");
        return wrapper;
    }

    private void showUserDialog(UserDto user, Runnable refresh) {
        boolean editing = user != null;
        Form form = new Form(editing ? "Sửa tài khoản" : "Thêm tài khoản");
        TextField username = form.text("Tài khoản", editing ? user.username() : "");
        PasswordField password = form.password("Mật khẩu", "");
        TextField fullName = form.text("Họ tên", editing ? user.fullName() : "");
        TextField email = form.text("Email", editing ? user.email() : "");
        TextField phone = form.text("Số điện thoại", editing ? user.phone() : "");
        ComboBox<RoleDto> role = form.combo("Vai trò", roles, RoleDto::name);
        role.getSelectionModel().select(findRole(editing ? user.roleName() : "CUSTOMER"));
        CheckBox active = form.check("Đang hoạt động", !editing || Boolean.TRUE.equals(user.active()));
        if (editing) {
            username.setDisable(true);
            password.setPromptText("Không đổi mật khẩu khi sửa");
        }
        form.show().ifPresent(ok -> {
            if (isBlank(fullName.getText()) || (!editing && (isBlank(username.getText()) || isBlank(password.getText())))) {
                showInlineError("Thiếu thông tin", "Vui lòng nhập tài khoản, mật khẩu và họ tên.");
                return;
            }
            RoleDto selectedRole = role.getValue();
            if (selectedRole == null) {
                showInlineError("Thiếu vai trò", "Vui lòng chọn vai trò.");
                return;
            }
            CompletableFuture<UserDto> call = editing
                    ? adminClient.updateUser(user.id(), new UserUpdateRequestDto(fullName.getText().trim(), blankToNull(email.getText()),
                            blankToNull(phone.getText()), selectedRole.id(), active.isSelected()))
                    : adminClient.createUser(new UserRequestDto(username.getText().trim(), password.getText().trim(), fullName.getText().trim(),
                            blankToNull(email.getText()), blankToNull(phone.getText()), selectedRole.id(), active.isSelected()));
            runMutation(call, refresh, "Không thể lưu tài khoản");
        });
    }

    private void showCustomerDialog(CustomerDto customer, Runnable refresh) {
        boolean editing = customer != null;
        Form form = new Form(editing ? "Sửa khách hàng" : "Thêm khách hàng");
        TextField fullName = form.text("Họ tên", editing ? customer.fullName() : "");
        TextField phone = form.text("Số điện thoại", editing ? customer.phone() : "");
        TextField email = form.text("Email", editing ? customer.email() : "");
        TextField gender = form.text("Giới tính", editing ? customer.gender() : "");
        TextField birthDate = form.text("Ngày sinh (yyyy-MM-dd)", editing && customer.dateOfBirth() != null ? customer.dateOfBirth().toString() : "");
        TextArea note = form.area("Ghi chú", editing ? customer.note() : "");
        form.show().ifPresent(ok -> {
            if (isBlank(fullName.getText()) || isBlank(phone.getText())) {
                showInlineError("Thiếu thông tin", "Vui lòng nhập họ tên và số điện thoại.");
                return;
            }
            CustomerRequestDto request = new CustomerRequestDto(editing ? customer.userId() : null, fullName.getText().trim(), phone.getText().trim(),
                    blankToNull(email.getText()), blankToNull(gender.getText()), parseDate(birthDate.getText()), blankToNull(note.getText()));
            CompletableFuture<CustomerDto> call = editing ? adminClient.updateCustomer(customer.id(), request) : adminClient.createCustomer(request);
            runMutation(call, refresh, "Không thể lưu khách hàng");
        });
    }

    private void showEmployeeDialog(EmployeeDto employee, Runnable refresh) {
        boolean editing = employee != null;
        Form form = new Form(editing ? "Sửa nhân viên" : "Thêm nhân viên");
        ComboBox<UserDto> user = form.combo("Tài khoản nhân viên", users, item -> item.username() + " - " + item.fullName());
        user.getSelectionModel().select(findUser(editing ? employee.userId() : null));
        TextField fullName = form.text("Họ tên", editing ? employee.fullName() : "");
        TextField phone = form.text("Số điện thoại", editing ? employee.phone() : "");
        TextField email = form.text("Email", editing ? employee.email() : "");
        TextField position = form.text("Vị trí", editing ? employee.position() : "");
        TextArea skillNote = form.area("Kỹ năng", editing ? employee.skillNote() : "");
        CheckBox active = form.check("Đang làm việc", !editing || Boolean.TRUE.equals(employee.active()));
        form.show().ifPresent(ok -> {
            UserDto selectedUser = user.getValue();
            if (selectedUser == null || isBlank(fullName.getText())) {
                showInlineError("Thiếu thông tin", "Vui lòng chọn tài khoản và nhập họ tên.");
                return;
            }
            EmployeeRequestDto request = new EmployeeRequestDto(selectedUser.id(), fullName.getText().trim(), blankToNull(phone.getText()),
                    blankToNull(email.getText()), blankToNull(position.getText()), blankToNull(skillNote.getText()), active.isSelected());
            CompletableFuture<EmployeeDto> call = editing ? adminClient.updateEmployee(employee.id(), request) : adminClient.createEmployee(request);
            runMutation(call, refresh, "Không thể lưu nhân viên");
        });
    }

    private void showServiceDialog(ServiceDto service, Runnable refresh) {
        boolean editing = service != null;
        Form form = new Form(editing ? "Sửa dịch vụ" : "Thêm dịch vụ");
        TextField name = form.text("Tên dịch vụ", editing ? service.name() : "");
        TextField price = form.text("Giá tiền", editing && service.price() != null ? service.price().toPlainString() : "");
        TextField duration = form.text("Thời lượng phút", editing ? text(service.durationMinutes()) : "");
        TextArea description = form.area("Mô tả", editing ? service.description() : "");
        CheckBox active = form.check("Đang kinh doanh", !editing || Boolean.TRUE.equals(service.active()));
        form.show().ifPresent(ok -> {
            if (isBlank(name.getText()) || isBlank(price.getText()) || isBlank(duration.getText())) {
                showInlineError("Thiếu thông tin", "Vui lòng nhập tên dịch vụ, giá tiền và thời lượng.");
                return;
            }
            try {
                ServiceRequestDto request = new ServiceRequestDto(name.getText().trim(), blankToNull(description.getText()),
                        new BigDecimal(price.getText().trim()), Integer.parseInt(duration.getText().trim()), active.isSelected());
                CompletableFuture<ServiceDto> call = editing ? adminClient.updateService(service.id(), request) : adminClient.createService(request);
                runMutation(call, refresh, "Không thể lưu dịch vụ");
            } catch (NumberFormatException exception) {
                showInlineError("Dữ liệu không hợp lệ", "Giá tiền và thời lượng phải là số.");
            }
        });
    }

    private void showAppointmentDialog(AppointmentDto appointment, Runnable refresh) {
        boolean editing = appointment != null;
        ensureReferenceDataLoaded();
        Form form = new Form(editing ? "Sửa lịch hẹn" : "Thêm lịch hẹn");
        ComboBox<CustomerDto> customer = form.combo("Khách hàng", customers, item -> item.fullName() + " - " + item.phone());
        customer.getSelectionModel().select(findCustomer(editing ? appointment.customerId() : null));
        ComboBox<EmployeeDto> employee = form.combo("Nhân viên", employees, item -> item.fullName() + " - " + valueOrEmpty(item.position()));
        employee.getItems().add(0, null);
        employee.getSelectionModel().select(findEmployee(editing ? appointment.employeeId() : null));
        TextField date = form.text("Ngày hẹn (yyyy-MM-dd)", editing && appointment.appointmentDate() != null ? appointment.appointmentDate().toString() : LocalDate.now().toString());
        TextField startTime = form.text("Giờ bắt đầu (HH:mm)", editing && appointment.startTime() != null ? TIME_FORMAT.format(appointment.startTime()) : "09:00");
        TextField serviceIds = form.text("ID dịch vụ, cách nhau dấu phẩy", editing ? serviceIds(appointment.services()) : "");
        TextArea note = form.area("Ghi chú", editing ? appointment.note() : "");
        form.show().ifPresent(ok -> {
            CustomerDto selectedCustomer = customer.getValue();
            if (selectedCustomer == null || isBlank(date.getText()) || isBlank(startTime.getText()) || isBlank(serviceIds.getText())) {
                showInlineError("Thiếu thông tin", "Vui lòng chọn khách hàng, ngày giờ và ít nhất một dịch vụ.");
                return;
            }
            try {
                AppointmentRequestDto request = new AppointmentRequestDto(
                        selectedCustomer.id(),
                        employee.getValue() == null ? null : employee.getValue().id(),
                        LocalDate.parse(date.getText().trim()),
                        LocalTime.parse(startTime.getText().trim()),
                        parseIds(serviceIds.getText()),
                        blankToNull(note.getText()));
                CompletableFuture<AppointmentDto> call = editing
                        ? adminClient.updateAppointment(appointment.id(), request)
                        : adminClient.createAppointment(request);
                runMutation(call, refresh, "Không thể lưu lịch hẹn");
            } catch (RuntimeException exception) {
                showInlineError("Dữ liệu không hợp lệ", "Ngày giờ hoặc danh sách dịch vụ không đúng định dạng.");
            }
        });
    }

    private void showAppointmentStatusDialog(AppointmentDto appointment, Runnable refresh) {
        if (appointment == null) {
            showInlineError("Chưa chọn dữ liệu", "Vui lòng chọn một lịch hẹn trong bảng.");
            return;
        }
        Form form = new Form("Đổi trạng thái lịch hẹn");
        ComboBox<String> status = form.combo("Trạng thái", FXCollections.observableArrayList(APPOINTMENT_STATUSES), value -> value);
        status.getSelectionModel().select(appointment.status());
        form.show().ifPresent(ok -> runMutation(
                adminClient.updateAppointmentStatus(appointment.id(), new AppointmentStatusRequestDto(status.getValue())),
                refresh,
                "Không thể đổi trạng thái lịch hẹn"));
    }

    private void showInvoiceDialog(Runnable refresh) {
        ensureReferenceDataLoaded();
        Form form = new Form("Tạo hóa đơn");
        ComboBox<AppointmentDto> appointment = form.combo("Lịch hẹn hoàn thành", appointments,
                item -> "#" + item.id() + " - " + item.customerName() + " - " + date(item.appointmentDate()));
        appointment.setItems(FXCollections.observableArrayList(appointments.stream()
                .filter(item -> "Hoàn thành".equals(item.status()))
                .toList()));
        TextField discount = form.text("Giảm giá", "0");
        ComboBox<String> method = form.combo("Phương thức", FXCollections.observableArrayList(PAYMENT_METHODS), value -> value);
        method.getItems().add(0, "");
        method.getSelectionModel().selectFirst();
        form.show().ifPresent(ok -> {
            if (appointment.getValue() == null) {
                showInlineError("Thiếu lịch hẹn", "Chỉ có thể tạo hóa đơn cho lịch hẹn đã hoàn thành.");
                return;
            }
            try {
                InvoiceRequestDto request = new InvoiceRequestDto(
                        appointment.getValue().id(),
                        isBlank(discount.getText()) ? BigDecimal.ZERO : new BigDecimal(discount.getText().trim()),
                        blankToNull(method.getValue()));
                runMutation(adminClient.createInvoice(request), refresh, "Không thể tạo hóa đơn");
            } catch (NumberFormatException exception) {
                showInlineError("Dữ liệu không hợp lệ", "Giảm giá phải là số.");
            }
        });
    }

    private void showPayInvoiceDialog(InvoiceDto invoice, Runnable refresh) {
        if (invoice == null) {
            showInlineError("Chưa chọn dữ liệu", "Vui lòng chọn một hóa đơn trong bảng.");
            return;
        }
        Form form = new Form("Thanh toán hóa đơn");
        ComboBox<String> method = form.combo("Phương thức", FXCollections.observableArrayList(PAYMENT_METHODS), value -> value);
        method.getSelectionModel().select(isBlank(invoice.paymentMethod()) ? "Tiền mặt" : invoice.paymentMethod());
        form.show().ifPresent(ok -> runMutation(
                adminClient.payInvoice(invoice.id(), new InvoicePaymentRequestDto(method.getValue())),
                refresh,
                "Không thể thanh toán hóa đơn"));
    }

    private void showInvoiceStatusDialog(InvoiceDto invoice, Runnable refresh) {
        if (invoice == null) {
            showInlineError("Chưa chọn dữ liệu", "Vui lòng chọn một hóa đơn trong bảng.");
            return;
        }
        Form form = new Form("Đổi trạng thái thanh toán");
        ComboBox<String> status = form.combo("Trạng thái", FXCollections.observableArrayList(PAYMENT_STATUSES), value -> value);
        status.getSelectionModel().select(invoice.paymentStatus());
        form.show().ifPresent(ok -> runMutation(
                adminClient.updateInvoiceStatus(invoice.id(), new InvoiceStatusRequestDto(status.getValue())),
                refresh,
                "Không thể đổi trạng thái hóa đơn"));
    }

    private void showScheduleDialog(EmployeeScheduleDto schedule, Runnable refresh) {
        boolean editing = schedule != null;
        ensureReferenceDataLoaded();
        Form form = new Form(editing ? "Sửa ca làm" : "Thêm ca làm");
        ComboBox<EmployeeDto> employee = form.combo("Nhân viên", employees, item -> item.fullName() + " - " + valueOrEmpty(item.position()));
        employee.getSelectionModel().select(findEmployee(editing ? schedule.employeeId() : null));
        TextField workDate = form.text("Ngày làm (yyyy-MM-dd)", editing && schedule.workDate() != null ? schedule.workDate().toString() : LocalDate.now().toString());
        TextField startTime = form.text("Giờ bắt đầu (HH:mm)", editing && schedule.startTime() != null ? time(schedule.startTime()) : "09:00");
        TextField endTime = form.text("Giờ kết thúc (HH:mm)", editing && schedule.endTime() != null ? time(schedule.endTime()) : "18:00");
        TextArea note = form.area("Ghi chú", editing ? schedule.note() : "");
        form.show().ifPresent(ok -> {
            if (employee.getValue() == null || isBlank(workDate.getText()) || isBlank(startTime.getText()) || isBlank(endTime.getText())) {
                showInlineError("Thiếu thông tin", "Vui lòng chọn nhân viên, ngày làm và giờ làm.");
                return;
            }
            try {
                EmployeeScheduleRequestDto request = new EmployeeScheduleRequestDto(
                        employee.getValue().id(),
                        LocalDate.parse(workDate.getText().trim()),
                        LocalTime.parse(startTime.getText().trim()),
                        LocalTime.parse(endTime.getText().trim()),
                        blankToNull(note.getText()));
                CompletableFuture<EmployeeScheduleDto> call = editing
                        ? adminClient.updateEmployeeSchedule(schedule.id(), request)
                        : adminClient.createEmployeeSchedule(request);
                runMutation(call, refresh, "Không thể lưu ca làm");
            } catch (RuntimeException exception) {
                showInlineError("Dữ liệu không hợp lệ", "Ngày hoặc giờ làm không đúng định dạng.");
            }
        });
    }

    private void showSmsDialog(Runnable refresh) {
        ensureReferenceDataLoaded();
        Form form = new Form("Tạo SMS nhắc lịch");
        ComboBox<AppointmentDto> appointment = form.combo("Lịch hẹn đã xác nhận", appointments,
                item -> "#" + item.id() + " - " + item.customerName() + " - " + date(item.appointmentDate()));
        appointment.setItems(FXCollections.observableArrayList(appointments.stream()
                .filter(item -> "Đã xác nhận".equals(item.status()))
                .toList()));
        TextField phone = form.text("Số điện thoại", "");
        TextArea message = form.area("Nội dung", "");
        appointment.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                CustomerDto selectedCustomer = findCustomer(newValue.customerId());
                phone.setText(selectedCustomer == null ? "" : valueOrEmpty(selectedCustomer.phone()));
                if (isBlank(message.getText())) {
                    message.setText("Spa Booking nhắc lịch hẹn của " + newValue.customerName() + " vào ngày "
                            + date(newValue.appointmentDate()) + " lúc " + time(newValue.startTime()) + ".");
                }
            }
        });
        form.show().ifPresent(ok -> {
            if (appointment.getValue() == null || isBlank(phone.getText()) || isBlank(message.getText())) {
                showInlineError("Thiếu thông tin", "Vui lòng chọn lịch hẹn đã xác nhận, số điện thoại và nội dung.");
                return;
            }
            SmsReminderRequestDto request = new SmsReminderRequestDto(appointment.getValue().id(), phone.getText().trim(), message.getText().trim());
            runMutation(adminClient.createSmsReminder(request), refresh, "Không thể tạo SMS");
        });
    }

    private void showSmsStatusDialog(SmsReminderDto sms, Runnable refresh) {
        if (sms == null) {
            showInlineError("Chưa chọn dữ liệu", "Vui lòng chọn một SMS trong bảng.");
            return;
        }
        Form form = new Form("Đổi trạng thái SMS");
        ComboBox<String> status = form.combo("Trạng thái", FXCollections.observableArrayList(SMS_STATUSES), value -> value);
        status.getSelectionModel().select(sms.status());
        TextArea errorMessage = form.area("Lỗi gửi SMS", sms.errorMessage());
        form.show().ifPresent(ok -> runMutation(
                adminClient.updateSmsReminderStatus(sms.id(), new SmsReminderStatusRequestDto(status.getValue(), blankToNull(errorMessage.getText()))),
                refresh,
                "Không thể đổi trạng thái SMS"));
    }

    private void loadRoles() {
        adminClient.findRoles().thenAccept(response -> Platform.runLater(() -> roles.setAll(response == null ? List.of() : response)));
        adminClient.findUsers().thenAccept(response -> Platform.runLater(() -> users.setAll(response == null ? List.of() : response)));
    }

    private void ensureReferenceDataLoaded() {
        if (users.isEmpty()) {
            adminClient.findUsers().thenAccept(response -> Platform.runLater(() -> users.setAll(response == null ? List.of() : response)));
        }
        if (customers.isEmpty()) {
            adminClient.findCustomers().thenAccept(response -> Platform.runLater(() -> customers.setAll(response == null ? List.of() : response)));
        }
        if (employees.isEmpty()) {
            adminClient.findEmployees().thenAccept(response -> Platform.runLater(() -> employees.setAll(response == null ? List.of() : response)));
        }
        if (services.isEmpty()) {
            adminClient.findServices().thenAccept(response -> Platform.runLater(() -> services.setAll(response == null ? List.of() : response)));
        }
        if (appointments.isEmpty()) {
            adminClient.findAppointments().thenAccept(response -> Platform.runLater(() -> appointments.setAll(response == null ? List.of() : response)));
        }
        if (invoices.isEmpty()) {
            adminClient.findInvoices().thenAccept(response -> Platform.runLater(() -> invoices.setAll(response == null ? List.of() : response)));
        }
        if (schedules.isEmpty()) {
            adminClient.findEmployeeSchedules().thenAccept(response -> Platform.runLater(() -> schedules.setAll(response == null ? List.of() : response)));
        }
        if (smsReminders.isEmpty()) {
            adminClient.findSmsReminders().thenAccept(response -> Platform.runLater(() -> smsReminders.setAll(response == null ? List.of() : response)));
        }
    }

    private void refreshAllData(Runnable afterLoad) {
        CompletableFuture<List<CustomerDto>> customerCall = adminClient.findCustomers();
        CompletableFuture<List<EmployeeDto>> employeeCall = adminClient.findEmployees();
        CompletableFuture<List<ServiceDto>> serviceCall = adminClient.findServices();
        CompletableFuture<List<AppointmentDto>> appointmentCall = adminClient.findAppointments();
        CompletableFuture<List<InvoiceDto>> invoiceCall = adminClient.findInvoices();
        CompletableFuture<List<EmployeeScheduleDto>> scheduleCall = adminClient.findEmployeeSchedules();
        CompletableFuture<List<SmsReminderDto>> smsCall = adminClient.findSmsReminders();
        CompletableFuture.allOf(customerCall, employeeCall, serviceCall, appointmentCall, invoiceCall, scheduleCall, smsCall)
                .thenRun(() -> Platform.runLater(() -> {
                    customers.setAll(customerCall.join() == null ? List.of() : customerCall.join());
                    employees.setAll(employeeCall.join() == null ? List.of() : employeeCall.join());
                    services.setAll(serviceCall.join() == null ? List.of() : serviceCall.join());
                    appointments.setAll(appointmentCall.join() == null ? List.of() : appointmentCall.join());
                    invoices.setAll(invoiceCall.join() == null ? List.of() : invoiceCall.join());
                    schedules.setAll(scheduleCall.join() == null ? List.of() : scheduleCall.join());
                    smsReminders.setAll(smsCall.join() == null ? List.of() : smsCall.join());
                    afterLoad.run();
                }))
                .exceptionally(error -> {
                    Platform.runLater(() -> showInlineError("Không thể tải báo cáo", resolveMessage(error)));
                    return null;
                });
    }

    private <T> void runLoad(CompletableFuture<List<T>> call, Consumer<List<T>> setter, String errorTitle) {
        call.thenAccept(response -> Platform.runLater(() -> setter.accept(response == null ? List.of() : response)))
                .exceptionally(error -> {
                    Platform.runLater(() -> showInlineError(errorTitle, resolveMessage(error)));
                    return null;
                });
    }

    private <T> void runMutation(CompletableFuture<T> call, Runnable refresh, String errorTitle) {
        call.thenRun(() -> Platform.runLater(refresh))
                .exceptionally(error -> {
                    Platform.runLater(() -> showInlineError(errorTitle, resolveMessage(error)));
                    return null;
                });
    }

    private <T> void deleteSelected(T selected, String label, Function<T, CompletableFuture<Void>> deleteCall, Runnable refresh) {
        if (selected == null) {
            showInlineError("Chưa chọn dữ liệu", "Vui lòng chọn một " + label + " trong bảng.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xóa " + label);
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn xóa " + label + " này không?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            runMutation(deleteCall.apply(selected), refresh, "Không thể xóa " + label);
        }
    }

    private Button button(String text, String styleClass, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        button.setOnAction(event -> action.run());
        return button;
    }

    private <T> TableView<T> table(String placeholder) {
        TableView<T> table = new TableView<>();
        table.getStyleClass().add("admin-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label(placeholder));
        return table;
    }

    private <T> TableColumn<T, String> column(String title, double width, Function<T, String> valueFactory) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(valueOrEmpty(valueFactory.apply(data.getValue()))));
        return column;
    }

    private boolean matches(String searchable, String keyword) {
        return keyword == null || keyword.isBlank() || searchable.toLowerCase().contains(keyword.trim().toLowerCase());
    }

    private String searchable(String... values) {
        return String.join(" ", java.util.Arrays.stream(values).map(this::valueOrEmpty).toList());
    }

    private RoleDto findRole(String name) {
        return roles.stream().filter(role -> role.name().equalsIgnoreCase(valueOrEmpty(name))).findFirst()
                .orElse(roles.isEmpty() ? null : roles.get(0));
    }

    private UserDto findUser(Long id) {
        return id == null ? null : users.stream().filter(user -> user.id().equals(id)).findFirst().orElse(null);
    }

    private CustomerDto findCustomer(Long id) {
        return id == null ? null : customers.stream().filter(item -> item.id().equals(id)).findFirst().orElse(null);
    }

    private EmployeeDto findEmployee(Long id) {
        return id == null ? null : employees.stream().filter(item -> item.id().equals(id)).findFirst().orElse(null);
    }

    private List<Long> parseIds(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .map(Long::parseLong)
                .toList();
    }

    private String serviceIds(List<AppointmentServiceDto> appointmentServices) {
        if (appointmentServices == null) {
            return "";
        }
        return appointmentServices.stream()
                .map(AppointmentServiceDto::serviceId)
                .map(String::valueOf)
                .reduce((first, second) -> first + ", " + second)
                .orElse("");
    }

    private String serviceNames(List<AppointmentServiceDto> appointmentServices) {
        if (appointmentServices == null) {
            return "";
        }
        return appointmentServices.stream()
                .map(AppointmentServiceDto::serviceName)
                .reduce((first, second) -> first + ", " + second)
                .orElse("");
    }

    private String resolveMessage(Throwable error) {
        Throwable current = error;
        while (current instanceof CompletionException && current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() == null || current.getMessage().isBlank() ? "Có lỗi xảy ra khi gọi API." : current.getMessage();
    }

    private void showInlineError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
        return new VBox(4, titleLabel, subtitleLabel);
    }

    private void showPlaceholder(String title, String subtitle) {
        headerTitle.setText(title);
        headerSubtitle.setText(subtitle);
        Label emptyIcon = new Label("✦");
        emptyIcon.getStyleClass().add("empty-icon");
        Label emptyTitle = new Label(title);
        emptyTitle.getStyleClass().add("empty-title");
        Label emptyText = new Label("Màn hình nghiệp vụ này sẽ được nối sau khi hoàn thiện dữ liệu nền.");
        emptyText.getStyleClass().add("empty-text");
        emptyText.setWrapText(true);
        VBox emptyState = new VBox(12, emptyIcon, emptyTitle, emptyText);
        emptyState.getStyleClass().add("empty-state");
        contentArea.getChildren().setAll(emptyState);
    }

    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Đăng xuất");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn đăng xuất không?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }
        SessionManager.clear();
        try {
            mainApp.showLogin();
        } catch (IOException e) {
            showInlineError("Lỗi giao diện", "Không thể quay lại màn hình đăng nhập.");
        }
    }

    private String joinLines(String first, String second) {
        if (isBlank(first)) {
            return valueOrEmpty(second);
        }
        if (isBlank(second)) {
            return valueOrEmpty(first);
        }
        return first + "\n" + second;
    }

    private String dateTime(java.time.LocalDateTime value) {
        return value == null ? "" : DATE_TIME_FORMAT.format(value);
    }

    private String date(LocalDate value) {
        return value == null ? "" : DATE_FORMAT.format(value);
    }

    private String time(LocalTime value) {
        return value == null ? "" : TIME_FORMAT.format(value);
    }

    private String timeRange(LocalTime start, LocalTime end) {
        if (start == null && end == null) {
            return "";
        }
        return time(start) + " - " + time(end);
    }

    private LocalDate parseDate(String value) {
        return isBlank(value) ? null : LocalDate.parse(value.trim());
    }

    private String activeText(Boolean active) {
        return Boolean.TRUE.equals(active) ? "Đang hoạt động" : "Tạm khóa";
    }

    private String money(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString() + " đ";
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    public record Metric(String label, String value, String hint) {
    }

    private static final class Form {
        private final Dialog<ButtonType> dialog = new Dialog<>();
        private final GridPane grid = new GridPane();
        private int row;

        Form(String title) {
            dialog.setTitle(title);
            dialog.setHeaderText(null);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            grid.setHgap(12);
            grid.setVgap(12);
            grid.setPadding(new Insets(18));
            dialog.getDialogPane().setContent(grid);
        }

        TextField text(String label, String value) {
            TextField field = new TextField(value == null ? "" : value);
            add(label, field);
            return field;
        }

        PasswordField password(String label, String value) {
            PasswordField field = new PasswordField();
            field.setText(value == null ? "" : value);
            add(label, field);
            return field;
        }

        TextArea area(String label, String value) {
            TextArea area = new TextArea(value == null ? "" : value);
            area.setPrefRowCount(3);
            add(label, area);
            return area;
        }

        CheckBox check(String label, boolean selected) {
            CheckBox checkBox = new CheckBox(label);
            checkBox.setSelected(selected);
            grid.add(checkBox, 1, row++);
            return checkBox;
        }

        <T> ComboBox<T> combo(String label, ObservableList<T> items, Function<T, String> labelFactory) {
            ComboBox<T> comboBox = new ComboBox<>(items);
            comboBox.setMaxWidth(Double.MAX_VALUE);
            comboBox.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(T object) {
                    return object == null ? "" : labelFactory.apply(object);
                }

                @Override
                public T fromString(String string) {
                    return null;
                }
            });
            add(label, comboBox);
            return comboBox;
        }

        Optional<ButtonType> show() {
            return dialog.showAndWait().filter(button -> button == ButtonType.OK);
        }

        private void add(String label, Node field) {
            Label labelNode = new Label(label);
            labelNode.getStyleClass().add("field-label");
            grid.add(labelNode, 0, row);
            grid.add(field, 1, row++);
            GridPane.setHgrow(field, Priority.ALWAYS);
        }
    }
}
