package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.db.DbException;
import com.toysocialnetworkgui.service.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateUserController {
    private Service service;
    private User loggedUser;

    @FXML
    private TextField textFieldFirstname;
    @FXML
    private TextField textFieldLastname;
    @FXML
    private TextField textFieldPassword;

    @FXML
    protected void onUpdateButtonClick(ActionEvent event) {
        try {
            service.updateUser(textFieldFirstname.getText(), textFieldLastname.getText(), loggedUser.getEmail(), textFieldPassword.getText());
        } catch (DbException e) {
            System.out.println(e.getMessage());
        }

        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void initialize(User user) {
        loggedUser = user;
    }
}
