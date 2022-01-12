package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.Friendship;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.MyAlert;
import com.toysocialnetworkgui.utils.UserFriendDTO;
import com.toysocialnetworkgui.utils.UserMessageDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
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
    private AnchorPane rightPane;

    @FXML
    protected PieChart pieChartActivities;

    public void initialize(Service service, User loggedUser, LocalDate dateFrom, LocalDate dateUntil, AnchorPane rightPane) {
        this.service = service;
        this.loggedUser = loggedUser;
        this.rightPane = rightPane;
        loadFriends(dateFrom, dateUntil);
        loadMessages(dateFrom, dateUntil);

        populateAllTimeFriendStatistics();

    }

    private void populateAllTimeFriendStatistics() {
        HashMap<Month, Integer> months_number= new HashMap<>();
        Month[] months = Month.values();
        for(Month m : months)
            months_number.put(m, 0);
        service.getFriendshipsDTO(loggedUser.getEmail())
                .forEach( p -> {
                    LocalDate date = p.getDate();
                    Month month = date.getMonth();
                    Integer frq = months_number.get(month);
                    months_number.put(month, frq + 1);
                });
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("January", months_number.get(Month.JANUARY)),
                        new PieChart.Data("February", months_number.get(Month.FEBRUARY)),
                        new PieChart.Data("March", months_number.get(Month.MARCH)),
                        new PieChart.Data("April", months_number.get(Month.APRIL)),
                        new PieChart.Data("May", months_number.get(Month.MAY)),
                        new PieChart.Data("June", months_number.get(Month.JUNE)),
                        new PieChart.Data("July", months_number.get(Month.JULY)),
                        new PieChart.Data("August", months_number.get(Month.AUGUST)),
                        new PieChart.Data("September", months_number.get(Month.SEPTEMBER)),
                        new PieChart.Data("October", months_number.get(Month.OCTOBER)),
                        new PieChart.Data("November", months_number.get(Month.NOVEMBER)),
                        new PieChart.Data("December", months_number.get(Month.DECEMBER)));
        pieChartActivities.setData(pieChartData);
        pieChartActivities.setLabelLineLength(10);
        pieChartActivities.setLegendSide(Side.LEFT);


        final Label caption = new Label("");
        caption.setTextFill(Color.DARKORANGE);
        caption.setStyle("-fx-font: 24 arial;");

        for (final PieChart.Data data : pieChartActivities.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    e -> {
                        caption.setTranslateX(e.getSceneX());
                        caption.setTranslateY(e.getSceneY());
                        caption.setText(data.getPieValue() + "%");
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
