package com.toysocialnetworkgui;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.PasswordEncryptor;
import com.toysocialnetworkgui.utils.UserFriendDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Date;

public class LoggedSceneController {
    @FXML
    Button buttonUpdateUser = new Button();
    @FXML
    private Label labelLoggedUser = new Label();
    @FXML
    TableView<UserFriendDTO> tableViewFriends;
    @FXML
    TableColumn<UserFriendDTO, String> tableColumnFirstname;
    @FXML
    TableColumn<UserFriendDTO, String> tableColumnLastname;
    @FXML
    TableColumn<UserFriendDTO, Date> tableColumnDate;

    private User loggedUser;
    private Service service;

    public void setService(Service service) {
        this.service = service;
    }

    public void initialize(User user) {
        setLoggedUser(user);
        initializeFriendsList(user);
    }

    private void setLoggedUser(User user) {
        loggedUser = user;
        labelLoggedUser.setText("Logged user: " + user);
    }

    private ObservableList<UserFriendDTO> getFriends() {
        ObservableList<UserFriendDTO> friends = FXCollections.observableArrayList();
        friends.setAll(service.getFriendshipsDTO(loggedUser.getEmail()));
        return friends;
    }

    private void initializeFriendsList(User user) {
        tableColumnFirstname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        tableViewFriends.setItems(getFriends());
    }

    @FXML
    protected void onUpdateButtonClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("updateUser.fxml"));
        Parent root = loader.load();
        UpdateUserController controller = loader.getController();
        controller.setService(service);
        controller.initialize(loggedUser);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner((Stage)((Node) event.getSource()).getScene().getWindow());
        stage.setTitle("Update user information");
        stage.setScene(new Scene(root));
        stage.showAndWait();

        setLoggedUser(service.getUser(loggedUser.getEmail()));
    }

}
