package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.Message;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.db.ConversationDbRepo;
import com.toysocialnetworkgui.repository.db.MessageDbRepo;
import com.toysocialnetworkgui.repository.observer.Observer;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.ConversationDTO;
import com.toysocialnetworkgui.utils.MyAlert;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
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
    Label conversationTitle;

    @FXML
    Button buttonPreviousPage;
    @FXML
    Button buttonNextPage;
    @FXML
    Button buttonRefresh;
    @FXML
    TextField textFieldMessage;
    @FXML
    Button buttonSendMessage;
    @FXML
    TableView<Message> tableViewMessages;
    @FXML
    TableColumn<Message, Integer> tableColumnID;
    @FXML
    TableColumn<Message, Circle> tableColumnSender;
    @FXML
    TableColumn<Message, String> tableColumnMessage;
    @FXML
    TableColumn<Message, String> tableColumnDate;
    @FXML
    ListView<ConversationDTO> listConversations;
    @FXML
    Button buttonCreateConversation;

    public void initialize(Service service, User user, AnchorPane rightPane) {
        tableViewMessages.setPlaceholder(new Label("No messages"));
        listConversations.setPlaceholder(new Label("You have no conversations"));
        this.pageSize = 9;
        this.service = service;
        this.loggedUser = user;
        this.rightPane = rightPane;
        this.idConversation = 0;
        this.exec = (ScheduledExecutorService)rightPane.getParent().getScene().getWindow().getUserData();
        reloadConversationsList();
        initializeMessages();
        service.getConversationRepo().addObserver(this);
        service.getMessageRepo().addObserver(this);
        listConversations.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            stopTask();

            if (listConversations.getSelectionModel().isEmpty())
                return;
            setIdConversation(listConversations.getSelectionModel().getSelectedItem().getId());
            pageNumber = getLastPageNumber();
            setConversationTitle();
            reloadMessages();

            task = exec.scheduleAtFixedRate(() -> {
                if (service.getConversationSize(idConversation) != lastConvSize)
                    pageNumber = getLastPageNumber();
                    reloadMessages();
            }, 10, 10, TimeUnit.SECONDS);
        });
    }

    public void initialize(Service service, User loggedUser, AnchorPane rightPane, String convUser) {
        int convId = service.getConversation(List.of(loggedUser.getEmail(), convUser)).getID();
        initialize(service, loggedUser, rightPane);
        idConversation = convId;
        ConversationDTO conv = listConversations.getItems()
                        .stream().filter(c -> c.getId() == idConversation).toList().get(0);
        listConversations.getSelectionModel().select(conv);
    }

    public void initialize(Service service, User loggedUser, AnchorPane rightPane, Integer convId) {
        initialize(service, loggedUser, rightPane);
        idConversation = convId;
        ConversationDTO conv = listConversations
                .getItems().stream().filter(c -> Objects.equals(c.getId(), convId)).toList().get(0);
        listConversations.getSelectionModel().select(conv);
    }

    private void setConversationTitle() {
        conversationTitle.setText(listConversations.getSelectionModel().getSelectedItem().toString());
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
        controller.initialize(service, loggedUser, rightPane);
        rightPane.getChildren().setAll(root);
    }

    @FXML
    protected void onRefreshButtonClick() {
        if (idConversation == 0) {
            MyAlert.StartAlert("Error", "Choose a conversation", Alert.AlertType.ERROR);
            return;
        }
        this.pageNumber = getLastPageNumber();
        reloadMessages();
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
        Thread t1 = new Thread(() -> service.sendMessage(idConversation, loggedUser.getEmail(), messageText));
        t1.start();
        textFieldMessage.clear();
        try {
            t1.join();
        } catch (InterruptedException e) {
            MyAlert.StartAlert("Error", "Program error", Alert.AlertType.ERROR);
        }
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
        tableColumnSender.setStyle("-fx-alignment: CENTER");
        tableColumnSender.setCellValueFactory(param -> new ObservableValue<>() {
            @Override
            public void addListener(ChangeListener<? super Circle> listener) {}

            @Override
            public void removeListener(ChangeListener<? super Circle> listener) {}

            @Override
            public Circle getValue() {
                Circle imagePlaceHolder = new Circle();
                imagePlaceHolder.setRadius(20);
                imagePlaceHolder.setStroke(Color.web("#862CE4"));
                User u = service.getUser(param.getValue().getSender());
                Image im = new Image(u.getProfilePicturePath());
                imagePlaceHolder.setFill(new ImagePattern(im));
                Tooltip.install(imagePlaceHolder, new Tooltip(u.toString()));
                return imagePlaceHolder;
            }

            @Override
            public void addListener(InvalidationListener listener) {}

            @Override
            public void removeListener(InvalidationListener listener) {}
        });
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        tableColumnDate.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd / HH:mm"))));
    }

    private void reloadMessages() {
        if (idConversation == 0)
            return;

        int lastPage = getLastPageNumber();
        buttonPreviousPage.setVisible(pageNumber != 1);
        buttonNextPage.setVisible(pageNumber != lastPage);

        this.lastConvSize = service.getConversationSize(idConversation);
        tableViewMessages.setItems(getMessages());
    }

    @Override
    public void update(Object obj) {
        if (obj instanceof MessageDbRepo) reloadMessages();
        if (obj instanceof ConversationDbRepo) reloadConversationsList();
    }
}
