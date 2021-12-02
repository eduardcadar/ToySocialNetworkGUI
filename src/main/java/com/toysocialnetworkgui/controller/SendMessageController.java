package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class SendMessageController {
    @FXML
    private TextField messageTextField;
    @FXML
    Button sendButton;

    private List<String> receivers;
    private Service service;
    private User sender;

    public void initialize(Service service, List<String> receivers, User sender) {
        setService(service);
        setReceivers(receivers);
        setSender(sender);
    }

    private void setSender(User sender) {
        this.sender = sender;
    }

    private void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    private void setService(Service service) {
        this.service = service;
    }

    @FXML
    protected void onSendButtonClick(ActionEvent event) {
        String msgText = messageTextField.getText();
        service.save(sender.getEmail(), receivers, msgText);
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }
}
