package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.MyAlert;
import com.toysocialnetworkgui.utils.UserMessageDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendReportController {
    @FXML
    TextField textFieldFilename;
    @FXML
    Label labelTitle;
    @FXML
    Button buttonExport;
    @FXML
    ListView<UserMessageDTO> listViewMessages;

    @FXML
    PieChart pieChartMessages;

    @FXML
    Label caption;

    @FXML
    Label noMessages;
    @FXML
    VBox vboxChart;

    int messagesInInterval;

    private Service service;
    private User loggedUser;
    private User otherUser;
    private AnchorPane rightPane;
    private LocalDate dateFrom;
    private LocalDate dateUntil;

    List<UserMessageDTO> allMessages;
    public void initialize(Service service, User loggedUser, User otherUser, LocalDate dateFrom, LocalDate dateUntil, AnchorPane rightPane) {
        this.service = service;
        this.loggedUser = loggedUser;
        this.otherUser = otherUser;
        this.rightPane = rightPane;
        this.dateFrom = dateFrom;
        this.dateUntil = dateUntil;
        labelTitle.setText("Conversation between " + loggedUser + " and " + otherUser);
        loadMessages(dateFrom, dateUntil);
        populateAllTimeMessages();
    }
    private void populateAllTimeMessages() {

         int allTime =  allMessages.size();
         pieChartMessages.getData().add(new PieChart.Data("In interval ", messagesInInterval ));
         pieChartMessages.getData().add(new PieChart.Data("All time ", allTime ));

        vboxChart.setVisible(allTime != 0);
        noMessages.setVisible(allTime == 0);

        pieChartMessages.setAnimated(true);
        pieChartMessages.setLabelsVisible(true);
        pieChartMessages.setLabelLineLength(10);
        pieChartMessages.setLegendSide(Side.LEFT);
        for (final PieChart.Data data : pieChartMessages.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED,
                    e -> {
                        String label = Math.round(data.getPieValue()) + " " + data.getName();
                        if( Math.round(data.getPieValue()) == 1)
                            label += " message";
                        else
                            label += " messages";
                        caption.setText(label);
                    });
            data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED,
                    e -> {
                        String label = "Hover slice to get detailed statistics!";
                        caption.setText(label);
                    });

        }
    }

    private List<UserMessageDTO> getConvDto(LocalDate dateFrom, LocalDate dateUntil){
        allMessages =  service.getConversationUserMessageDTOs(List.of(loggedUser.getEmail(), otherUser.getEmail()));
        List<UserMessageDTO> result =  allMessages
                .stream()
                .filter(m -> !m.getDate().toLocalDate().isAfter(dateUntil) && !m.getDate().toLocalDate().isBefore(dateFrom))
                .toList();
        messagesInInterval = result.size();
        return result;
    }
    private void loadMessages(LocalDate dateFrom, LocalDate dateUntil) {
        listViewMessages.getItems()
                .setAll(getConvDto(dateFrom, dateUntil));
    }

    @FXML
    protected void onButtonExportClick(ActionEvent event) throws IOException {
        if (textFieldFilename.getText().isBlank()) {
            MyAlert.StartAlert("Error!", "Please choose a file name!", Alert.AlertType.ERROR);
            return;
        }
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Save location");
        File selectedDirectory = chooser.showDialog(((Node)event.getSource()).getScene().getWindow());
        if(selectedDirectory == null){
            MyAlert.StartAlert("Error!", "Please choose a directory!", Alert.AlertType.ERROR);
            return;
        }
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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("friendReportChooseDate.fxml"));
        Parent root = loader.load();
        FriendReportChooseDateController controller = loader.getController();
        controller.initialize(service, loggedUser, rightPane);
        rightPane.getChildren().setAll(root);
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
