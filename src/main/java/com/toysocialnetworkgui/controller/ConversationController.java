package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.Message;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.MessageDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConversationController {
    private Service service;
    private User loggedUser, otherUser;

    @FXML
    TextField textFieldMessage;
    @FXML
    Button buttonReplyToSender;
    @FXML
    Button buttonReplyToAll;
    @FXML
    TableView<MessageDTO> tableViewMessages;
    @FXML
    TableColumn<MessageDTO, Integer> tableColumnID;
    @FXML
    TableColumn<MessageDTO, String> tableColumnSender;
    @FXML
    TableColumn<MessageDTO, List<String>> tableColumnReceivers;
    @FXML
    TableColumn<MessageDTO, String> tableColumnMessageRepliedTo;
    @FXML
    TableColumn<MessageDTO, String> tableColumnMessage;
    @FXML
    TableColumn<MessageDTO, LocalDateTime> tableColumnDate;

    @FXML
    public void onReplyToSenderButtonClick() {
        if (tableViewMessages.getSelectionModel().isEmpty() || textFieldMessage.getLength() == 0)
            return;
        String messageText = textFieldMessage.getText();
        Message msgRepliedTo = service.getMessage(tableViewMessages.getSelectionModel().getSelectedItem().getID());
        if (checkMessageSender(msgRepliedTo.getSender())) return;
        service.save(loggedUser.getEmail(), List.of(msgRepliedTo.getSender()), messageText, msgRepliedTo.getID());
        reloadMessages();
        textFieldMessage.clear();
    }

    @FXML
    public void onReplyToAllButtonClick() {
        if (tableViewMessages.getSelectionModel().isEmpty() || textFieldMessage.getLength() == 0)
            return;
        String messageText = textFieldMessage.getText();
        MessageDTO msgRepliedTo = tableViewMessages.getSelectionModel().getSelectedItem();
        if (checkMessageSender(msgRepliedTo.getSender()))
            return;
        List<String> receivers = new ArrayList<>();
        receivers.add(msgRepliedTo.getSender());
        for (String receiver : msgRepliedTo.getReceivers())
            if (!receiver.equals(loggedUser.getEmail()))
                receivers.add(receiver);
        service.save(loggedUser.getEmail(), receivers, messageText, msgRepliedTo.getID());
        reloadMessages();
        textFieldMessage.clear();
    }

    private boolean checkMessageSender(String sender) {
        if (sender.equals(loggedUser.getEmail())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You cannot reply to your message");
            alert.showAndWait();
            return true;
        }
        return false;
    }

    public void initialize(Service service, User user, User otherUser) {
        this.service = service;
        this.loggedUser = user;
        this.otherUser = otherUser;
        initializeMessages();
    }

    private ObservableList<MessageDTO> getMessages() {
        return FXCollections.observableArrayList(service
                .getConversationDTOs(loggedUser.getEmail(), otherUser.getEmail()));
    }

    private void initializeMessages() {
        tableColumnID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        tableColumnSender.setCellValueFactory(new PropertyValueFactory<>("sender"));
        tableColumnReceivers.setCellValueFactory(new PropertyValueFactory<>("receivers"));
        tableColumnMessageRepliedTo.setCellValueFactory(new PropertyValueFactory<>("msgRepliedTo"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        reloadMessages();
    }

    private void reloadMessages() {
        tableViewMessages.setItems(getMessages());
    }
}
