package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.MyAlert;
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

public class FriendReportController {
    @FXML
    TextField textFieldFilename;
    @FXML
    Label labelTitle;
    @FXML
    Button buttonExport;
    @FXML
    ListView<UserMessageDTO> listViewMessages;

    private Service service;
    private User loggedUser;
    private User otherUser;

    public void initialize(Service service, User loggedUser, User otherUser, LocalDate dateFrom, LocalDate dateUntil) {
        this.service = service;
        this.loggedUser = loggedUser;
        this.otherUser = otherUser;
        labelTitle.setText("Conversation between " + loggedUser + " and " + otherUser);
        loadMessages(dateFrom, dateUntil);
    }

    private void loadMessages(LocalDate dateFrom, LocalDate dateUntil) {
        listViewMessages.getItems()
                .setAll(service.getConversationUserMessageDTOs(List.of(loggedUser.getEmail(), otherUser.getEmail()))
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

            MyAlert.StartAlert("Report", "Report exported to pdf!", Alert.AlertType.INFORMATION);
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
        contentStream.showText("Conversation between " + loggedUser + " and " + otherUser);

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
