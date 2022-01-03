module com.toysocialnetworkgui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.apache.pdfbox;


    opens com.toysocialnetworkgui to javafx.fxml, javafx.base;
    opens com.toysocialnetworkgui.controller to javafx.fxml, javafx.base;
    opens com.toysocialnetworkgui.utils to javafx.fxml, javafx.base;
    opens com.toysocialnetworkgui.service to javafx.fxml, javafx.base;
    opens com.toysocialnetworkgui.domain to javafx.fxml, javafx.base;
    opens com.toysocialnetworkgui.validator to javafx.fxml, javafx.base;
    opens com.toysocialnetworkgui.repository to javafx.fxml, javafx.base;
    opens com.toysocialnetworkgui.repository.db to javafx.fxml, javafx.base;
    exports com.toysocialnetworkgui;
    exports com.toysocialnetworkgui.service;
    exports com.toysocialnetworkgui.controller;
    exports com.toysocialnetworkgui.utils;
    exports com.toysocialnetworkgui.domain;
    exports com.toysocialnetworkgui.validator;
    exports com.toysocialnetworkgui.repository;
    exports com.toysocialnetworkgui.repository.db;
}