package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.DbException;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.validator.ValidatorException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class AdminSceneController {

    private Service service;

    @FXML
    private Button addUserButton;

    @FXML
    private Button removeUserButton;

    @FXML
    private TableView<User> tableViewUsers;

    @FXML
    TableColumn<User, String> tableColumnFirstname;

    @FXML
    TableColumn<User, String> tableColumnLastname;

    @FXML
    TableColumn<User, String> tableColumnEmail;


    @FXML
    private TextField textFieldFirstname;
    @FXML
    private TextField textFieldLastname;
    @FXML
    private TextField textFieldEmail;

    @FXML
    private PasswordField textFieldPassword;


    public void initialize(Service service) {
        this.service = service;
        initializeUsersList();
        tableViewUsers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    private void initializeUsersList() {
        tableColumnFirstname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        setUsersList(getUsers());

    }

    private ObservableList<User> getUsers() {
        return FXCollections.observableArrayList(service.getUsers());
    }

    private void setUsersList(ObservableList<User> users) {
        tableViewUsers.setItems(users);
    }


    @FXML
    protected void onAddUserButtonClick() {
       try {
           service.addUser(textFieldFirstname.getText(), textFieldLastname.getText(), textFieldEmail.getText(), textFieldPassword.getText());
           setUsersList(getUsers());
       }
       catch (RepoException | ValidatorException | DbException e){
           Alert alert = new Alert(Alert.AlertType.WARNING);
           alert.setTitle("Error");
           alert.setHeaderText(null);
           alert.setContentText(e.getMessage());
           alert.showAndWait();
       }
    }

    @FXML
    protected void onRemoveUserButtonClick() {
        service.removeUser(textFieldEmail.getText());
        setUsersList(getUsers());
    }
}
