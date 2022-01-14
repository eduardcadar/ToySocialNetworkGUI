package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.MyAlert;
import com.toysocialnetworkgui.utils.UserFriendDTO;
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

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private AnchorPane rightPane;

    @FXML
    protected PieChart pieChartFriendships;

    @FXML
    protected PieChart pieChartMessages;

    @FXML
    protected Label captionFriendships;

    @FXML
    protected Label captionMessages;

    @FXML
    protected VBox vboxFriendshipChart;

    @FXML
    protected Label notEnoughFriends;

    @FXML
    protected VBox vboxMessagesChart;

    @FXML
    protected Label notEnoughMessages;

    public void initialize(Service service, User loggedUser, LocalDate dateFrom, LocalDate dateUntil, AnchorPane rightPane) {
        this.service = service;
        this.loggedUser = loggedUser;
        this.rightPane = rightPane;
        loadFriends(dateFrom, dateUntil);
        loadMessages(dateFrom, dateUntil);

        populateAllTimeFriendStatistics();
        populateAllTimeMessagesStatistics();
    }

    private void populateAllTimeMessagesStatistics() {
        HashMap<String, Integer> convFrq =  new HashMap<>();
        service.getUserMessageDTOs(loggedUser.getEmail()).forEach(
                        p ->{
                            String sender = p.getSender().getFirstName() + " " + p.getSender().getLastName();
                            if(convFrq.get(sender) == null)
                                convFrq.put(sender, 1);
                            else{
                                int frqOld = convFrq.get(sender);
                                convFrq.put(sender, frqOld + 1);
                            }
                        }
                );
            for(Map.Entry<String, Integer> senderFr : convFrq.entrySet()){
                pieChartMessages.getData().add(new PieChart.Data(senderFr.getKey(), senderFr.getValue() ));
            }
        vboxMessagesChart.setVisible(convFrq.size() != 0);
        notEnoughMessages.setVisible(convFrq.size() == 0);
        for (final PieChart.Data data : pieChartMessages.getData()) {
        data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED,
                e -> {
                    String label = String.valueOf(Math.round(data.getPieValue()));
                    if( Math.round(data.getPieValue()) == 1)
                         label += " message from " + data.getName();
                    else
                        label += " messages from " + data.getName().toLowerCase(Locale.ROOT);
                        captionMessages.setText(label);
                });
        data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED,
                e -> {
                    String label = "Hover slice to get detailed statistics!";
                    captionMessages.setText(label);
                });

        }
        pieChartMessages.setAnimated(true);
        pieChartMessages.setLabelsVisible(true);
        pieChartMessages.setLabelLineLength(10);
        pieChartMessages.setLegendSide(Side.BOTTOM);

    }

    private void populateAllTimeFriendStatistics() {
        HashMap<Month, Integer> monthsFrq = new HashMap<>();
        service.getFriendshipsDTO(loggedUser.getEmail())
                .forEach( p -> {
                    LocalDate date = p.getDate();
                    Month month = date.getMonth();
                    if(monthsFrq.get(month) == null)
                        monthsFrq.put(month,1);
                    else {
                        Integer frq = monthsFrq.get(month);
                        monthsFrq.put(month, frq + 1);
                    }
                });
        for(Map.Entry<Month, Integer> monthFr : monthsFrq.entrySet()){
            pieChartFriendships.getData().add(new PieChart.Data(monthFr.getKey().toString(), monthFr.getValue() ));
        }
        vboxFriendshipChart.setVisible(monthsFrq.size() != 0);
        notEnoughFriends.setVisible(monthsFrq.size() == 0);

        pieChartFriendships.setAnimated(true);
        pieChartFriendships.setLabelsVisible(true);
        pieChartFriendships.setLabelLineLength(10);
        pieChartFriendships.setLegendSide(Side.LEFT);
        for (final PieChart.Data data : pieChartFriendships.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED,
                    e -> {

                        String label = data.getName().toLowerCase() + " : " + Math.round(data.getPieValue());
                        if( Math.round(data.getPieValue()) == 1)
                            label += " friendship";
                        else
                            label += " friendships";
                        captionFriendships.setText(label);
                    });
            data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED,
                    e -> {
                        String label = "Hover slice to get detailed statistics!";
                        captionFriendships.setText(label);
                    });

        }
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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("activitiesReportChooseDate.fxml"));
        Parent root = loader.load();
        ActivitiesReportChooseDateController controller = loader.getController();
        controller.initialize(service, loggedUser, rightPane);
        rightPane.getChildren().setAll(root);
    }

    private void addContent(PDDocument document) throws IOException {
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();

        contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
        contentStream.newLineAtOffset(25, 20);
        contentStream.showText("Report made on: " + LocalDate.now());

        contentStream.newLineAtOffset(0, 700);
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
