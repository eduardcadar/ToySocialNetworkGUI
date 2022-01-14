package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.CONSTANTS;
import com.toysocialnetworkgui.utils.MyAlert;
import com.toysocialnetworkgui.utils.PasswordEncryptor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginSceneController {
    private Stage window;

    private Service service;
    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldPassword;

    public void initialize(Service service, Stage primaryStage) {
        this.service = service;
        this.window = primaryStage;
    }

    @FXML
    protected void onLoginButtonClick() throws IOException {
        User loggedUser;
        if (textFieldEmail.getText().equals(""))
             loggedUser = this.service.getUser("stef@gmail.com");

        // TODO
        //  ONLY FOR TESTING PURPOSES
        else {
            loggedUser = this.service.getUser(textFieldEmail.getText());
            if (loggedUser == null || !loggedUser.getPassword().equals(PasswordEncryptor.toHexString(PasswordEncryptor.getSHA(textFieldPassword.getText())))) {
                MyAlert.StartAlert("Error", "Wrong email or password", Alert.AlertType.ERROR);
                return;
            }
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("loggedScene.fxml"));
        Parent root = loader.load();
        LoggedSceneController controller = loader.getController();
        controller.initialize(service, loggedUser, window);
        Scene scene = new Scene(root);
        window.setScene(scene);
    }

    @FXML
    public void onSignUpClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("createAccount.fxml"));
        Parent root = loader.load();
        CreateAccountController controller = loader.getController();
        controller.initialize(service, window);

        window.getScene().setRoot(root);
    }
}