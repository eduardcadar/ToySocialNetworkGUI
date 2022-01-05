package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.MyAlert;
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

public class ActivitiesReportChooseDateController {
    @FXML
    DatePicker datePickerFrom;
    @FXML
    DatePicker datePickerUntil;
    @FXML
    Button buttonGenerate;

    private Service service;
    private User loggedUser;

    public void initialize(Service service, User loggedUser) {
        this.service = service;
        this.loggedUser = loggedUser;
    }

    @FXML
    protected void onButtonGenerateClick(ActionEvent event) throws IOException {
        LocalDate dateFrom = datePickerFrom.getValue();
        LocalDate dateUntil = datePickerUntil.getValue();
        if (dateFrom == null || dateUntil == null) {
            MyAlert.StartAlert("Error", "Choose from and until date!", Alert.AlertType.WARNING);
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("activitiesReport.fxml"));
        Parent root = loader.load();
        ActivitiesReportController controller = loader.getController();
        controller.initialize(service, loggedUser, dateFrom, dateUntil);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
