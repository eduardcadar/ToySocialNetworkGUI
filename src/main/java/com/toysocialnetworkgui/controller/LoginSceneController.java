package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.PasswordEncryptor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginSceneController {
    private Service service;
    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldPassword;

    @FXML
    private Button buttonAdminInterface;

    public void setService(Service service) {
        this.service = service;
    }

    @FXML
    protected void onLoginButtonClick(ActionEvent event) throws IOException {
        User loggedUser = this.service.getUser(textFieldEmail.getText());
        if (loggedUser == null || !loggedUser.getPassword().equals(PasswordEncryptor.toHexString(PasswordEncryptor.getSHA(textFieldPassword.getText())))) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Wrong email or password");
            alert.showAndWait();
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

    public void onAdminButtonClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("adminScene.fxml"));
        Parent root = loader.load();
        AdminSceneController controller = loader.getController();
        controller.initialize(service);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());
        stage.setTitle("Admin interface");
        stage.setScene( new Scene(root));
        stage.showAndWait();
    }
}