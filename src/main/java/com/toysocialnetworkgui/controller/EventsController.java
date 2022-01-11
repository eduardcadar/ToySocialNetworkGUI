package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.Event;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.DbException;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.CONSTANTS;
import com.toysocialnetworkgui.utils.MyAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;

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
    @FXML
    protected Button buttonUnsubscribeEvent;

    public void initialize(Service service, User loggedUser, Stage window) {
        this.service = service;
        this.loggedUser = loggedUser;
        this.window = window;
        initTable();
        initList();

        Callback<DatePicker, DateCell> dontLetUserPickEarlyDate =dontLetUserPickEarlyDate();

        datePickerEventEnd.setDayCellFactory(dontLetUserPickEarlyDate);
    }

    /**
     * Callback to not let user enter: i) end date before a start date
     *                                 ii) end date if you haven't picked a start date
     * @return - callback function
     */
    Callback<DatePicker, DateCell> dontLetUserPickEarlyDate(){
        return new Callback<>() {
            @Override
            public DateCell call(final DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                        LocalDate startDate = datePickerEventStart.getValue();
                        if (startDate != null)
                            setDisable(empty || item.compareTo(startDate) < 0);
                        else
                            setDisable(true);
                    }

                };
            }
        };
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
    protected void onCancelEventClick() throws IOException {
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
    protected void onSubscribeClick(){
        Event event = tableViewEvents.getSelectionModel().getSelectedItem();
        try {
            if (event != null){
                service.subscribeUserToEvent(event.getId(), loggedUser.getEmail());
                initList();
            } else
                MyAlert.StartAlert("Error", "Please select an event", Alert.AlertType.WARNING);
        } catch (RepoException | DbException e) {
            MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    /**
     * Removes the subscription from the selected event which you participate
     */
    public void onUnsubscribeButtonClick() {
        Event event = listEventsSubscribed.getSelectionModel().getSelectedItem();
        if (event != null) {
            service.unsubscribeUserFromEvent(event.getId(), loggedUser.getEmail());
            initList();
        } else
            MyAlert.StartAlert("Error", "You didn't select any event!", Alert.AlertType.WARNING);
    }
}
