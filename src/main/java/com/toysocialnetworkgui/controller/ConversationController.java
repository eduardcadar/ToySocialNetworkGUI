package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.Message;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConversationController {
    private Service service;
    private User loggedUser;
    private int idConversation;

    @FXML
    TextField textFieldMessage;
    @FXML
    Button buttonSendMessage;
    @FXML
    TableView<Message> tableViewMessages;
    @FXML
    TableColumn<Message, Integer> tableColumnID;
    @FXML
    TableColumn<Message, String> tableColumnSender;
    @FXML
    TableColumn<Message, String> tableColumnMessage;
    @FXML
    TableColumn<Message, LocalDateTime> tableColumnDate;

    @FXML
    public void onSendMessageButtonClick() {
        if (textFieldMessage.getLength() == 0)
            return;
        String messageText = textFieldMessage.getText();
        service.sendMessage(idConversation, loggedUser.getEmail(), messageText);
        reloadMessages();
        textFieldMessage.clear();
    }

    public void initialize(Service service, User user, int idConversation) {
        this.service = service;
        this.loggedUser = user;
        this.idConversation = idConversation;
        initializeMessages();
    }

    private ObservableList<Message> getMessages() {
        return FXCollections.observableArrayList(service
                .getConversation(idConversation).getMessages());
    }

    private void initializeMessages() {
        tableColumnID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        tableColumnSender.setCellValueFactory(new PropertyValueFactory<>("sender"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        reloadMessages();
    }

    private void reloadMessages() {
        tableViewMessages.setItems(getMessages());
    }
}
