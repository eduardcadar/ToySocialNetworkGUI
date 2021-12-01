package com.toysocialnetworkgui;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.PasswordEncryptor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginSceneController {
    private Service service;

    @FXML
    private Label welcomeText;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldPassword;

    public void setService(Service service) {
        this.service = service;
    }

    @FXML
    protected void onLoginButtonClick(ActionEvent event) throws IOException {
        welcomeText.setText("Welcome to JavaFX Application!");

        User loggedUser = this.service.getUser(textFieldEmail.getText());
        if (loggedUser == null || !loggedUser.getPassword().equals(PasswordEncryptor.toHexString(PasswordEncryptor.getSHA(textFieldPassword.getText())))) {
            //login failed
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("loggedScene.fxml"));
        Parent root = loader.load();
        LoggedSceneController controller = loader.getController();
        controller.setService(service);
        controller.initialize(loggedUser);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }
}