package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.Conversation;
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
import java.util.*;

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
    Button buttonLogout;

    @FXML
    ListView<Conversation> listConversations;

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

    @FXML
    Button previousPage;
    @FXML
    Button nextPage;

    @FXML
    TextField textFieldSearchFriend;

    private User loggedUser;
    private Service service;
    private Stage window;
    private int pageNumber;
    private int pageSize;
    private int lastPage;

    public void setService(Service service) {
        this.service = service;
    }

    /**
     * Filter the friends searching by name from textFieldSearchFriend
     */
    public void onSearchFriend(){
        String input = textFieldSearchFriend.getText().toLowerCase(Locale.ROOT);
        if(input.equals(""))
            setFriendsList(getFriends());
        else
            setFriendsList(getFriends().
                    filtered(x -> {
                        String fullName = x.getFirstName().toLowerCase(Locale.ROOT) +' ' + x.getLastName().toLowerCase(Locale.ROOT);
                        return fullName.contains(input);
                    } ));

    }

    public void initialize(User user) {
        pageNumber = 1;
        pageSize = 2;
        setLoggedUser(user);
        initializeFriendsList();
        reloadConversationsList();
        tableViewFriends.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        comboBoxMonth.setItems(getMonths());
    }

    private void reloadConversationsList() {
        listConversations.getItems().setAll(service.getUserConversations(loggedUser.getEmail()));
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
                .getFriendshipsDTOPage(loggedUser.getEmail(), pageNumber, pageSize));
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
        textFieldSearchFriend.textProperty().addListener(ev-> onSearchFriend());
    }

    @FXML
    protected void onPreviousPageButtonClick() {
        if (pageNumber == 1)
            return;
        pageNumber--;
        reloadFriends();
    }

    @FXML
    protected void onNextPageButtonClick() {
        // TODO - find the value for last page in a more efficient way
        lastPage = ((service.getUserFriends(loggedUser.getEmail()).size() - 1) / pageSize) + 1;
        if (pageNumber == lastPage)
            return;
        pageNumber++;
        reloadFriends();
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
        int idConversation;
        if (!listConversations.getSelectionModel().isEmpty()) {
            idConversation = listConversations.
                    getSelectionModel().getSelectedItem().getID();
        } else if (!tableViewFriends.getSelectionModel().isEmpty()) {
            List<String> participants = new ArrayList<>();
            participants.add(loggedUser.getEmail());
            tableViewFriends.getSelectionModel().getSelectedItems()
                    .forEach(p -> participants.add(p.getEmail()));
            idConversation = service.getConversation(participants).getID();
        } else return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("conversationScene.fxml"));
        Parent root = loader.load();
        ConversationController controller = loader.getController();
        controller.initialize(service, loggedUser, idConversation);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)event.getSource()).getScene().getWindow());
        stage.setTitle("Conversation");
        stage.setScene(new Scene(root));
        stage.showAndWait();
        reloadConversationsList();
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
    protected void onLogoutButtonClick() throws IOException {
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
