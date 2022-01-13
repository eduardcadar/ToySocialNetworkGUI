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
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;

public class FriendReportChooseDateController {
    @FXML
    DatePicker datePickerFrom;
    @FXML
    DatePicker datePickerUntil;
    @FXML
    Button buttonGenerate;
    @FXML
    private ListView<User> listViewFriends;

    private Service service;
    private User loggedUser;
    private AnchorPane rightPane;

    public void initialize(Service service, User loggedUser, AnchorPane rightPane) {
        this.service = service;
        this.loggedUser = loggedUser;
        this.rightPane = rightPane;
        Callback<DatePicker, DateCell> dontLetUserPickEarlyEnd = dontLetUserPickEarlyDateThanStart();
        datePickerUntil.setDayCellFactory(dontLetUserPickEarlyEnd);

        initializeFriendsList();
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
    private void initializeFriendsList() {
        listViewFriends.getItems().setAll(service.getUserFriends(loggedUser.getEmail()));
        listViewFriends.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML
    protected void onButtonGenerateClick() throws IOException {
        if (listViewFriends.getSelectionModel().isEmpty()) {
            MyAlert.StartAlert("Error", "Select one friend!", Alert.AlertType.WARNING);
            return;
        }
        User otherUser = listViewFriends.getSelectionModel().getSelectedItem();
        
        LocalDate dateFrom = datePickerFrom.getValue();
        LocalDate dateUntil = datePickerUntil.getValue();
        if (dateFrom == null || dateUntil == null) {
            MyAlert.StartAlert("Error", "Choose from and until date!", Alert.AlertType.WARNING);
            return;
        }
        if (dateFrom.isAfter(dateUntil)) {
            MyAlert.StartAlert("Error", "From date should be after until date", Alert.AlertType.WARNING);
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("friendReport.fxml"));
        Parent root = loader.load();
        FriendReportController controller = loader.getController();
        controller.initialize(service, loggedUser, otherUser, dateFrom, dateUntil, rightPane);
        rightPane.getChildren().setAll(root);
    }
}
