package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.Message;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.MessageDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
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
