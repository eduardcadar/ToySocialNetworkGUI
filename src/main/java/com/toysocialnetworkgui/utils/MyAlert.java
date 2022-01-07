package com.toysocialnetworkgui.utils;

import javafx.scene.control.Alert;

public class MyAlert {

    public static void StartAlert(String title, String text, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}
