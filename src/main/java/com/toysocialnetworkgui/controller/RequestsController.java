package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.FriendshipRequest;
import com.toysocialnetworkgui.domain.REQUESTSTATE;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.UserFriendDTO;
import com.toysocialnetworkgui.utils.UserRequestDTO;
import com.toysocialnetworkgui.validator.ValidatorException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RequestsController {
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

    }

    /**
     * Initialize the two tables corresponding to the Requested sent and Requested Received
     *
     */
    private void initializeRequestsList() {
        tableSentColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableSentColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableSentColumnState.setCellValueFactory(new PropertyValueFactory<>("state"));
        tableSentColumnSentDate.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getSendDate().toString()));
        setSentRequestsList(getSentRequests());

        tableReceivedColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableReceivedColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableReceivedColumnState.setCellValueFactory(new PropertyValueFactory<>("state"));
        tableReceivedColumnSentDate.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getSendDate().toString()));

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
     * Reloads both tables  SentRequests and ReceivedRequests
     *
     */
    // TODO
    //  might reload only the affected table  deal with this later
    public void reloadTables(){
        setSentRequestsList(getSentRequests());
        setReceivedRequestsList(getReceivedRequests());
    }
    public void onButtonAcceptClick(ActionEvent event){
        UserRequestDTO requestDTO =  tableReceivedRequestsView.getSelectionModel().getSelectedItem();
        if(requestDTO != null){
            try {
                service.acceptFriendship(requestDTO.getEmail(), loggedUser.getEmail());
                reloadTables();
            }catch (RepoException e){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();

            }
        }
          else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No request selected ");
            alert.showAndWait();

        }


    }
    public void onButtonRejectClick(ActionEvent event){
        UserRequestDTO requestDTO =  tableReceivedRequestsView.getSelectionModel().getSelectedItem();
        if(requestDTO != null) {
            try {
                service.rejectFriendship(requestDTO.getEmail(), loggedUser.getEmail());
                setSentRequestsList(getSentRequests());
                setReceivedRequestsList(getReceivedRequests());
            } catch (RepoException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No request selected ");
            alert.showAndWait();

        }

    }
    /**
     *
     */
    public void onButtonCancelClick(ActionEvent event){
        System.out.println("Cancel click");
        UserRequestDTO dto = tableSentRequestsView.getSelectionModel().getSelectedItem();
        if(dto != null) {
            try {
            service.cancelPendingRequest(loggedUser.getEmail(), dto.getEmail());
            reloadTables();
        }catch(RepoException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No request selected ");
            alert.showAndWait();
        }


    }


}
