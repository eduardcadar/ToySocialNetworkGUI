package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.DbException;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.CONSTANTS;
import com.toysocialnetworkgui.utils.UserFriendDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LoggedSceneController {
    @FXML
    Button buttonShowConversation;
  
    @FXML
    Button buttonRemoveFriend = new Button();
    @FXML
    Button buttonFriendRequest = new Button();
    @FXML
    Button buttonAddFriend;

    @FXML
    ComboBox<String> comboBoxMonth;
    @FXML
    Button buttonUpdateUser;
    @FXML
    Button buttonSendMessage;

    @FXML
    Button buttonLogout;


    @FXML
    private Label labelLoggedUser;
    @FXML
    TableView<UserFriendDTO> tableViewFriends;
    @FXML
    TableColumn<UserFriendDTO, String> tableColumnEmail;
    @FXML
    TableColumn<UserFriendDTO, String> tableColumnFirstname;
    @FXML
    TableColumn<UserFriendDTO, String> tableColumnLastname;
    @FXML
    TableColumn<UserFriendDTO, Date> tableColumnDate;

    private User loggedUser;
    private Service service;
    private Stage window;

    public void setService(Service service) {
        this.service = service;
    }

    public void initialize(User user) {
        setLoggedUser(user);
        initializeFriendsList();
        tableViewFriends.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        comboBoxMonth.setItems(getMonths());
    }

    private void setLoggedUser(User user) {
        loggedUser = user;
        labelLoggedUser.setText("Logged user: " + user);
    }

    private ObservableList<String> getMonths() {
        return FXCollections.observableArrayList(Arrays.asList(
                "any month",
                "january", "february", "march", "april",
                "may", "june", "july", "august",
                "september", "october", "november", "december"));
    }

    @FXML
    protected void onSelectMonth() {
        String month = comboBoxMonth.getValue();
        int monthNr;
        if (month == null)
            month = "any month";
        switch (month) {
            case "january" -> monthNr = 1;
            case "february" -> monthNr = 2;
            case "march" -> monthNr = 3;
            case "april" -> monthNr = 4;
            case "may" -> monthNr = 5;
            case "june" -> monthNr = 6;
            case "july" -> monthNr = 7;
            case "august" -> monthNr = 8;
            case "september" -> monthNr = 9;
            case "october" -> monthNr = 10;
            case "november" -> monthNr = 11;
            case "december" -> monthNr = 12;
            default -> monthNr = 0;
        }
        if (monthNr == 0)
            setFriendsList(getFriends());
        else
            setFriendsList(getFriends().
                    filtered(x -> x.getDate().getMonthValue() == monthNr));
    }

    private void reloadFriends() {
        onSelectMonth(); // ??
    }

    private ObservableList<UserFriendDTO> getFriends() {
        return FXCollections.observableArrayList(service
                .getFriendshipsDTO(loggedUser.getEmail()));
    }

    private void setFriendsList(ObservableList<UserFriendDTO> friends) {
        tableViewFriends.setItems(friends);
    }

    private void initializeFriendsList() {
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnFirstname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        setFriendsList(getFriends());
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
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());
        stage.setTitle("Update user information");
        stage.setScene(new Scene(root));
        stage.showAndWait();

        setLoggedUser(service.getUser(loggedUser.getEmail()));
    }

    @FXML
    protected void onShowConversationButtonClick(ActionEvent event) throws IOException {
        if (tableViewFriends.getSelectionModel().isEmpty())
            return;
        User otherUser = service.getUser(tableViewFriends.
                getSelectionModel().getSelectedItem().getEmail());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("conversationScene.fxml"));
        Parent root = loader.load();
        ConversationController controller = loader.getController();
        controller.initialize(service, loggedUser, otherUser);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)event.getSource()).getScene().getWindow());
        stage.setTitle("Conversation with " + otherUser);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    @FXML
    protected void onSendMessageButtonClick(ActionEvent event) throws IOException {
        if (tableViewFriends.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No friends selected");
            alert.setHeaderText(null);
            alert.setContentText("Select at least one friend!");
            alert.showAndWait();
            return;
        }
        List<String> receivers = new ArrayList<>();
        tableViewFriends.getSelectionModel().getSelectedItems()
                .forEach(x -> receivers.add(x.getEmail()));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sendMessage.fxml"));
        Parent root = loader.load();
        SendMessageController controller = loader.getController();
        controller.initialize(service, receivers, loggedUser);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)event.getSource()).getScene().getWindow());
        stage.setTitle("Send message");
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    @FXML
    protected void onAddFriendButtonClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addFriend.fxml"));
        Parent root = loader.load();
        AddFriendController controller = loader.getController();
        controller.initialize(service, loggedUser);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)event.getSource()).getScene().getWindow());
        stage.setTitle("Add friend");
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }
    @FXML
    protected void onRemoveFriendButtonClick() {
        if (tableViewFriends.getSelectionModel().isEmpty())
            return;
        UserFriendDTO friend = tableViewFriends.getSelectionModel().getSelectedItem();
        try {
            service.removeFriendship(loggedUser.getEmail(), friend.getEmail());
        } catch (RepoException | DbException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        reloadFriends();
    }

    /**
     * Opens a new Stage to handle user interactions with friend requests
     * @param event - the event that triggered the function
     * @throws IOException - from load
     */
    @FXML
    protected void onFriendRequestClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("requestsScene.fxml"));
        Parent root = loader.load();
        RequestsController controller = loader.getController();
        controller.initialize(service, loggedUser);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());
        stage.setTitle("Requests interface");
        stage.setScene( new Scene(root));
        stage.showAndWait();

        // TODO
        //  Refresh the friend list after requests menu ??
        //  Rather notify this LoggedScene to update his friendListTable
        //  at the signal made by onButtonClickAccept
        reloadFriends();
    }

    /**
     * Press R to  refresh table
     * Might delete later ?? doesn't refresh when 2 instances work
     * @param keyEvent - the event that triggered the function
     */
    public void onRefreshFriends(KeyEvent keyEvent) {
        System.out.println(keyEvent.getCode());
        if(keyEvent.isAltDown()){
            if(keyEvent.getCode().equals(KeyCode.R)) {
                reloadFriends();
            }
        }
    }

    public void setStage(Stage window) {
        this.window = window;
    }

    @FXML
    protected void onLogoutButtonClick(ActionEvent event) throws IOException {
        showLoginScene();

    }

    private void showLoginScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginScene.fxml"));
        Parent root = loader.load();
        LoginSceneController controller = loader.getController();
        controller.setService(service);
        controller.setStage(window);
        window.setScene(new Scene(root, CONSTANTS.LOGIN_SCREEN_WIDTH, CONSTANTS.LOGIN_SCREEN_HEIGHT));
    }
}
