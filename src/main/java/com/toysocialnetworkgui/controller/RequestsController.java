package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.DbException;
import com.toysocialnetworkgui.repository.db.FriendshipDbRepo;
import com.toysocialnetworkgui.repository.db.FriendshipRequestDbRepo;
import com.toysocialnetworkgui.repository.observer.Observer;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.CommonFriendsDTO;
import com.toysocialnetworkgui.utils.MyAlert;
import com.toysocialnetworkgui.utils.UserRequestDTO;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RequestsController implements Observer {
    private Service service;
    private User loggedUser;

    @FXML
    private TableView<UserRequestDTO> tableSentRequestsView;
    @FXML
    private TableColumn<UserRequestDTO, String> tableSentColumnFirstName;
    @FXML
    private TableColumn<UserRequestDTO, String> tableSentColumnLastName;
    @FXML
    private TableColumn<UserRequestDTO, ImageView > tableSentColumnState;
    @FXML
    private TableColumn<UserRequestDTO, String> tableSentColumnSentDate;
    @FXML
    private TableColumn<UserRequestDTO, ImageView> tableSentColumnCancel;

    @FXML
    private TableView<UserRequestDTO> tableReceivedRequestsView;
    @FXML
    private TableColumn<UserRequestDTO, String> tableReceivedColumnFirstName;
    @FXML
    private TableColumn<UserRequestDTO, String> tableReceivedColumnLastName;
    @FXML
    private TableColumn<UserRequestDTO, ImageView > tableReceivedColumnState;
    @FXML
    private TableColumn<UserRequestDTO, String> tableReceivedColumnSentDate;


    @FXML
    private TableColumn<UserRequestDTO, ImageView> tableAcceptRequest;
    @FXML
    private TableColumn<UserRequestDTO, ImageView> tableRejectRequest;

    @FXML
    TableView<CommonFriendsDTO> tableAddFriend;
    @FXML
    TableColumn<CommonFriendsDTO, String> tableAddFriendColumnEmail;
    @FXML
    TableColumn<CommonFriendsDTO, String> tableAddFriendColumnFirstname;
    @FXML
    TableColumn<CommonFriendsDTO, String> tableAddFriendColumnLastname;
    @FXML
    TableColumn<CommonFriendsDTO, Integer> tableAddFriendColumnCommonFriendsNr;
    @FXML
    TableColumn<CommonFriendsDTO, ImageView> tableAddFriendColumnSendReq;

    @FXML
    TextField textFieldSearch;
    @FXML
    Button buttonSearch;

    private String currentPattern;

    public void initialize(Service service, User loggedUser, AnchorPane rightPane) {
        this.service = service;
        this.loggedUser = loggedUser;
        currentPattern = "";
        initializeRequestsList();
        tableSentRequestsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableReceivedRequestsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        service.getRequestRepo().addObserver(this);
        service.getFriendshipRepo().addObserver(this);
        ((ScheduledExecutorService)rightPane.getScene().getWindow().getUserData())
                .scheduleAtFixedRate(() -> {
                    reloadRequestsTables();
                    reloadAddFriendTable();
                }, 5, 10, TimeUnit.SECONDS);
    }

    /**
     * Initialize the two tables corresponding to the Requested sent and Requested Received
     */
    private void initializeRequestsList() {
        initializeSentRequestsList();
        initializeReceivedRequestsList();
        initializeAddFriendList();
        // TODO
        //  - DONT KNOW WHY I CANT CALL THIS METHODS IN INITIALIZE
        //   - NO SORTING IS DONE THERE
        // start with the table sorted by sentDate descending
//        tableSentColumnSentDate.setSortType(TableColumn.SortType.DESCENDING);
//        tableSentRequestsView.getSortOrder().add(tableSentColumnSentDate);

//        tableReceivedColumnSentDate.setSortType(TableColumn.SortType.DESCENDING);
//        tableReceivedRequestsView.getSortOrder().add(tableReceivedColumnSentDate);
    }

    private ObservableList<CommonFriendsDTO> getNotFriends() {
        return FXCollections.observableArrayList(
                service.getUserFilteredCommonFriendsDTO(loggedUser.getEmail(), currentPattern));
    }

    private void initializeAddFriendList() {
        // To not let user re-arrange columns
        // We need this because of icons, they should stay at a constant position
        tableAddFriend.setPlaceholder(new Text("No users!"));
        tableAddFriend.getColumns().forEach(e -> e.setReorderable(false));

        tableAddFriendColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableAddFriendColumnFirstname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableAddFriendColumnLastname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableAddFriendColumnCommonFriendsNr.setCellValueFactory(new PropertyValueFactory<>("nrOfCommonFriends"));
        tableAddFriendColumnSendReq.setCellValueFactory(param -> new ObservableValue<>() {
            @Override
            public void addListener(ChangeListener<? super ImageView> listener) {}

            @Override
            public void removeListener(ChangeListener<? super ImageView> listener) {}

            @Override
            public ImageView getValue() {
                ImageView imageView = new ImageView();
                imageView.setFitHeight(30);
                imageView.setFitWidth(30);
                imageView.setImage(new Image("images/add_friend_man.png"));
                return imageView;
            }

            @Override
            public void addListener(InvalidationListener listener) {}

            @Override
            public void removeListener(InvalidationListener listener) {}
        });
        tableAddFriendColumnCommonFriendsNr.setStyle("-fx-alignment: center");
        tableAddFriend.setItems(getNotFriends());
    }

    private void initializeReceivedRequestsList() {
        // To not let user re-arrange columns
        // We need this because of icons, they should stay at a constant position
        tableReceivedRequestsView.setPlaceholder(new Text("No received requests!"));
        tableReceivedRequestsView.getColumns().forEach(e -> e.setReorderable(false));

        tableReceivedColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableReceivedColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableReceivedColumnState.setCellValueFactory(param -> new ObservableValue<ImageView>() {
            @Override
            public void addListener(ChangeListener<? super ImageView> listener) {}

            @Override
            public void removeListener(ChangeListener<? super ImageView> listener) {}

            @Override
            public ImageView getValue() {
                switch (param.getValue().getState()){
                    case APPROVED -> {
                        ImageView imageView = new ImageView();
                        imageView.setFitHeight(30);
                        imageView.setFitWidth(30);
                        imageView.setImage(new Image("images/accepted_request.png"));
                        return imageView;
                    }
                    case PENDING -> {
                        ImageView imageView = new ImageView();
                        imageView.setFitHeight(30);
                        imageView.setFitWidth(30);
                        imageView.setImage(new Image("images/pending_request.png"));
                        return imageView;
                    }
                    case REJECTED -> {
                        ImageView imageView = new ImageView();
                        imageView.setFitHeight(30);
                        imageView.setFitWidth(30);
                        imageView.setImage(new Image("images/rejected_request.png"));
                        return imageView;
                    }
                }
                ImageView imageView = new ImageView();
                imageView.setFitHeight(30);
                imageView.setFitWidth(30);
                imageView.setImage(new Image("images/error.png"));
                return imageView;
            }

            @Override
            public void addListener(InvalidationListener listener) {}

            @Override
            public void removeListener(InvalidationListener listener) {}
        });
        tableReceivedColumnSentDate.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getSendDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        tableAcceptRequest.setCellValueFactory(param -> new ObservableValue<>() {
            @Override
            public void addListener(ChangeListener<? super ImageView> listener) {}

            @Override
            public void removeListener(ChangeListener<? super ImageView> listener) {}

            @Override
            public ImageView getValue() {
                ImageView imageView = new ImageView();
                imageView.setFitHeight(30);
                imageView.setFitWidth(30);
                imageView.setImage(new Image("images/accept.png"));
                return imageView;
            }

            @Override
            public void addListener(InvalidationListener listener) {}

            @Override
            public void removeListener(InvalidationListener listener) {}
        });

        tableRejectRequest.setCellValueFactory(param -> new ObservableValue<>() {
            @Override
            public void addListener(ChangeListener<? super ImageView> listener) {}

            @Override
            public void removeListener(ChangeListener<? super ImageView> listener) {}

            @Override
            public ImageView getValue() {
                ImageView imageView = new ImageView();
                imageView.setFitHeight(30);
                imageView.setFitWidth(30);
                imageView.setImage(new Image("images/reject.png"));
                return imageView;
            }

            @Override
            public void addListener(InvalidationListener listener) {}

            @Override
            public void removeListener(InvalidationListener listener) {}
        });

        tableReceivedRequestsView.setItems(getReceivedRequests());
        tableReceivedColumnSentDate.setSortType(TableColumn.SortType.DESCENDING);
        tableReceivedRequestsView.getSortOrder().add(tableReceivedColumnSentDate);
    }

    private void initializeSentRequestsList() {
        //
        // To not let user re-arrange columns
        // We need this because of icons, they should stay at a constant position
        tableSentRequestsView.setPlaceholder(new Text("No sent requests!"));
        tableSentRequestsView.getColumns().forEach(e -> e.setReorderable(false));

        tableSentColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableSentColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableSentColumnState.setCellValueFactory(param -> new ObservableValue<ImageView>() {
            @Override
            public void addListener(ChangeListener<? super ImageView> listener) {}

            @Override
            public void removeListener(ChangeListener<? super ImageView> listener) {}

            @Override
            public ImageView getValue() {
                switch (param.getValue().getState()){
                    case APPROVED -> {
                        ImageView imageView = new ImageView();
                        imageView.setFitHeight(30);
                        imageView.setFitWidth(30);
                        imageView.setImage(new Image("images/accepted_request.png"));
                        return imageView;
                    }
                    case PENDING -> {
                        ImageView imageView = new ImageView();
                        imageView.setFitHeight(30);
                        imageView.setFitWidth(30);
                        imageView.setImage(new Image("images/pending_request.png"));
                        return imageView;
                    }
                    case REJECTED -> {
                        ImageView imageView = new ImageView();
                        imageView.setFitHeight(30);
                        imageView.setFitWidth(30);
                        imageView.setImage(new Image("images/rejected_request.png"));
                        return imageView;
                    }
                }
                ImageView imageView = new ImageView();
                imageView.setFitHeight(30);
                imageView.setFitWidth(30);
                imageView.setImage(new Image("images/error.png"));
                return imageView;
            }

            @Override
            public void addListener(InvalidationListener listener) {}

            @Override
            public void removeListener(InvalidationListener listener) {}
        });
        tableSentColumnSentDate.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getSendDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        tableSentColumnCancel.setCellValueFactory(param -> new ObservableValue<>() {
            @Override
            public void addListener(ChangeListener<? super ImageView> listener) {
            }

            @Override
            public void removeListener(ChangeListener<? super ImageView> listener) {
            }

            @Override
            public ImageView getValue() {
                ImageView imageView = new ImageView();
                imageView.setFitHeight(30);
                imageView.setFitWidth(30);
                imageView.setImage(new Image("images/reject.png"));
                return imageView;
            }

            @Override
            public void addListener(InvalidationListener listener) {}

            @Override
            public void removeListener(InvalidationListener listener) {}
        });

        tableSentRequestsView.setItems(getSentRequests());
        tableSentColumnSentDate.setSortType(TableColumn.SortType.DESCENDING);
        tableSentRequestsView.getSortOrder().add(tableSentColumnSentDate);
    }

    private ObservableList<UserRequestDTO> getSentRequests() {
        return FXCollections.observableArrayList(service.getUserSentRequests(loggedUser.getEmail()));
    }

    private ObservableList<UserRequestDTO> getReceivedRequests() {
        return FXCollections.observableArrayList(service.getUserReceivedRequests(loggedUser.getEmail()));
    }

    /**
     * Reloads both tables SentRequests and ReceivedRequests
     */
    public void reloadRequestsTables() {
        Platform.runLater(() -> tableSentRequestsView.setItems(getSentRequests()));
        Platform.runLater(() -> tableReceivedRequestsView.setItems(getReceivedRequests()));
    }

    @FXML
    protected void onButtonSearchClick() {
        currentPattern = textFieldSearch.getText();
        reloadAddFriendTable();
    }

    private void reloadAddFriendTable() {
        Platform.runLater(() -> tableAddFriend.setItems(getNotFriends()));
    }

    public void onButtonAcceptClick() {
        UserRequestDTO requestDTO = tableReceivedRequestsView.getSelectionModel().getSelectedItem();
        if (requestDTO != null) {
            try {
                service.acceptFriendship(requestDTO.getEmail(), loggedUser.getEmail());
            } catch (RepoException e) {
                MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.WARNING);
            }
        } else
            MyAlert.StartAlert("Error", "No request selected", Alert.AlertType.WARNING);
    }

    public void onButtonRejectClick(){
        UserRequestDTO requestDTO =  tableReceivedRequestsView.getSelectionModel().getSelectedItem();
        if (requestDTO != null) {
            try {
                service.rejectFriendship(requestDTO.getEmail(), loggedUser.getEmail());
            } catch (RepoException e) {
                MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.WARNING);
            }
        } else
            MyAlert.StartAlert("Error", "No request selected", Alert.AlertType.WARNING);
    }

    public void onButtonCancelClick() {
        UserRequestDTO dto = tableSentRequestsView.getSelectionModel().getSelectedItem();
        if (dto != null) {
            try {
                service.cancelPendingRequest(loggedUser.getEmail(), dto.getEmail());
            } catch (RepoException e) {
                MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.WARNING);
            }
        } else
            MyAlert.StartAlert("Error", "No request selected", Alert.AlertType.WARNING);
    }

    public void handleReceivedClickEvent() {
        if (tableReceivedRequestsView.getSelectionModel() != null) {
            if (tableReceivedRequestsView.getSelectionModel().getSelectedCells().size() > 0){
                if ((tableReceivedRequestsView.getSelectionModel().getSelectedCells().get(0)).getColumn() == 4)
                    onButtonAcceptClick();
                else if ((tableReceivedRequestsView.getSelectionModel().getSelectedCells().get(0)).getColumn() == 5)
                    onButtonRejectClick();
            }
        }
    }
    
    public void handleSentClickEvent() {
        if (tableSentRequestsView.getSelectionModel() != null) {
            if (tableSentRequestsView.getSelectionModel().getSelectedCells().size() > 0) {
                if ((tableSentRequestsView.getSelectionModel().getSelectedCells().get(0)).getColumn() == 4)
                    onButtonCancelClick();
            }
        }
    }

    public void handleAddFriendClickEvent() {
        if (tableAddFriend.getSelectionModel() != null) {
            if (tableAddFriend.getSelectionModel().getSelectedCells().size() > 0) {
                if ((tableAddFriend.getSelectionModel().getSelectedCells().get(0)).getColumn() == 3) {
                    sendFriendRequest();
                    tableSentRequestsView.setItems(getSentRequests());
                }
            }
        }
    }

    private void sendFriendRequest() {
        User friend = tableAddFriend.getSelectionModel().getSelectedItem().getUser();
        if (friend == null)
            return;
        try {
            service.addFriendship(loggedUser.getEmail(), friend.getEmail());
        } catch (RepoException | DbException e) {
            MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    @Override
    public void update(Object obj) {
        if (obj instanceof FriendshipRequestDbRepo) reloadRequestsTables();
        if (obj instanceof FriendshipDbRepo) reloadAddFriendTable();
    }
}