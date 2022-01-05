package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.DbException;
import com.toysocialnetworkgui.repository.db.FriendshipRequestDbRepo;
import com.toysocialnetworkgui.repository.observer.Observer;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.MyAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

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

    public void initialize(Service service, User user) {
        this.service = service;
        this.loggedUser = user;
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
    protected void onAddFriendButtonClick(ActionEvent event) {
        User friend = tableViewUsers.getSelectionModel().getSelectedItem();
        if (friend == null)
            return;
        try {
            service.addFriendship(loggedUser.getEmail(), friend.getEmail());
        } catch (RepoException | DbException e) {
            MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.WARNING);
        }
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }
}
