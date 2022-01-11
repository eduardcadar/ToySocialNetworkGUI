package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.Event;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.DbException;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.CONSTANTS;
import com.toysocialnetworkgui.utils.MyAlert;
import com.toysocialnetworkgui.validator.ValidatorException;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
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
    protected TextArea textAreaEventDescription;

    @FXML
    protected Text addEventPhotoText;

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

    @FXML
    Rectangle rectangleImageEvent;

    @FXML
    protected TextField textFieldCategory;

    @FXML
    protected Text textAddEventPhoto;

    boolean uploadedPhoto = false;

    String lastEventPicturePath = "";

    public void initialize(Service service, User loggedUser, Stage window) {
        uploadedPhoto = false;
        lastEventPicturePath = "";
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
        return new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                        LocalDate startDate = datePickerEventStart.getValue();
                        if(startDate != null)
                            setDisable(empty || item.compareTo(startDate) < 0);
                        else{
                            setDisable(true);
                        }
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
    protected void onCancelEventClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loggedScene.fxml"));
        Parent root = loader.load();
        LoggedSceneController controller = loader.getController();
        controller.initialize(service, loggedUser, window);
        Scene scene = new Scene(root, CONSTANTS.MAIN_SCREEN_WIDTH, CONSTANTS.MAIN_SCREEN_HEIGHT);
        window.setScene(scene);

    }

    public void onButtonCreateClick(ActionEvent event) throws IOException {
        if(!uploadedPhoto || lastEventPicturePath.isEmpty()){
            MyAlert.StartAlert("Error", "Please upload a photo!", Alert.AlertType.ERROR);
            return;
        }
        String name = textFieldEventName.getText();
        LocalDate startDate = datePickerEventStart.getValue();
        LocalDate endDate = datePickerEventEnd.getValue();
        String location = textFieldEventLocation.getText();
        String category = textFieldCategory.getText();
        String description = textAreaEventDescription.getText();
        String creator = loggedUser.getEmail();
        try{
            service.addEvent(name,creator,location,category, description, startDate, endDate, lastEventPicturePath);
        }
        catch (ValidatorException | DbException | RepoException e){
            MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
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
            } else {
                MyAlert.StartAlert("Error", "Please select an event", Alert.AlertType.WARNING);
            }
        } catch (RepoException | DbException e) {
            MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.WARNING);
        }

    }

    /**
     * Removes the subscription from the selected event which you participate
     * @param ev - ActionEvent
     */
    public void onUnsubscribeButtonClick(ActionEvent ev) {
        Event event = listEventsSubscribed.getSelectionModel().getSelectedItem();
        if(event != null) {
            service.unsubscribeUserFromEvent(event.getId(), loggedUser.getEmail());
            initList();
        } else {
            MyAlert.StartAlert("Error", "You didn't select any event!", Alert.AlertType.WARNING);
        }
    }

    public void onEventImageClick(MouseEvent mouseEvent) {
        textAddEventPhoto.setVisible(false);
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image", "*.jpg", "*.png", "*.jpeg")
        );

        fileChooser.setInitialDirectory(new File(".\\src\\main\\resources\\events"));
        fileChooser.setTitle("Pick an image from this folder, please!");
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile != null) {
            String fullPath = selectedFile.getAbsolutePath();
            String[] args = fullPath.split("resources");
            if (args.length != 2 || !fullPath.contains("events")) {
                MyAlert.StartAlert("Error", "Pick an image from this profile folder, please!", Alert.AlertType.ERROR);
                return;
            }
            String pathToFile = args[1];
            pathToFile = pathToFile.replace("\\", "/");
            rectangleImageEvent.setStroke(Color.web("#862CE4"));
            rectangleImageEvent.setStrokeWidth(2);

            Image im = new Image(pathToFile);
            rectangleImageEvent.setFill(new ImagePattern(im));
            lastEventPicturePath = pathToFile;
            uploadedPhoto = true;
        }
        textAddEventPhoto.setVisible(true);

    }

}
