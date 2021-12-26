package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.Event;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class EventsController {
    private Service service;
    private User loggedUser;
    private Stage window;
    @FXML
    protected Button buttonCreateEvent;

    @FXML
    protected Button buttonCancelEvent;

    @FXML
    protected TextField textFieldEventName;

    @FXML
    protected TextField textFieldEventLocation;
    @FXML
    protected TextField textFieldEventDescription;

    @FXML
    protected DatePicker datePickerEventStart;

    @FXML
    protected DatePicker datePickerEventEnd;

    @FXML
    protected TableView<Event> tableViewEvents;
    @FXML
    protected TableColumn<Event, String> columnOrganizer;
    @FXML
    protected TableColumn<Event, String> columnName;
    @FXML
    protected TableColumn<Event, String> columnLocation;
    @FXML
    protected TableColumn<Event, String> columnDescription;

    @FXML
    protected ListView<Event> listEventsSubscribed;

    @FXML
    protected Button buttonSubscribeEvent;

    public void initialize(Service service, User loggedUser, Stage window) {
        this.service = service;
        this.loggedUser = loggedUser;
        this.window = window;
        initTable();
        initList();
    }

    private void initList() {
        listEventsSubscribed.getItems().setAll(service.getEventsForUser(loggedUser.getEmail()));
    }

    private void initTable() {
        columnOrganizer.setCellValueFactory(new PropertyValueFactory<>("organizer"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        setEventList(getEvents());

    }
    private ObservableList<Event> getEvents() {
        return FXCollections.observableArrayList(service
                .getAllEvents());
    }
    private void setEventList(ObservableList<Event> events) {
        tableViewEvents.setItems(events);
    }

    @FXML
    protected void onCancelEventClick(ActionEvent event) throws IOException {
        System.out.println("cancel");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loggedScene.fxml"));
        Parent root = loader.load();
        LoggedSceneController controller = loader.getController();
        controller.initialize(service, loggedUser, window);
        Scene scene = new Scene(root, CONSTANTS.MAIN_SCREEN_WIDTH, CONSTANTS.MAIN_SCREEN_HEIGHT);
        window.setScene(scene);

    }

    public void onButtonCreateClick(ActionEvent event) throws IOException {
        System.out.println("Create clicked");
        String name = textFieldEventName.getText();
        String location = textFieldEventLocation.getText();
        String description = textFieldEventDescription.getText();
        LocalDate startDate = datePickerEventStart.getValue();
        LocalDate endDate = datePickerEventEnd.getValue();
        String creator = loggedUser.getEmail();
        String category = "test category";
        service.addEvent(name,creator,location,category, description, startDate, endDate);
        // TODO
        //  - do some notify observer here?
        setEventList(getEvents());

    }
@FXML
    protected void onSubscribeClick(ActionEvent actionEvent){
        Event event = tableViewEvents.getSelectionModel().getSelectedItem();
        try{
            if(event != null){
                service.subscribeUserToEvent(event.getId(), loggedUser.getEmail());
                initList();
            }else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please select an event");
                alert.showAndWait();

            }
        }catch (RepoException | DbException e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();


        }

    }
}
