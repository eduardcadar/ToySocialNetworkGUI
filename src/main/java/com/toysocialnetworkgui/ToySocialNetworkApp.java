package com.toysocialnetworkgui;

import com.toysocialnetworkgui.controller.LoginSceneController;
import com.toysocialnetworkgui.domain.network.Network;
import com.toysocialnetworkgui.repository.db.*;
import com.toysocialnetworkgui.service.*;
import com.toysocialnetworkgui.utils.CONSTANTS;
import com.toysocialnetworkgui.validator.FriendshipValidator;
import com.toysocialnetworkgui.validator.ConversationParticipantValidator;
import com.toysocialnetworkgui.validator.MessageValidator;
import com.toysocialnetworkgui.validator.UserValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ToySocialNetworkApp extends Application {

    Scene loginScene;
    LoginSceneController loginSceneController;
    Service service;

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLogin = new FXMLLoader(ToySocialNetworkApp.class.getResource("controller/loginScene.fxml"));

        initialize();

        loginScene = new Scene(fxmlLogin.load(), CONSTANTS.LOGIN_SCREEN_WIDTH, CONSTANTS.LOGIN_SCREEN_HEIGHT);
        loginSceneController = fxmlLogin.getController();
        loginSceneController.initialize(service, primaryStage);
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        primaryStage.setUserData(exec);
        primaryStage.setOnCloseRequest(event ->
                ((ScheduledExecutorService)primaryStage.getUserData()).shutdown());

      //  primaryStage.setResizable(false);
        primaryStage.setTitle("Big Blana Society");
        primaryStage.getIcons().add(new Image("images/logo_small.png"));

        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void initialize() {
        String url = "jdbc:postgresql://localhost:5432/ToySocialNetwork";
        String username = "postgres";
        String password = "postgres";
        // USER
        UserDbRepo uRepo = new UserDbRepo(url, username, password, new UserValidator(), "users");
        UserService uSrv = new UserService(uRepo);
        FriendshipDbRepo fRepo = new FriendshipDbRepo(url, username, password, new FriendshipValidator(), "friendships", "users");
        FriendshipRequestDbRepo friendshipRequestRepo = new FriendshipRequestDbRepo(url, username, password,"requests");
        FriendshipService fSrv = new FriendshipService(fRepo, friendshipRequestRepo);
        // CONVERSATION
        ConversationDbRepo cRepo = new ConversationDbRepo(url, username, password, "conversations");
        MessageDbRepo mRepo = new MessageDbRepo(url, username, password, new MessageValidator(), "messages");
        MessageService mSrv = new MessageService(mRepo);
        ConversationParticipantDbRepo crRepo = new ConversationParticipantDbRepo(url, username, password, new ConversationParticipantValidator(), "participants");
        ConversationService mrSrv = new ConversationService(cRepo, crRepo);
        Network network = new Network(uRepo, fRepo);

        /// EVENTS
        EventDbRepo eventRepo = new EventDbRepo(url, username, password, "events");
        EventsSubscriptionDbRepo eventsSubscriptionRepo = new EventsSubscriptionDbRepo(url,username,password, "events_subscription", "events");
        EventService eventService = new EventService(eventRepo,eventsSubscriptionRepo);
        this.service = new Service(uSrv, fSrv, mSrv, mrSrv, network,eventService );
    }
}