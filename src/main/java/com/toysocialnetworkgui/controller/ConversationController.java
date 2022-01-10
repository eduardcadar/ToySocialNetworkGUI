package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.Message;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.db.ConversationParticipantDbRepo;
import com.toysocialnetworkgui.repository.db.MessageDbRepo;
import com.toysocialnetworkgui.repository.observer.Observer;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.ConversationDTO;
import com.toysocialnetworkgui.utils.MyAlert;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ConversationController implements Observer {
    private Service service;
    private User loggedUser;
    private int idConversation;
    private int lastConvSize;
    private int pageNumber;
    private int pageSize;
    private ScheduledExecutorService exec;
    private ScheduledFuture<?> task;
    private AnchorPane rightPane;

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
    TableColumn<Message, String> tableColumnDate;
    @FXML
    ListView<ConversationDTO> listConversations;
    @FXML
    Button buttonCreateConversation;

    public void initialize(Service service, User user, AnchorPane rightPane, ScheduledExecutorService exec) {
        tableViewMessages.setPlaceholder(new Label("No messages"));
        listConversations.setPlaceholder(new Label("You have no conversations"));
        this.pageSize = 3;
        this.service = service;
        this.loggedUser = user;
        this.rightPane = rightPane;
        this.idConversation = 0;
        this.exec = exec;
        reloadConversationsList();
        initializeMessages();
        service.getConversationParticipantsRepo().addObserver(this);
        service.getMessageRepo().addObserver(this);
        listConversations.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            stopTask();

            if (listConversations.getSelectionModel().isEmpty())
                return;
            setIdConversation(listConversations.getSelectionModel().getSelectedItem().getId());
            reloadMessages();

            task = exec.scheduleAtFixedRate(() -> {
                if (service.getConversationSize(idConversation) != lastConvSize)
                    reloadMessages();
            }, 10, 10, TimeUnit.SECONDS);
        });
    }

    public void setIdConversation(int idConversation) {
        this.idConversation = idConversation;
    }

    private void stopTask() {
        if (task != null)
            if (!task.isCancelled())
                task.cancel(true);
    }

    private void reloadConversationsList() {
        listConversations.getItems().setAll(service.getUserConversationDTOs(loggedUser.getEmail()));
    }

    @FXML
    protected void onCreateConversationButtonClick() throws IOException {
        stopTask();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("createConversation.fxml"));
        Parent root = loader.load();
        CreateConversationController controller = loader.getController();
        controller.initialize(service, loggedUser, rightPane, exec);
        rightPane.getChildren().setAll(root);
    }

    @FXML
    protected void onPreviousPageButtonClick() {
        if (idConversation == 0) return;
        if (pageNumber == 1)
            return;
        pageNumber--;
        reloadMessages();
    }

    @FXML
    protected void onNextPageButtonClick() {
        if (idConversation == 0) return;
        if (pageNumber == getLastPageNumber())
            return;
        pageNumber++;
        reloadMessages();
    }

    @FXML
    protected void onSendMessageButtonClick() {
        if (textFieldMessage.getLength() == 0)
            return;
        if (idConversation == 0) {
            MyAlert.StartAlert("No conversation selected", "Select a conversation!", Alert.AlertType.WARNING);
            return;
        }
        String messageText = textFieldMessage.getText();
        service.sendMessage(idConversation, loggedUser.getEmail(), messageText);
        textFieldMessage.clear();
        reloadMessages();
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
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
        tableColumnDate.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd / HH:mm"))));
    }

    private void reloadMessages() {
        pageNumber = getLastPageNumber();
        this.lastConvSize = service.getConversationSize(idConversation);
        tableViewMessages.setItems(getMessages());
    }

    @Override
    public void update(Object obj) {
        if (obj instanceof MessageDbRepo) reloadMessages();
        if (obj instanceof ConversationParticipantDbRepo) reloadConversationsList();
    }
}
