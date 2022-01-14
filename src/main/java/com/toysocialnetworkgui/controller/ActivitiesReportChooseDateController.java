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
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

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
    private AnchorPane rightPane;

    public void initialize(Service service, User loggedUser, AnchorPane rightPane) {
        this.service = service;
        this.loggedUser = loggedUser;
        this.rightPane = rightPane;
        Callback<DatePicker, DateCell> dontLetUserPickEarlyEnd = dontLetUserPickEarlyDateThanStart();
        datePickerUntil.setDayCellFactory(dontLetUserPickEarlyEnd);

    }

    Callback<DatePicker, DateCell> dontLetUserPickEarlyDateThanStart() {
        return new Callback<>() {
            @Override
            public DateCell call (final DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                        LocalDate startDate = datePickerFrom.getValue();
                        if (startDate != null)
                            setDisable(empty || item.compareTo(startDate) < 0);
                        else
                            setDisable(true);
                    }

                };
            }
        };
    }

    @FXML
    protected void onButtonGenerateClick() throws IOException {
        LocalDate dateFrom = datePickerFrom.getValue();
        LocalDate dateUntil = datePickerUntil.getValue();
        if (dateFrom == null || dateUntil == null) {
            MyAlert.StartAlert("Error", "Choose from and until date!", Alert.AlertType.ERROR);
            return;
        }
        if (dateFrom.isAfter(dateUntil)) {
            MyAlert.StartAlert("Error", "From date should be after until date", Alert.AlertType.ERROR);
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("activitiesReport.fxml"));
        Parent root = loader.load();
        ActivitiesReportController controller = loader.getController();
        controller.initialize(service, loggedUser, dateFrom, dateUntil, rightPane);
        rightPane.getChildren().setAll(root);
    }
}
