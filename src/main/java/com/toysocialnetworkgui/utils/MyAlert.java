package com.toysocialnetworkgui.utils;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class MyAlert {

    public static void StartAlert(String title, String text, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.getDialogPane().getStylesheets().add("style/styleAlerts.css");
        ((Stage)alert.getDialogPane().getScene().getWindow())
                .getIcons().add(new Image("/images/logo_small.png"));
        ImageView img;
        if (alertType == Alert.AlertType.WARNING)
            img = new ImageView(new Image("/images/alert-warning.png"));
        else if (alertType == Alert.AlertType.ERROR)
            img = new ImageView(new Image("/images/alert-error.png"));
        else
            img = new ImageView(new Image("/images/alert-information.png"));
        img.setFitWidth(32);
        img.setFitHeight(32);
        alert.setGraphic(img);
        alert.showAndWait();
    }
}
