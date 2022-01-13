package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.db.ConversationDbRepo;
import com.toysocialnetworkgui.repository.db.ConversationParticipantDbRepo;
import com.toysocialnetworkgui.repository.db.EventsSubscriptionDbRepo;
import com.toysocialnetworkgui.repository.db.FriendshipDbRepo;
import com.toysocialnetworkgui.repository.observer.Observer;
import com.toysocialnetworkgui.service.ConversationService;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.CONSTANTS;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
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
    Text textNrConversations;
    @FXML
    Text textNrEvents;
    @FXML
    Text textNrFriends;

    @FXML
    Button buttonFriends;
    @FXML
    Button buttonFriendRequest;
    @FXML
    Button buttonFriendReport;
    @FXML
    Button buttonActivitiesReport;
    @FXML
    Button buttonUpdateUser;
    @FXML
    Button buttonLogout;
    @FXML
    Circle imagePlaceHolder;

    @FXML
    Button buttonEvents;
    @FXML
    ImageView imageViewNotification = new ImageView();

    private User loggedUser;
    private Service service;
    private Stage window;

    public void initialize(Service service, User user, Stage window) {
        this.window = window;
        this.loggedUser = user;
        this.service = service;

        service.getFriendshipRepo().addObserver(this);
        service.getConversationParticipantsRepo().addObserver(this);
        service.getConversationService().addObserver(this);

        setLoggedUser(user);
        int numberOfNotification = service.getUserUpcomingEvents(loggedUser.getEmail()).size();
        if (numberOfNotification != 0) {
            imageViewNotification.setImage(new Image("images/active_notification.png"));
        } else {
            imageViewNotification.setImage(new Image("images/no_notification.png"));
        }
        setupProfilePicture();

        ((ScheduledExecutorService)window.getUserData())
                .scheduleAtFixedRate(() -> {
            if (!service.getUserUpcomingEvents(loggedUser.getEmail()).isEmpty())
                if (!imageViewNotification.getImage().getUrl().equals("images/no_notification.png"))
                    imageViewNotification.setImage(new Image("images/active_notification.png"));
        }, 5, 60, TimeUnit.SECONDS);
    }

    private void setupProfilePicture() {
        imagePlaceHolder.setStroke(Color.web("#862CE4"));
        Image im = new Image(loggedUser.getProfilePicturePath());
        imagePlaceHolder.setFill(new ImagePattern(im));
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
        textNrConversations.setText(String.valueOf(service.getUserConversations(loggedUser.getEmail()).size()));
    }

    @FXML
    protected void onButtonFriendsClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("friends.fxml"));
        Parent root = loader.load();
        FriendsController controller = loader.getController();
        controller.initialize(service, loggedUser, rightPane);
        rightPane.getChildren().setAll(root);
    }

    @FXML
    protected void onButtonActivitiesReportClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("activitiesReportChooseDate.fxml"));
        Parent root = loader.load();
        ActivitiesReportChooseDateController controller = loader.getController();
        controller.initialize(service, loggedUser, rightPane);
        rightPane.getChildren().setAll(root);
    }

    @FXML
    protected void onButtonFriendReportClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("friendReportChooseDate.fxml"));
        Parent root = loader.load();
        FriendReportChooseDateController controller = loader.getController();
        controller.initialize(service, loggedUser, rightPane);
        rightPane.getChildren().setAll(root);
    }

    @FXML
    protected void onUpdateButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("updateUser.fxml"));
        Parent root = loader.load();
        UpdateUserController controller = loader.getController();
        controller.initialize(loggedUser, window, service);
        rightPane.getChildren().setAll(root);

        setLoggedUser(service.getUser(loggedUser.getEmail()));
    }

    @FXML
    protected void onShowConversationButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("conversationScene.fxml"));
        Parent root = loader.load();
        ConversationController controller = loader.getController();
        controller.initialize(service, loggedUser, rightPane);
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
        controller.initialize(service, window);
        window.setScene(new Scene(root, CONSTANTS.LOGIN_SCREEN_WIDTH, CONSTANTS.LOGIN_SCREEN_HEIGHT));
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

    @Override
    public void update(Object obj) {
        if (obj instanceof FriendshipDbRepo) setLoggedUser(loggedUser);
        if (obj instanceof EventsSubscriptionDbRepo) setLoggedUser(loggedUser);
        if (obj instanceof ConversationService) setLoggedUser(loggedUser);
    }
}
