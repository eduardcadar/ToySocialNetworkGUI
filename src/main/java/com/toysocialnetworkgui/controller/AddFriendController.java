package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.DbException;
import com.toysocialnetworkgui.repository.db.FriendshipRequestDbRepo;
import com.toysocialnetworkgui.repository.observer.Observer;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.CONSTANTS;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class AddFriendController {
    @FXML
    Button buttonAddFriend = new Button();
    @FXML
    TableView<User> tableViewUsers;
    @FXML
    TableColumn<User, String> tableColumnEmail;
    @FXML
    TableColumn<User, String> tableColumnFirstname;
    @FXML
    TableColumn<User, String> tableColumnLastname;

    private User loggedUser;
    private Service service;
    private Stage window;
    public void initialize(Service service, User user, Stage window) {
        this.service = service;
        this.loggedUser = user;
        this.window = window;
        initializeUsersList();
    }

    private ObservableList<User> getNotFriends() {
        return FXCollections.observableArrayList(
                service.getNotFriends(loggedUser.getEmail()));
    }

    private void initializeUsersList() {
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnFirstname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastname.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        tableViewUsers.setItems(getNotFriends());
    }

    @FXML
    protected void onAddFriendButtonClick(ActionEvent event) throws IOException {
        User friend = tableViewUsers.getSelectionModel().getSelectedItem();
        if (friend == null)
            return;
        try {
            service.addFriendship(loggedUser.getEmail(), friend.getEmail());
        } catch (RepoException | DbException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loggedScene.fxml"));
        Parent root = loader.load();
        LoggedSceneController controller = loader.getController();
        controller.initialize(service, loggedUser, window);
        Scene scene = new Scene(root, CONSTANTS.MAIN_SCREEN_WIDTH, CONSTANTS.MAIN_SCREEN_HEIGHT);
        window.setScene(scene);
    }
}
