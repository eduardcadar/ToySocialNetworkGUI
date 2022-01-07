package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.FriendshipRequestDbRepo;
import com.toysocialnetworkgui.repository.observer.Observer;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.MyAlert;
import com.toysocialnetworkgui.utils.UserRequestDTO;
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
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

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
    private TableColumn<UserRequestDTO, String > tableSentColumnState;

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
    private TableColumn<UserRequestDTO, String > tableReceivedColumnState;

    @FXML
    private TableColumn<UserRequestDTO, String> tableReceivedColumnSentDate;


    @FXML
    private TableColumn<UserRequestDTO, ImageView> tableAcceptRequest;
    @FXML
    private TableColumn<UserRequestDTO, ImageView> tableRejectRequest;


    @FXML
    private Button buttonAcceptFriendRequest;

    @FXML
    private Button buttonRejectFriendRequest;

    @FXML
    private Button buttonCancelFriendRequest;

    public void initialize(Service service, User loggedUser) {
        this.service = service;
        this.loggedUser = loggedUser;
        initializeRequestsList();
        tableSentRequestsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableReceivedRequestsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        service.getRequestRepo().addObserver(this);
    }

    /**
     * Initialize the two tables corresponding to the Requested sent and Requested Received
     *
     */
    private void initializeRequestsList() {
        tableSentColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableSentColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableSentColumnState.setCellValueFactory(new PropertyValueFactory<>("state"));
        tableSentColumnSentDate.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getSendDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        tableSentColumnCancel.setStyle("-fx-alignment: CENTER");
        tableSentColumnCancel.setCellValueFactory(param -> new ObservableValue<ImageView>() {
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
            public void addListener(InvalidationListener listener) {

            }

            @Override
            public void removeListener(InvalidationListener listener) {

            }
        });

        setSentRequestsList(getSentRequests());

        tableReceivedColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableReceivedColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableReceivedColumnState.setCellValueFactory(new PropertyValueFactory<>("state"));
        tableReceivedColumnSentDate.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getSendDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        tableAcceptRequest.setStyle("-fx-alignment: CENTER");

        tableAcceptRequest.setCellValueFactory(param -> new ObservableValue<ImageView>() {
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
                imageView.setImage(new Image("images/accept.png"));
                return imageView;
            }

            @Override
            public void addListener(InvalidationListener listener) {

            }

            @Override
            public void removeListener(InvalidationListener listener) {

            }
        });
        // TODO
        //  - EXTRACT THIS SET STYLE INTO CSS FILE
        tableRejectRequest.setStyle("-fx-alignment: CENTER");
        tableRejectRequest.setCellValueFactory(param -> new ObservableValue<ImageView>() {
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
            public void addListener(InvalidationListener listener) {

            }

            @Override
            public void removeListener(InvalidationListener listener) {

            }
        });

        setReceivedRequestsList(getReceivedRequests());

    }

    private void setSentRequestsList(ObservableList<UserRequestDTO> requests) {
        tableSentRequestsView.setItems(requests);
    }

    private void setReceivedRequestsList(ObservableList<UserRequestDTO> requests) {
        tableReceivedRequestsView.setItems(requests);
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
    // TODO
    //  might reload only the affected table  deal with this later
    public void reloadTables(){
        setSentRequestsList(getSentRequests());
        setReceivedRequestsList(getReceivedRequests());
    }

    public void onButtonAcceptClick(){
        UserRequestDTO requestDTO = tableReceivedRequestsView.getSelectionModel().getSelectedItem();
        if (requestDTO != null) {
            try {
                service.acceptFriendship(requestDTO.getEmail(), loggedUser.getEmail());
            } catch (RepoException e){
                MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.WARNING);
            }
        } else {
            MyAlert.StartAlert("Error", "No request selected", Alert.AlertType.WARNING);
        }
    }

    public void onButtonRejectClick(){
        UserRequestDTO requestDTO =  tableReceivedRequestsView.getSelectionModel().getSelectedItem();
        if (requestDTO != null) {
            try {
                service.rejectFriendship(requestDTO.getEmail(), loggedUser.getEmail());
            } catch (RepoException e) {
                MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.WARNING);
            }
        } else {
            MyAlert.StartAlert("Error", "No request selected", Alert.AlertType.WARNING);
        }
    }

    public void onButtonCancelClick(){
        System.out.println("Cancel click");
        UserRequestDTO dto = tableSentRequestsView.getSelectionModel().getSelectedItem();
        if(dto != null) {
            try {
            service.cancelPendingRequest(loggedUser.getEmail(), dto.getEmail());
        } catch(RepoException e) {
                MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.WARNING);
            }
        }
        else {
            MyAlert.StartAlert("Error", "No request selected", Alert.AlertType.WARNING);
        }
    }

    public void handleReceivedClickEvent(MouseEvent mouseEvent) {
        if(tableReceivedRequestsView.getSelectionModel() != null) {
            if(tableReceivedRequestsView.getSelectionModel().getSelectedCells().size() > 0){
                System.out.println("Clicked on " + (tableReceivedRequestsView.getSelectionModel().getSelectedCells().get(0)).getColumn());
                if ((tableReceivedRequestsView.getSelectionModel().getSelectedCells().get(0)).getColumn() == 4) {
                    onButtonAcceptClick();
                } else if ((tableReceivedRequestsView.getSelectionModel().getSelectedCells().get(0)).getColumn() == 5) {
                    onButtonRejectClick();
                }
            }
        }
    }
    
    public void handleSentClickEvent(MouseEvent mouseEvent) {
        if (tableSentRequestsView.getSelectionModel() != null) {
            if (tableSentRequestsView.getSelectionModel().getSelectedCells().size() > 0) {
                System.out.println("Clicked on " + (tableSentRequestsView.getSelectionModel().getSelectedCells().get(0)).getColumn());
                if ((tableSentRequestsView.getSelectionModel().getSelectedCells().get(0)).getColumn() == 4) {
                    onButtonCancelClick();
                }
            }
        }
    }
  
    @Override
    public void update(Object obj) {
        if (obj instanceof FriendshipRequestDbRepo) reloadTables();
    }
}