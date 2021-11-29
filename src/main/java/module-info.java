module com.toysocialnetworkgui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.toysocialnetworkgui to javafx.fxml, javafx.base;
    opens com.toysocialnetworkgui.utils to javafx.fxml, javafx.base;
    exports com.toysocialnetworkgui;

}