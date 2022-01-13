package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.MyAlert;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateConversationController {
    private Service service;
    private User loggedUser;
    private int pageNumber;
    private int pageSize;
    private String currentSearchPattern;
    private AnchorPane rightPane;

    @FXML
    TextField textFieldSearchFriend;
    @FXML
    ListView<User> listFriends;
    @FXML
    ListView<User> listConversationFriends;
    @FXML
    Button buttonSearch;
    @FXML
    Button buttonAddFriendToConversation;
    @FXML
    Button buttonRemoveFriendFromConversation;
    @FXML
    Button buttonCreateConversation;
    @FXML
    Button previousPage;
    @FXML
    Button nextPage;

    public void initialize(Service service, User loggedUser, AnchorPane rightPane) {
        this.service = service;
        this.loggedUser = loggedUser;
        this.pageNumber = 1;
        this.pageSize = 5;
        this.currentSearchPattern = "";
        this.rightPane = rightPane;
        reloadFriends();
    }

    private void reloadFriends() {
        listFriends.getItems().setAll(service.getUserFriendsFilteredPage(loggedUser.getEmail(), pageNumber, pageSize, currentSearchPattern));
    }

    @FXML
    protected void onButtonSearchClick() {
        currentSearchPattern = textFieldSearchFriend.getText();
        reloadFriends();
    }

    @FXML
    protected void onButtonPreviousPageClick() {
        if (pageNumber == 1)
            return;
        pageNumber--;
        reloadFriends();
    }

    @FXML
    protected void onButtonNextPageClick() {
        int lastPage = ((service
                .getUserFriendsFilteredSize(loggedUser.getEmail(), currentSearchPattern) - 1) / pageSize) + 1;
        if (pageNumber == lastPage)
            return;
        pageNumber++;
        reloadFriends();
    }

    @FXML
    protected void onButtonAddFriendToConversationClick() {
        if (listFriends.getSelectionModel().isEmpty())
            return;
        if (listConversationFriends.getItems().contains(listFriends.getSelectionModel().getSelectedItem())) {
            MyAlert.StartAlert("Error", "Friend already added to conversation", Alert.AlertType.WARNING);
            return;
        }
        listConversationFriends.getItems().add(listFriends.getSelectionModel().getSelectedItem());
    }

    @FXML
    protected void onButtonRemoveFriendFromConversationClick() {
        if (listConversationFriends.getSelectionModel().isEmpty())
            return;
        listConversationFriends.getItems().remove(listConversationFriends.getSelectionModel().getSelectedItem());
    }

    @FXML
    protected void onButtonCreateConversationClick() throws IOException {
        if (listConversationFriends.getItems().isEmpty()) {
            MyAlert.StartAlert("Error", "Add friends to the conversation", Alert.AlertType.WARNING);
            return;
        }
        List<String> participantsEmails = new ArrayList<>();
        participantsEmails.add(loggedUser.getEmail());
        listConversationFriends.getItems().forEach(f -> participantsEmails.add(f.getEmail()));
        int convId = service.getConversation(participantsEmails).getID();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("conversationScene.fxml"));
        Parent root = loader.load();
        ConversationController controller = loader.getController();
        controller.initialize(service, loggedUser, rightPane, convId);
        rightPane.getChildren().setAll(root);
    }
}
