package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.CONSTANTS;
import com.toysocialnetworkgui.utils.PasswordEncryptor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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

    @FXML
    private Button buttonCreateAccount;

    public void setService(Service service) {
        this.service = service;
    }

    @FXML
    protected void onLoginButtonClick(ActionEvent event) throws IOException {
        User loggedUser;
        if(textFieldEmail.getText().equals(""))
             loggedUser = this.service.getUser("stef@gmail.com");

        // TODO
        //  ONLY FOR TESTING PURPOSES
        else {
             loggedUser = this.service.getUser(textFieldEmail.getText());
            if (loggedUser == null || !loggedUser.getPassword().equals(PasswordEncryptor.toHexString(PasswordEncryptor.getSHA(textFieldPassword.getText())))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Wrong email or password");
                alert.showAndWait();
                return;
            }
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loggedScene.fxml"));
        Parent root = loader.load();
        LoggedSceneController controller = loader.getController();
        controller.setService(service);
        controller.setStage(window);
        controller.initialize(loggedUser);
        // Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, CONSTANTS.MAIN_SCREEN_WIDTH, CONSTANTS.MAIN_SCREEN_HEIGHT);
        window.setScene(scene);

        window.show();
    }

    public void onAdminButtonClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("adminScene.fxml"));
        Parent root = loader.load();
        AdminSceneController controller = loader.getController();
        controller.initialize(service, window);

        window.setScene(new Scene(root, CONSTANTS.ADMIN_SCREEN_WIDTH, CONSTANTS.ADMIN_SCREEN_HEIGHT));
        window.show();
      /*  Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());
        stage.setTitle("Admin interface");
        stage.setScene( new Scene(root));
        stage.showAndWait();
        */
    }


    public void setStage(Stage primaryStage) {
        this.window = primaryStage;
    }
}