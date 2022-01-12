package com.toysocialnetworkgui.controller;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.service.Service;
import com.toysocialnetworkgui.utils.MyAlert;
import com.toysocialnetworkgui.utils.UserMessageDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

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
    private AnchorPane rightPane;

    public void initialize(Service service, User loggedUser, User otherUser, LocalDate dateFrom, LocalDate dateUntil, AnchorPane rightPane) {
        this.service = service;
        this.loggedUser = loggedUser;
        this.otherUser = otherUser;
        this.rightPane = rightPane;
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
     ////////////////////////////////////////////////////
        // Create a new font object selecting one of the PDF base fonts
//        PDFont fontPlain = PDType1Font.HELVETICA;
//        PDFont fontBold = PDType1Font.HELVETICA_BOLD;
//        PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
//        PDFont fontMono = PDType1Font.COURIER;
//
//        // Create a document and add a page to it
//    //    PDDocument document = new PDDocument();
//        PDPage page = new PDPage(PDRectangle.A4);
//        // PDRectangle.LETTER and others are also possible
//        PDRectangle rect = page.getMediaBox();
//        // rect can be used to get the page width and height
//        document.addPage(page);
//
//        // Start a new content stream which will "hold" the to be created content
//        PDPageContentStream cos = new PDPageContentStream(document, page);
//
//        //Dummy Table
//        float margin = 50;
//        // starting y position is whole page height subtracted by top and bottom margin
//        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
//        // we want table across whole page width (subtracted by left and right margin ofcourse)
//        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
//
//        boolean drawContent = true;
//        float yStart = yStartNewPage;
//        float bottomMargin = 70;
//        // y position is your coordinate of top left corner of the table
//        float yPosition = 550;
//
//        BaseTable table = new BaseTable(yPosition, yStartNewPage,
//                bottomMargin, tableWidth, margin, document, page, true, drawContent);
//
//        // the parameter is the row height
//        Row<PDPage> headerRow = table.createRow(50);
//        // the first parameter is the cell width
//        Cell<PDPage> cell = headerRow.createCell(100, "Header");
//        cell.setFont(fontBold);
//        cell.setFontSize(20);
//        // vertical alignment
//        cell.setValign(VerticalAlignment.MIDDLE);
//        // border style
//        cell.setTopBorderStyle(new LineStyle(Color.BLACK, 10));
//        table.addHeaderRow(headerRow);
//
//        Row<PDPage> row = table.createRow(20);
//        cell = row.createCell(30, "black left plain");
//        cell.setFontSize(15);
//        cell = row.createCell(70, "black left bold");
//        cell.setFontSize(15);
//        cell.setFont(fontBold);
//
//        row = table.createRow(20);
//        cell = row.createCell(50, "red right mono");
//        cell.setTextColor(Color.RED);
//        cell.setFontSize(15);
//        cell.setFont(fontMono);
//        // horizontal alignment
//        cell.setAlign(HorizontalAlignment.RIGHT);
//        cell.setBottomBorderStyle(new LineStyle(Color.RED, 5));
//        cell = row.createCell(50, "green centered italic");
//        cell.setTextColor(Color.GREEN);
//        cell.setFontSize(15);
//        cell.setFont(fontItalic);
//        cell.setAlign(HorizontalAlignment.CENTER);
//        cell.setBottomBorderStyle(new LineStyle(Color.GREEN, 5));
//
//        row = table.createRow(20);
//        cell = row.createCell(40, "rotated");
//        cell.setFontSize(15);
//        // rotate the text
//        cell.setTextRotated(true);
//        cell.setAlign(HorizontalAlignment.RIGHT);
//        cell.setValign(VerticalAlignment.MIDDLE);
//        // long text that wraps
//        cell = row.createCell(30, "long text long text long text long text long text long text long text");
//        cell.setFontSize(12);
//        // long text that wraps, with more line spacing
//        cell = row.createCell(30, "long text long text long text long text long text long text long text");
//        cell.setFontSize(12);
//        cell.setLineSpacing(2);
//
//        table.draw();
//
//        float tableHeight = table.getHeaderAndDataHeight();
//        System.out.println("tableHeight = "+tableHeight);
//
//        // close the content stream
//        cos.close();
//
//        // Save the results and ensure that the document is properly closed:
//
//        document.close();
    }
}
