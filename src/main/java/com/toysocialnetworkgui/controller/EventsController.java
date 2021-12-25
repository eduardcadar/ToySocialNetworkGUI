package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.CONSTANTS;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

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


    public void initialize(Service service, User loggedUser, Stage window) {
        this.service = service;
        this.loggedUser = loggedUser;
        this.window = window;
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
        service.addEvent(name, location, description, startDate, endDate);
        onCancelEventClick(event);
    }
}