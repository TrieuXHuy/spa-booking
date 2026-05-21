package com.example.spabooking.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

public final class DialogHelper {
    private static final String THEME_CSS = "/css/spa-theme.css";

    private DialogHelper() {
    }

    public static void applyBaseDialogStyles(Dialog<?> dialog) {
        DialogPane pane = dialog.getDialogPane();
        String cssUrl = resolveCssUrl();
        if (cssUrl != null && !pane.getStylesheets().contains(cssUrl)) {
            pane.getStylesheets().add(cssUrl);
        }
        if (!pane.getStyleClass().contains("base-dialog")) {
            pane.getStyleClass().add("base-dialog");
        }
    }

    public static Alert createAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyBaseDialogStyles(alert);
        return alert;
    }

    private static String resolveCssUrl() {
        if (DialogHelper.class.getResource(THEME_CSS) == null) {
            return null;
        }
        return DialogHelper.class.getResource(THEME_CSS).toExternalForm();
    }
}
