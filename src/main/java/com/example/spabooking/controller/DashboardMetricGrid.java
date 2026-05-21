package com.example.spabooking.controller;

import com.example.spabooking.controller.AdminDashboardController.Metric;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DashboardMetricGrid extends GridPane {

    public DashboardMetricGrid(Metric... metrics) {
        getStyleClass().add("metric-grid");
        setHgap(16);
        setVgap(16);
        setPadding(Insets.EMPTY);
        getColumnConstraints().setAll(
                equalColumn(),
                equalColumn(),
                equalColumn(),
                equalColumn()
        );
        for (int i = 0; i < metrics.length; i++) {
            VBox card = createMetricCard(metrics[i]);
            add(card, i % 4, i / 4);
            GridPane.setHgrow(card, Priority.ALWAYS);
            card.setMaxWidth(Double.MAX_VALUE);
        }
    }

    public static DashboardMetricGrid workflow() {
        return new DashboardMetricGrid(
                new Metric("Bước 1", "Chọn dịch vụ", "Khách hàng chọn dịch vụ cần đặt"),
                new Metric("Bước 2", "Tạo lịch", "Kiểm tra nhân viên và thời gian trống"),
                new Metric("Bước 3", "Xác nhận", "Admin hoặc nhân viên xác nhận lịch"),
                new Metric("Bước 4", "Thanh toán", "Hoàn thành dịch vụ và tạo hóa đơn")
        );
    }

    private VBox createMetricCard(Metric metric) {
        Label label = new Label(metric.label());
        label.getStyleClass().add("metric-label");
        Label value = new Label(metric.value());
        value.getStyleClass().add("metric-value");
        Label hint = new Label(metric.hint());
        hint.getStyleClass().add("metric-hint");
        hint.setWrapText(true);

        VBox card = new VBox(8, label, value, hint);
        card.getStyleClass().add("metric-card");
        return card;
    }

    private ColumnConstraints equalColumn() {
        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(25);
        column.setHgrow(Priority.ALWAYS);
        column.setFillWidth(true);
        return column;
    }
}
