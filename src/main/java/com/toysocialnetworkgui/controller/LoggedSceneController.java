package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.DbException;
import com.toysocialnetworkgui.repository.db.FriendshipDbRepo;
import com.toysocialnetworkgui.repository.observer.Observer;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.CONSTANTS;
import com.toysocialnetworkgui.utils.MyAlert;
import com.toysocialnetworkgui.utils.UserFriendDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoggedSceneController implements Observer {
    @FXML
    Button buttonShowConversation;

    @FXML
    AnchorPane rightPane;

    @FXML
    Text textUserFullName;

    @FXML
    Text textNrMessages;

    @FXML
    Text textNrEvents;

    @FXML
    Text textNrFriends;

    @FXML
    Button buttonRemoveFriend = new Button();
    @FXML
    Button buttonFriendRequest;
    @FXML
    Button buttonAddFriend;
    @FXML
    Button buttonFriendReport;
    @FXML
    Button buttonActivitiesReport;

    @FXML
    ComboBox<String> comboBoxMonth;
    @FXML
    Button buttonUpdateUser;

    @FXML
    Button buttonLogout;

    @FXML
    Button buttonSearch;

    @FXML
    Circle imagePlaceHolder;

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

    @FXML
    Button buttonEvents;

    @FXML
    ImageView imageViewNotification = new ImageView();


    private User loggedUser;
    private Service service;
    private Stage window;
    private int pageNumber;
    private int pageSize;
    private String currentSearchPattern;
    private int currentMonthFilter;
    private ScheduledExecutorService exec;

    public void initialize(Service service, User user, Stage window) {
        this.window = window;
        this.loggedUser = user;
        this.service = service;
        pageNumber = 1;
        pageSize = 2;
        currentSearchPattern = "";
        currentMonthFilter = 0;
        setLoggedUser(user);
        initializeFriendsList();
        tableViewFriends.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        comboBoxMonth.setItems(getMonths());
        service.getFriendshipRepo().addObserver(this);
        int numberOfNotification = service.getUserUpcomingEvents(loggedUser.getEmail()).size();
        if (numberOfNotification != 0) {
            imageViewNotification.setImage(new Image("images/active_notification.png"));
        } else {
            imageViewNotification.setImage(new Image("images/no_notification.png"));
        }
        setupProfilePicture();

        exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            if (service.getUserUpcomingEvents(loggedUser.getEmail()).size() > 0)
                if (!imageViewNotification.getImage().getUrl().equals("images/no_notification.png"))
                    imageViewNotification.setImage(new Image("images/active_notification.png"));
        }, 5, 60, TimeUnit.SECONDS);

        window.setOnCloseRequest(event -> tearDown());
    }

    public void tearDown() {
        if (!exec.isShutdown()) exec.shutdown();
    }

    private void setupProfilePicture() {
        imagePlaceHolder.setStroke(Color.web("#862CE4"));
        Image im = new Image(loggedUser.getProfilePicturePath());
        imagePlaceHolder.setFill(new ImagePattern(im));
    }

    /**
     * Filter the friends searching by name from textFieldSearchFriend
     */
    public void onButtonSearchFriend() {
        pageNumber = 1;
        currentSearchPattern = textFieldSearchFriend.getText().toLowerCase(Locale.ROOT);
        setFriendsList(getFriends());
    }

    /**
     * Show all friends if the textField for searching is empty
     */
    public void clearSearchFriendSelection() {
        String input = textFieldSearchFriend.getText().toLowerCase(Locale.ROOT);
        if (input.equals(""))
            setFriendsList(getFriends());
    }

    private void setLoggedUser(User user) {
        loggedUser = user;
        String firstName = user.getFirstName();
        firstName = firstName.substring(0,1).toUpperCase() + firstName.substring(1).toLowerCase();
        String lastName = user.getLastName();
        lastName = lastName.substring(0,1).toUpperCase() + lastName.substring(1).toLowerCase();

        textUserFullName.setText(firstName + " "+ lastName);
        textNrEvents.setText(String.valueOf(service.getEventsForUser(loggedUser.getEmail()).size()));
        textNrFriends.setText(String.valueOf(service.getUserFriends(loggedUser.getEmail()).size()));
        textNrMessages.setText(String.valueOf(service.getUserConversations(loggedUser.getEmail()).size()));
    }

    private ObservableList<String> getMonths() {
        return FXCollections.observableArrayList(Arrays.asList(
                "any month",
                "january", "february", "march", "april",
                "may", "june", "july", "august",
                "september", "october", "november", "december"));
    }

    @FXML
    protected void onButtonActivitiesReportClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("activitiesReportChooseDate.fxml"));
        Parent root = loader.load();

        ActivitiesReportChooseDateController controller = loader.getController();
        controller.initialize(service, loggedUser);
        rightPane.getChildren().setAll(root);
    }

    @FXML
    protected void onButtonFriendReportClick(ActionEvent event) throws IOException {
        if (tableViewFriends.getSelectionModel().getSelectedItems().size() != 1) {
            MyAlert.StartAlert("Error", "Select one friend!", Alert.AlertType.WARNING);
            return;
        }
        String userEmail = tableViewFriends.getSelectionModel().getSelectedItem().getEmail();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("friendReportChooseDate.fxml"));
        Parent root = loader.load();
        FriendReportChooseDateController controller = loader.getController();
        controller.initialize(service, loggedUser, service.getUser(userEmail));
        rightPane.getChildren().setAll(root);
    }

    @FXML
    protected void onSelectMonth() {
        pageNumber = 1;
        String month = comboBoxMonth.getValue();
        if (month == null)
            month = "any month";
        switch (month) {
            case "january" -> currentMonthFilter = 1;
            case "february" -> currentMonthFilter = 2;
            case "march" -> currentMonthFilter = 3;
            case "april" -> currentMonthFilter = 4;
            case "may" -> currentMonthFilter = 5;
            case "june" -> currentMonthFilter = 6;
            case "july" -> currentMonthFilter = 7;
            case "august" -> currentMonthFilter = 8;
            case "september" -> currentMonthFilter = 9;
            case "october" -> currentMonthFilter = 10;
            case "november" -> currentMonthFilter = 11;
            case "december" -> currentMonthFilter = 12;
            default -> currentMonthFilter = 0;
        }
        setFriendsList(FXCollections.observableArrayList(service
                .getFriendshipsDTOMonthFilteredPage(loggedUser.getEmail(), pageNumber, pageSize, currentSearchPattern, currentMonthFilter)));
    }

    private void reloadFriends() {
        setFriendsList(getFriends());
    }

    private ObservableList<UserFriendDTO> getFriends() {
        return FXCollections.observableArrayList(service
                .getFriendshipsDTOMonthFilteredPage(loggedUser.getEmail(), pageNumber, pageSize, currentSearchPattern, currentMonthFilter));
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

        textFieldSearchFriend.textProperty().addListener(listener -> clearSearchFriendSelection());
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
        int lastPage = ((service
                .getUserFriendsMonthFilteredSize(loggedUser.getEmail(), currentSearchPattern, currentMonthFilter) - 1) / pageSize) + 1;
        if (pageNumber == lastPage)
            return;
        pageNumber++;
        reloadFriends();
    }

    @FXML
    protected void onUpdateButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("updateUser.fxml"));
        Parent root = loader.load();
        UpdateUserController controller = loader.getController();
        controller.setService(service);
        controller.initialize(loggedUser, window);
        rightPane.getChildren().setAll(root);

        setLoggedUser(service.getUser(loggedUser.getEmail()));
    }

    @FXML
    protected void onShowConversationButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("conversationScene.fxml"));
        Parent root = loader.load();
        ConversationController controller = loader.getController();
        controller.initialize(service, loggedUser, rightPane, exec);
        rightPane.getChildren().setAll(root);
    }

    @FXML
    protected void onAddFriendButtonClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addFriend.fxml"));
        Parent root = fxmlLoader.load();
        AddFriendController controller = fxmlLoader.getController();
        controller.initialize(service, loggedUser, window);
        rightPane.getChildren().setAll(root);
    }

    @FXML
    protected void onEventsClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("eventsScene.fxml"));
        Parent dashboard = fxmlLoader.load();
        EventsController controller = fxmlLoader.getController();
        controller.initialize(service, loggedUser, window);
        rightPane.getChildren().setAll(dashboard);
    }

    @FXML
    protected void onRemoveFriendButtonClick() {
        if (tableViewFriends.getSelectionModel().isEmpty())
            return;
        UserFriendDTO friend = tableViewFriends.getSelectionModel().getSelectedItem();
        try {
            service.removeFriendship(loggedUser.getEmail(), friend.getEmail());
        } catch (RepoException | DbException e) {
            MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    /**
     * Opens a new Stage to handle user interactions with friend requests
     * @throws IOException - from load
     */
    @FXML
    protected void onFriendRequestClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("requestsScene.fxml"));
        Parent dashboard = loader.load();
        RequestsController controller = loader.getController();
        controller.initialize(service, loggedUser);
        rightPane.getChildren().setAll(dashboard);
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

    @Override
    public void update(Object obj) {
        if (obj instanceof FriendshipDbRepo) reloadFriends();
    }

    /**
     * Changes the image for the notification to the no_notification. Customize it late
     * + Show the events in a drop box maybe?
     */
    @FXML
    public void clearNotificationImage() {
        System.out.print("Subscribed events: ");
        service.getUserUpcomingEvents(loggedUser.getEmail()).forEach(System.out::println);
        imageViewNotification.setImage(new Image("images/no_notification.png"));
        // TODO
        //  - Show only the subscribed events somewhere

    }
}
