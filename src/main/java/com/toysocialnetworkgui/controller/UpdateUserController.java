package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.db.DbException;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.CONSTANTS;
import com.toysocialnetworkgui.utils.MyAlert;
import com.toysocialnetworkgui.utils.PasswordEncryptor;
import com.toysocialnetworkgui.validator.ValidatorException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
import javafx.stage.Window;
import org.controlsfx.validation.ValidateEvent;

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
    private Text questionMark;
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
    protected void onUpdateButtonClick(ActionEvent event) throws IOException {
        if(!PasswordEncryptor.toHexString(PasswordEncryptor.getSHA(textFieldOldPassword.getText())).equals(loggedUser.getPassword())){
            MyAlert.StartAlert("Error","Passwords don't match!", Alert.AlertType.ERROR );
        }
        else{
            try {
                service.updateUser(textFieldFirstname.getText(), textFieldLastname.getText(), loggedUser.getEmail(), textFieldPassword.getText(), lastProfilePicturePath);
                loggedUser = service.getUser(loggedUser.getEmail());

            } catch (DbException | ValidatorException e) {
                System.out.println(e.getMessage());
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
        lastProfilePicturePath = user.getProfilePicturePath();
    }

    public void onProfilePictureClick(MouseEvent mouseEvent) throws IOException {
        questionMark.setVisible(true);
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image", "*.jpg", "*.png", "*.jpeg")
        );

        fileChooser.setInitialDirectory(new File(".\\src\\main\\resources\\profile"));
        fileChooser.setTitle("Pick an image from this folder, please!");
        File selectedFile = fileChooser.showOpenDialog(window);
        if(selectedFile != null) {
            questionMark.setVisible(false);
            String fullPath = selectedFile.getAbsolutePath();
            String[] args = fullPath.split("resources");
            if(args.length != 2 || !fullPath.contains("profile")) {
                MyAlert.StartAlert("Error", "Pick an image from this profile folder, please!", Alert.AlertType.ERROR);
                return;
            }
            String pathToFile = args[1];
            pathToFile = pathToFile.replace("\\", "/");
            imagePlaceHolder.setStroke(Color.web("#862CE4"));
            Image im = new Image(pathToFile);
            imagePlaceHolder.setFill(new ImagePattern(im));
            lastProfilePicturePath =  pathToFile;

        }
    }

}
