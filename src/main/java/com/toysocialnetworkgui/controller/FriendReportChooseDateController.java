package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class FriendReportChooseDateController {
    @FXML
    DatePicker datePickerFrom;
    @FXML
    DatePicker datePickerUntil;
    @FXML
    Button buttonGenerate;

    private Service service;
    private User loggedUser;
    private User otherUser;

    public void initialize(Service service, User loggedUser, User otherUser) {
        this.service = service;
        this.loggedUser = loggedUser;
        this.otherUser = otherUser;
    }

    @FXML
    protected void onButtonGenerateClick(ActionEvent event) throws IOException {
        LocalDate dateFrom = datePickerFrom.getValue();
        LocalDate dateUntil = datePickerUntil.getValue();
        if (dateFrom == null || dateUntil == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Choose from and until date!");
            alert.showAndWait();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("friendReport.fxml"));
        Parent root = loader.load();
        FriendReportController controller = loader.getController();
        controller.initialize(service, loggedUser, otherUser, dateFrom, dateUntil);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
