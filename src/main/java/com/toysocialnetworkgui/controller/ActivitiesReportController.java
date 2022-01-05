package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.MyAlert;
import com.toysocialnetworkgui.utils.UserFriendDTO;
import com.toysocialnetworkgui.utils.UserMessageDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ActivitiesReportController {
    @FXML
    TextField textFieldFilename;
    @FXML
    Button buttonExport;
    @FXML
    ListView<UserMessageDTO> listViewMessages;
    @FXML
    ListView<UserFriendDTO> listViewFriends;

    private Service service;
    private User loggedUser;

    public void initialize(Service service, User loggedUser, LocalDate dateFrom, LocalDate dateUntil) {
        this.service = service;
        this.loggedUser = loggedUser;
        loadFriends(dateFrom, dateUntil);
        loadMessages(dateFrom, dateUntil);
    }

    private void loadFriends(LocalDate dateFrom, LocalDate dateUntil) {
        listViewFriends.getItems()
                .setAll(service.getFriendshipsDTO(loggedUser.getEmail())
                        .stream()
                        .filter(f -> !f.getDate().isAfter(dateUntil) && !f.getDate().isBefore(dateFrom))
                        .toList());
    }

    private void loadMessages(LocalDate dateFrom, LocalDate dateUntil) {
        listViewMessages.getItems()
                .setAll(service.getUserMessageDTOs(loggedUser.getEmail())
                        .stream()
                        .filter(m -> !m.getDate().toLocalDate().isAfter(dateUntil) && !m.getDate().toLocalDate().isBefore(dateFrom))
                        .toList());
    }

    @FXML
    protected void onButtonExportClick(ActionEvent event) {
        if (textFieldFilename.getText().isBlank())
            return;
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Save location");
        File selectedDirectory = chooser.showDialog(((Node)event.getSource()).getScene().getWindow());
        String path = selectedDirectory.getAbsolutePath();
        path = path.concat("\\" + textFieldFilename.getText() + ".pdf");
        try {
            PDDocument document = new PDDocument();
            addContent(document);
            document.save(path);
            document.close();

            MyAlert.StartAlert("Report", "Report expected to pdf!", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            MyAlert.StartAlert("Error", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    private void addContent(PDDocument document) throws IOException {
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.newLineAtOffset(25, 700);

        contentStream.setFont(PDType1Font.TIMES_ROMAN, 18);
        contentStream.showText("User: " + loggedUser);
        contentStream.newLineAtOffset(0, -50);
        contentStream.showText("NEW FRIENDS");
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        contentStream.newLineAtOffset(0, -30);
        List<UserFriendDTO> friends = listViewFriends.getItems();
        for (UserFriendDTO friend : friends) {
            contentStream.showText(friend.toString());
            contentStream.newLineAtOffset(0, -15);
        }

        contentStream.newLineAtOffset(0, -30);
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 20);
        contentStream.showText("MESSAGES RECEIVED");
        contentStream.newLineAtOffset(0, -30);
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        List<UserMessageDTO> messages = listViewMessages.getItems();
        for (UserMessageDTO message : messages) {
            contentStream.showText(message.toString());
            contentStream.newLineAtOffset(0, -15);
        }

        contentStream.endText();
        contentStream.close();
    }
}
