package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.DbException;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.CONSTANTS;
import com.toysocialnetworkgui.validator.ValidatorException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.io.IOException;

public class AdminSceneController {

    private Stage window;
    private Service service;

    @FXML
    private Button buttonSubmit;

    @FXML
    private Button buttonCancel;



    @FXML
    public TextField textFieldFirstname;
    @FXML
    public TextField textFieldLastname;
    @FXML
    public TextField textFieldEmail;

    @FXML
    public PasswordField textFieldPassword;

    @FXML
    public PasswordField textFieldPasswordConfirmation;

    ValidationSupport validationSupport;


    public void setStage(Stage window){
        this.window = window;
    }

    public void initialize(Service service, Stage window) {
        this.service = service;
        this.window = window;
        initializeUsersList();
     //   tableViewUsers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // TODO
        //  Maybe remove this if it doesnt look nice
        //  Need to ADD this as VM argument --add-opens=javafx.graphics/javafx.scene=org.controlsfx.controls
        validationSupport = new ValidationSupport();
        validationSupport.registerValidator(textFieldFirstname, Validator.createEmptyValidator("Field is required"));
        validationSupport.registerValidator(textFieldLastname, Validator.createEmptyValidator("Field is required"));
        validationSupport.registerValidator(textFieldEmail, Validator.createEmptyValidator("Field is required"));
        validationSupport.registerValidator(textFieldPassword, Validator.createEmptyValidator("Field is required"));
        validationSupport.registerValidator(textFieldPasswordConfirmation, Validator.createEmptyValidator("Field is required"));

    }

    private void initializeUsersList() {

    }

    private ObservableList<User> getUsers() {
        return FXCollections.observableArrayList(service.getUsers());
    }

   // private void setUsersList(ObservableList<User> users) {
     //   tableViewUsers.setItems(users);
    //}


    @FXML
    protected void onSubmitButtonClick() {
        if(!textFieldPassword.getText().equals(textFieldPasswordConfirmation.getText())){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Passwords not match");
            alert.showAndWait();
        }
        try {
           service.addUser(textFieldFirstname.getText(), textFieldLastname.getText(), textFieldEmail.getText(), textFieldPassword.getText());
            showLoginScene();
       }
       catch (RepoException | ValidatorException | DbException | IOException e){
           Alert alert = new Alert(Alert.AlertType.WARNING);
           alert.setTitle("Error");
           alert.setHeaderText(null);
           alert.setContentText(e.getMessage());
           alert.showAndWait();
       }
    }

    @FXML
    protected void onCancelButtonClick() throws IOException {
        showLoginScene();
    }

    @FXML
    protected void showLoginScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginScene.fxml"));
        Parent root = loader.load();
        LoginSceneController controller = loader.getController();
        controller.setService(service);
        controller.setStage(window);
        window.setScene(new Scene(root, CONSTANTS.LOGIN_SCREEN_WIDTH, CONSTANTS.LOGIN_SCREEN_HEIGHT));


    }
}
