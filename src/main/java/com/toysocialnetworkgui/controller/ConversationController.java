package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.Message;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.observer.Observer;
import com.toysocialnetworkgui.service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;

public class ConversationController implements Observer {
    private Service service;
    private User loggedUser;
    private int idConversation;
    private int pageNumber;
    private int pageSize;

    @FXML
    Button buttonPreviousPage;
    @FXML
    Button buttonNextPage;
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

    public void initialize(Service service, User user, int idConversation) {
        pageNumber = 1;
        pageSize = 3;
        this.service = service;
        this.loggedUser = user;
        this.idConversation = idConversation;
        initializeMessages();
        service.getMessageRepo().addObserver(this);
    }

    @FXML
    protected void onPreviousPageButtonClick() {
        if (pageNumber == 1)
            return;
        pageNumber--;
        reloadMessages();
    }

    @FXML
    protected void onNextPageButtonClick() {
        if (pageNumber == getLastPageNumber())
            return;
        pageNumber++;
        reloadMessages();
    }

    @FXML
    protected void onSendMessageButtonClick() {
        if (textFieldMessage.getLength() == 0)
            return;
        String messageText = textFieldMessage.getText();
        service.sendMessage(idConversation, loggedUser.getEmail(), messageText);
        textFieldMessage.clear();
        pageNumber = getLastPageNumber();
        reloadMessages();
    }

    private int getLastPageNumber() {
        return ((service.getConversation(idConversation).getMessages().size() - 1) / pageSize) + 1;
    }
  
    private ObservableList<Message> getMessages() {
        return FXCollections.observableArrayList(service
                .getConversationPage(idConversation, pageNumber, pageSize).getMessages());
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

    @Override
    public void update(Object obj) {
        reloadMessages();
    }
}
