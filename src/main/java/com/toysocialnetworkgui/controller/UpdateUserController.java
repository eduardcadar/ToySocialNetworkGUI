package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.db.DbException;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.CONSTANTS;
import com.toysocialnetworkgui.utils.MyAlert;
import com.toysocialnetworkgui.utils.PasswordEncryptor;
import com.toysocialnetworkgui.validator.ValidatorException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class UpdateUserController {
    private Service service;
    private User loggedUser;

    private Stage window;
    @FXML
    private TextField textFieldFirstname;
    @FXML
    private TextField textFieldLastname;
    @FXML
    private TextField textFieldPassword;

    @FXML
    private TextField textFieldOldPassword;
    @FXML
    protected void onUpdateButtonClick() throws IOException {
        if (!PasswordEncryptor.toHexString(PasswordEncryptor.getSHA(textFieldOldPassword.getText())).equals(loggedUser.getPassword())) {
            MyAlert.StartAlert("Error", "Passwords do not match!", Alert.AlertType.WARNING);
        }
        else {
            try {
                loggedUser = service.updateUser(textFieldFirstname.getText(), textFieldLastname.getText(), loggedUser.getEmail(), textFieldPassword.getText());
            } catch (DbException | ValidatorException e) {
                MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.WARNING);
            }
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loggedScene.fxml"));
        Parent root = loader.load();
        LoggedSceneController controller = loader.getController();
        controller.initialize(service, loggedUser, window);
        Scene scene = new Scene(root, CONSTANTS.MAIN_SCREEN_WIDTH, CONSTANTS.MAIN_SCREEN_HEIGHT);
        window.setScene(scene);
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void initialize(User user, Stage window) {
        this.window = window;
        loggedUser = user;
    }
}
