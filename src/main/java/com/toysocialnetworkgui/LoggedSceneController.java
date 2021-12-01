package com.toysocialnetworkgui;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.UserFriendDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class LoggedSceneController {
    @FXML
    ComboBox<String> comboBoxMonth = new ComboBox<String>();
    @FXML
    Button buttonUpdateUser = new Button();
    @FXML
    private Label labelLoggedUser = new Label();
    @FXML
    TableView<UserFriendDTO> tableViewFriends;
    @FXML
    TableColumn<UserFriendDTO, String> tableColumnFirstname;
    @FXML
    TableColumn<UserFriendDTO, String> tableColumnLastname;
    @FXML
    TableColumn<UserFriendDTO, Date> tableColumnDate;

    private User loggedUser;
    private Service service;

    public void setService(Service service) {
        this.service = service;
    }

    public void initialize(User user) {
        setLoggedUser(user);
        initializeFriendsList();
        comboBoxMonth.setItems(getMonths());
    }

    private void setLoggedUser(User user) {
        loggedUser = user;
        labelLoggedUser.setText("Logged user: " + user);
    }

    private ObservableList<String> getMonths() {
        return FXCollections.observableArrayList(Arrays.asList(
                "any month",
                "january", "february", "march", "april",
                "may", "june", "july", "august",
                "september", "october", "november", "december"));
    }

    @FXML
    protected void onSelectMonth(ActionEvent event) throws IOException {
        String month = comboBoxMonth.getValue().toString();
        int monthNr;
        switch (month) {
            case "january" -> monthNr = 1;
            case "february" -> monthNr = 2;
            case "march" -> monthNr = 3;
            case "april" -> monthNr = 4;
            case "may" -> monthNr = 5;
            case "june" -> monthNr = 6;
            case "july" -> monthNr = 7;
            case "august" -> monthNr = 8;
            case "september" -> monthNr = 9;
            case "october" -> monthNr = 10;
            case "november" -> monthNr = 11;
            case "december" -> monthNr = 12;
            default -> monthNr = 0;
        }
        if (monthNr == 0)
            setFriendsList(getFriends());
        else
            setFriendsList(getFriends().
                    filtered(x -> x.getDate().getMonthValue() == monthNr));
    }

    private ObservableList<UserFriendDTO> getFriends() {
        return FXCollections.observableArrayList(service.getFriendshipsDTO(loggedUser.getEmail()));
    }

    private void setFriendsList(ObservableList<UserFriendDTO> friends) {
        tableViewFriends.setItems(friends);
    }

    private void initializeFriendsList() {
        tableColumnFirstname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        setFriendsList(getFriends());
    }

    @FXML
    protected void onUpdateButtonClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("updateUser.fxml"));
        Parent root = loader.load();
        UpdateUserController controller = loader.getController();
        controller.setService(service);
        controller.initialize(loggedUser);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner((Stage)((Node) event.getSource()).getScene().getWindow());
        stage.setTitle("Update user information");
        stage.setScene(new Scene(root));
        stage.showAndWait();

        setLoggedUser(service.getUser(loggedUser.getEmail()));
    }

}
