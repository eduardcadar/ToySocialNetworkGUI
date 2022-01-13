package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.Event;
import com.toysocialnetworkgui.domain.EventWantedView;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.DbException;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.CONSTANTS;
import com.toysocialnetworkgui.utils.MyAlert;
import com.toysocialnetworkgui.validator.ValidatorException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
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
import java.time.format.DateTimeFormatter;

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
    protected Text textEventName;
    @FXML
    protected TextField textFieldEventLocation;
    @FXML
    protected Text textEventLocation;
    @FXML
    protected TextArea textCreateEventDescription;
    @FXML
    protected Text textShowEventDescription;

    @FXML
    protected DatePicker datePickerEventStart;
    @FXML
    protected Text textDate;
    @FXML
    protected DatePicker datePickerEventEnd;

    @FXML
    protected Button buttonSubscribeEvent;
    @FXML
    protected Button buttonUnsubscribeEvent;
    @FXML
    Rectangle rectangleEnterImage;
    @FXML
    Rectangle rectangleShowSavedImage;

    @FXML
    protected TextField textFieldCategory;
    @FXML
    protected Text textEventCategory;
    @FXML
    protected Text textAddEventPhoto;

    boolean uploadedPhoto = false;

    String lastEventPicturePath = "";
    @FXML
    AnchorPane createEventPane;
    @FXML
    AnchorPane showEventPane;
    @FXML
    Button buttonVisibleCreate;
    @FXML
    Button buttonSeeEvents;

    @FXML
    Button buttonSeeSubscribed;

    @FXML
    Button buttonPreviousEvent;
    @FXML
    Button buttonNextEvent;
    @FXML
    TextField textFieldSearch;
    @FXML
    Button buttonSearch;

    private int pageNumber;
    private EventWantedView eventWantedView;
    private int currentEventId;
    private String currentPattern;

    public void initialize(Service service, User loggedUser, Stage window, EventWantedView view) {
        uploadedPhoto = false;
        lastEventPicturePath = "";
        this.service = service;
        this.loggedUser = loggedUser;
        this.window = window;
        if (view == EventWantedView.SUBSCRIBED && service.getFilteredUserEventsSize(loggedUser.getEmail(), "") == 0) {
            MyAlert.StartAlert("Alert!", "You didn't subscribe to any event", Alert.AlertType.INFORMATION);
            return;
        }
        textFieldSearch.clear();
        this.currentPattern = "";
        this.eventWantedView = view;
        switch (eventWantedView) {
            case ALL -> initializeAllEvents();
            case SUBSCRIBED -> initializeSubscribedEvents();
            case CREATE -> initializeCreateEvent();
        }
    }

    /**
     * Initialize creeate event, show create event pain and apply constraint to datePickerEvent
     */
    private void initializeCreateEvent() {
        buttonSeeEvents.setEffect(null);
        buttonSeeSubscribed.setEffect(null);
        buttonVisibleCreate.setEffect(new DropShadow());
        showCreateView();
        Callback<DatePicker, DateCell> dontLetUserPickEarlyEnd = dontLetUserPickEarlyDateThanStart();
        datePickerEventEnd.setDayCellFactory(dontLetUserPickEarlyEnd);
        Callback<DatePicker, DateCell> dontLetUserPickEarlyStart = dontLetUserPickEarlyDateThanToday();
        datePickerEventStart.setDayCellFactory(dontLetUserPickEarlyStart);
    }


    /**
     * Initialize subscribed events: Hide create view, Show pane for event printing
     * and load the first event from page
     */
    private void initializeSubscribedEvents() {
        buttonSeeEvents.setEffect(null);
        buttonSeeSubscribed.setEffect(new DropShadow());
        buttonVisibleCreate.setEffect(null);
        hideCreateView();
        pageNumber = 1;
        loadEvent();
    }

    /**
     * Initialize subscribed events: Hide create view, Show event output and load the first event from page
     */
    private void initializeAllEvents() {
        buttonSeeEvents.setEffect(new DropShadow());
        buttonSeeSubscribed.setEffect(null);
        buttonVisibleCreate.setEffect(null);
        hideCreateView();
        pageNumber = 1;
        loadEvent();
    }

    /**
     * Don't let user enter a date before localDate.now()
     * @return - callback
     */
    private Callback<DatePicker, DateCell> dontLetUserPickEarlyDateThanToday() {
        return new Callback<>() {
            @Override
            public DateCell call (final DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                        LocalDate today = LocalDate.now();
                        setDisable(empty || item.compareTo(today) < 0);
                    }

                };
            }
        };

    }

    /**
     * Callback to not let user enter: i) end date before a start date
     *                                 ii) end date if you haven't picked a start date
     * @return - callback function
     */
    Callback<DatePicker, DateCell> dontLetUserPickEarlyDateThanStart() {
        return new Callback<>() {
            @Override
            public DateCell call (final DatePicker param) {
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

    @FXML
    protected void onCancelEventClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loggedScene.fxml"));
        Parent root = loader.load();
        LoggedSceneController controller = loader.getController();
        controller.initialize(service, loggedUser, window);
        Scene scene = new Scene(root, CONSTANTS.MAIN_SCREEN_WIDTH, CONSTANTS.MAIN_SCREEN_HEIGHT);
        window.setScene(scene);
    }

    public void onButtonCreateClick() {
        if (!uploadedPhoto || lastEventPicturePath.isEmpty()) {
            MyAlert.StartAlert("Error", "Please upload a photo!", Alert.AlertType.ERROR);
            return;
        }
        String name = textFieldEventName.getText();
        LocalDate startDate = datePickerEventStart.getValue();
        LocalDate endDate = datePickerEventEnd.getValue();
        String location = textFieldEventLocation.getText();
        String category = textFieldCategory.getText();
        String description = textCreateEventDescription.getText();
        String creator = loggedUser.getEmail();
        try {
            service.addEvent(name,creator,location,category, description, startDate, endDate, lastEventPicturePath);
        }
        catch (ValidatorException | DbException | RepoException e) {
            MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
        initialize(service, loggedUser, window, EventWantedView.ALL);
    }

    /**
     * Subscribes to the event given by AllEventsView
     */
    @FXML
    protected void onSubscribeClick() {
        try {
            service.subscribeUserToEvent(currentEventId, loggedUser.getEmail());
            loadEvent();

        } catch (RepoException | DbException e) {
            MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    /**
     * Unsubscribe from event from ONLY SUBSCRIBED VIEW
     */
    public void onUnsubscribeButtonClick() {
        service.unsubscribeUserFromEvent(currentEventId, loggedUser.getEmail());
        if (pageNumber > 1) pageNumber--;
        loadEvent();

    }

    /**
     * Renders in rectangle the image indicated by path
     * @param rectangle
     * @param path
     */
    public void putImagePathInRectangle(Rectangle rectangle, String path){
        rectangle.setStroke(Color.web("#862CE4"));
        rectangle.setStrokeWidth(2);
        Image im = new Image(path);
        rectangle.setFill(new ImagePattern(im));
    }

    public void onRectangleUploadImageClick() {
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
            putImagePathInRectangle(rectangleEnterImage, pathToFile);
            lastEventPicturePath = pathToFile;
            uploadedPhoto = true;
        } else if (lastEventPicturePath.isEmpty()){
            textAddEventPhoto.setVisible(true);
        }
    }

    public void onButtonSeeAllEvents() {
        initialize(service, loggedUser, window, EventWantedView.ALL);
    }

    public void onButtonSeeSubscribedEvents(){
        initialize(service, loggedUser, window, EventWantedView.SUBSCRIBED);
    }

    /**
     * Loads the first event
     * Knows to take following decisions:
     * 1. Visible or invisible prev/next buttons
     * 2. Load the event depending on if you want subscribed view or all events view
     * 3. Sets subscribe/unsubscribe button depending on you are actually subscribed or not
     */
    public void loadEvent() {
        int lastPage = getLastPageNumber();
        if (lastPage < 1) {
            if (service.getEventsSize() == 0) initialize(service, loggedUser, window, EventWantedView.CREATE);
            else initialize(service, loggedUser, window, EventWantedView.ALL);
            return;
        }
        buttonPreviousEvent.setVisible(pageNumber != 1);
        buttonNextEvent.setVisible(pageNumber != lastPage);
        Event event;
        if (eventWantedView == EventWantedView.SUBSCRIBED)
            event = service.getFilteredUserEventsPage(loggedUser.getEmail(), pageNumber, 1, currentPattern).get(0);
        else if (eventWantedView == EventWantedView.ALL)
            event = service.getFilteredEventsPage(pageNumber, 1, currentPattern).get(0);
        else {
            MyAlert.StartAlert("Error", "Error on loading event", Alert.AlertType.WARNING);
            return;
        }
        currentEventId = event.getId();
        boolean isSubscribedToCurrentEvent = service.isSubscribed(loggedUser.getEmail(), currentEventId);
        buttonUnsubscribeEvent.setVisible(isSubscribedToCurrentEvent);
        buttonSubscribeEvent.setVisible(!isSubscribedToCurrentEvent);
        populateSavedEvent(event);
    }

    @FXML
    protected void onButtonSearchClick() {
        currentPattern = textFieldSearch.getText();
        pageNumber = 1;
        loadEvent();
    }

    @FXML
    protected void onButtonPreviousEventClick() {
        if (pageNumber == 1)
            return;
        pageNumber--;
        loadEvent();
    }

    @FXML
    protected void onButtonNextEventClick() {
        if (pageNumber == getLastPageNumber())
            return;
        pageNumber++;
        loadEvent();
    }

    private int getLastPageNumber() {
        if (eventWantedView == EventWantedView.SUBSCRIBED) return service.getFilteredUserEventsSize(loggedUser.getEmail(), currentPattern);
        if (eventWantedView == EventWantedView.ALL) return service.getFilteredEventsSize(currentPattern);
        return 0;
    }

    public void onButtonVisibleCreate() {
        initialize(service, loggedUser, window, EventWantedView.CREATE);
    }

    /**
     * Place the details of the SAVED event in Anchor pane responsible for showing
     * @param event the event to be shown
     */
    public void populateSavedEvent(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CONSTANTS.DATE_PATTERN);
        textEventName.setText(event.getName());
        textDate.setText(event.getStart().format(formatter) + " - " + event.getEnd().format(formatter));
        textEventLocation.setText(event.getLocation());
        textEventCategory.setText(event.getCategory());
        textShowEventDescription.setText(event.getDescription());
        putImagePathInRectangle(rectangleShowSavedImage, event.getPhotoPath());
    }

    private void hideCreateView() {
        createEventPane.setVisible(false);
        showEventPane.setVisible(true);
     }

    private void showCreateView() {
        buttonPreviousEvent.setVisible(false);
        buttonNextEvent.setVisible(false);
        showEventPane.setVisible(false);
        createEventPane.setVisible(true);
    }
}
