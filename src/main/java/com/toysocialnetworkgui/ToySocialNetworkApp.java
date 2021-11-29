package com.toysocialnetworkgui;

import com.toysocialnetworkgui.domain.network.Network;
import com.toysocialnetworkgui.repository.db.*;
import com.toysocialnetworkgui.service.*;
import com.toysocialnetworkgui.validator.FriendshipValidator;
import com.toysocialnetworkgui.validator.MessageReceiverValidator;
import com.toysocialnetworkgui.validator.MessageValidator;
import com.toysocialnetworkgui.validator.UserValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ToySocialNetworkApp extends Application {

    Scene loginScene;
    Scene loggedScene;
    LoginSceneController loginSceneController;
    LoggedSceneController loggedSceneController;
    Service service;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLogin = new FXMLLoader(ToySocialNetworkApp.class.getResource("loginScene.fxml"));
        FXMLLoader fxmlLogged = new FXMLLoader(ToySocialNetworkApp.class.getResource("loggedScene.fxml"));
        initialize();

        loginScene = new Scene(fxmlLogin.load(), 400, 400);
        loggedScene = new Scene(fxmlLogged.load(), 400, 400);
        loginSceneController = fxmlLogin.getController();
        loginSceneController.setService(service);
        loggedSceneController = fxmlLogged.getController();
        loggedSceneController.setService(service);

        primaryStage.setTitle("ToySocialNetwork");
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
        UserDbRepo uRepo = new UserDbRepo(url, username, password, new UserValidator(), "users");
        UserService uSrv = new UserService(uRepo);
        FriendshipDbRepo fRepo = new FriendshipDbRepo(url, username, password, new FriendshipValidator(), "friendships");
        FriendshipRequestDbRepo friendshipRequestRepo = new FriendshipRequestDbRepo(url, username, password,"requests");
        FriendshipService fSrv = new FriendshipService(fRepo, friendshipRequestRepo);
        MessageDbRepo mRepo = new MessageDbRepo(url, username, password, new MessageValidator(), "messages");
        MessageService mSrv = new MessageService(mRepo);
        MessageReceiverDbRepo mrRepo = new MessageReceiverDbRepo(url, username, password, new MessageReceiverValidator(), "receivers");
        MessageReceiverService mrSrv = new MessageReceiverService(mrRepo);
        Network network = new Network(uRepo, fRepo);
        this.service = new Service(uSrv, fSrv, mSrv, mrSrv, network);
    }
}