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
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class UpdateUserController {
    private Service service;
    private User loggedUser;

    @FXML
    Circle imagePlaceHolder;

    private String lastProfilePicturePath;

    @FXML
    private StackPane profilePicturePicker;

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
        } else
            try {
                loggedUser = service.updateUser(textFieldFirstname.getText(), textFieldLastname.getText(), loggedUser.getEmail(), textFieldPassword.getText(), lastProfilePicturePath);
            } catch (DbException | ValidatorException e) {
                MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.WARNING);
            }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loggedScene.fxml"));
        Parent root = loader.load();
        LoggedSceneController controller = loader.getController();
        controller.initialize(service, loggedUser, window);
        window.getScene().setRoot(root);
    }

    public void initialize(User user, Stage window, Service service) {
        this.service = service;
        this.window = window;
        loggedUser = user;
        lastProfilePicturePath = user.getProfilePicturePath();
        imagePlaceHolder.setStroke(Color.web("#862CE4"));
        Image im = new Image(lastProfilePicturePath);
        imagePlaceHolder.setFill(new ImagePattern(im));
    }

    public void onProfilePictureClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image", "*.jpg", "*.png", "*.jpeg")
        );

        fileChooser.setInitialDirectory(new File(".\\src\\main\\resources\\profile"));
        fileChooser.setTitle("Pick an image from this folder, please!");
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile != null) {
            String fullPath = selectedFile.getAbsolutePath();
            String[] args = fullPath.split("resources");
            if (args.length != 2 || !fullPath.contains("profile")) {
                MyAlert.StartAlert("Error", "Pick an image from this profile folder, please!", Alert.AlertType.ERROR);
                return;
            }
            String pathToFile = args[1];
            pathToFile = pathToFile.replace("\\", "/");
            imagePlaceHolder.setStroke(Color.web("#862CE4"));
            Image im = new Image(pathToFile);
            imagePlaceHolder.setFill(new ImagePattern(im));
            lastProfilePicturePath = pathToFile;
        }
    }
}
