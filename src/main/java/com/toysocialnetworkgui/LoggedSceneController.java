package com.toysocialnetworkgui;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.UserFriendDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Date;

public class LoggedSceneController {
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

}
