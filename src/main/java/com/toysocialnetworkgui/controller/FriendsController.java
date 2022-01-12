package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.DbException;
import com.toysocialnetworkgui.repository.db.FriendshipDbRepo;
import com.toysocialnetworkgui.repository.observer.Observer;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.MyAlert;
import com.toysocialnetworkgui.utils.UserFriendDTO;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class FriendsController implements Observer {
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
    TableColumn<UserFriendDTO, ImageView> tableColumnRemove;
    @FXML
    TableColumn<UserFriendDTO, ImageView> tableColumnMessage;

    @FXML
    Label labelPage;
    @FXML
    ComboBox<String> comboBoxMonth;
    @FXML
    TextField textFieldSearchFriend;
    @FXML
    Button buttonSearch;
    @FXML
    Button buttonPreviousPage;
    @FXML
    Button buttonNextPage;

    private AnchorPane rightPane;
    private Service service;
    private User loggedUser;
    private String currentSearchPattern;
    private int pageNumber;
    private int pageSize;
    private int currentMonthFilter;

    public void initialize(Service service, User loggedUser, AnchorPane rightPane) {
        this.rightPane = rightPane;
        this.service = service;
        this.loggedUser = loggedUser;
        currentMonthFilter = 0;
        pageNumber = 1;
        pageSize = 2;
        currentSearchPattern = "";
        service.getFriendshipRepo().addObserver(this);
        comboBoxMonth.setItems(getMonths());
        initializeFriendsList();
        reloadFriends();
    }

    private ObservableList<UserFriendDTO> getFriends() {
        return FXCollections.observableArrayList(service
                .getFriendshipsDTOMonthFilteredPage(loggedUser.getEmail(), pageNumber, pageSize, currentSearchPattern, currentMonthFilter));
    }

    private void reloadFriends() {
        int lastPage = getLastPageNumber();
        buttonPreviousPage.setVisible(pageNumber != 1);
        buttonNextPage.setVisible(pageNumber != lastPage);

        labelPage.setText("Page " + pageNumber + " of " + lastPage);
        tableViewFriends.setItems(getFriends());
    }

    private void initializeFriendsList() {
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnFirstname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableColumnRemove.setStyle("-fx-cursor:hand");
        tableColumnRemove.setCellValueFactory(p -> new ObservableValue<>() {
            @Override
            public void addListener(ChangeListener<? super ImageView> listener) {}

            @Override
            public void removeListener(ChangeListener<? super ImageView> listener) {}

            @Override
            public ImageView getValue() {
                ImageView imageView = new ImageView();
                imageView.setFitHeight(30);
                imageView.setFitWidth(30);
                imageView.setImage(new Image("images/rejected_request.png"));
                Tooltip.install(imageView, new Tooltip("remove friend"));
                return imageView;
            }

            @Override
            public void addListener(InvalidationListener listener) {}

            @Override
            public void removeListener(InvalidationListener listener) {}
        });
        tableColumnMessage.setCellValueFactory(p -> new ObservableValue<>() {
            @Override
            public void addListener(ChangeListener<? super ImageView> listener) {}

            @Override
            public void removeListener(ChangeListener<? super ImageView> listener) {}

            @Override
            public ImageView getValue() {
                ImageView imageView = new ImageView();
                imageView.setFitHeight(30);
                imageView.setFitWidth(30);
                imageView.setImage(new Image("images/message.png"));
                Tooltip.install(imageView, new Tooltip("message"));
                return imageView;
            }

            @Override
            public void addListener(InvalidationListener listener) {}

            @Override
            public void removeListener(InvalidationListener listener) {}
        });
        textFieldSearchFriend.textProperty().addListener(listener -> clearSearchFriendSelection());
    }

    @FXML
    protected void handleTableClickEvent() throws IOException {
        if (tableViewFriends.getSelectionModel() != null)
            if (!tableViewFriends.getSelectionModel().getSelectedCells().isEmpty()) {
                if (tableViewFriends.getSelectionModel().getSelectedCells().get(0).getColumn() == 3)
                    removeFriend();
                else if (tableViewFriends.getSelectionModel().getSelectedCells().get(0).getColumn() == 4)
                    openConversation();
            }
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
        if (pageNumber == getLastPageNumber())
            return;
        pageNumber++;
        reloadFriends();
    }

    private int getLastPageNumber() {
        return ((service.getUserFriendsMonthFilteredSize(
                loggedUser.getEmail(), currentSearchPattern, currentMonthFilter) - 1) / pageSize) + 1;
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
        reloadFriends();
    }

    private ObservableList<String> getMonths() {
        return FXCollections.observableArrayList(Arrays.asList(
                "any month",
                "january", "february", "march", "april",
                "may", "june", "july", "august",
                "september", "october", "november", "december"));
    }

    /**
     * Filter the friends searching by name from textFieldSearchFriend
     */
    public void onButtonSearchFriend() {
        pageNumber = 1;
        currentSearchPattern = textFieldSearchFriend.getText().toLowerCase(Locale.ROOT);
        reloadFriends();
    }

    /**
     * Show all friends if the textField for searching is empty
     */
    public void clearSearchFriendSelection() {
        String input = textFieldSearchFriend.getText().toLowerCase(Locale.ROOT);
        if (input.equals(""))
            reloadFriends();
    }

    private void removeFriend() {
        if (tableViewFriends.getSelectionModel().isEmpty())
            return;
        UserFriendDTO friend = tableViewFriends.getSelectionModel().getSelectedItem();
        try {
            service.removeFriendship(loggedUser.getEmail(), friend.getEmail());
        } catch (RepoException | DbException e) {
            MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    private void openConversation() throws IOException {
        if (tableViewFriends.getSelectionModel().isEmpty())
            return;
        UserFriendDTO friend = tableViewFriends.getSelectionModel().getSelectedItem();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("conversationScene.fxml"));
        Parent root = loader.load();
        ConversationController controller = loader.getController();
        controller.initialize(service, loggedUser, rightPane, friend.getEmail());
        rightPane.getChildren().setAll(root);
    }

    @Override
    public void update(Object obj) {
        if (obj instanceof FriendshipDbRepo) reloadFriends();
    }
}
