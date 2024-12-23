module org.example.traveldesktopapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires static lombok;
    requires org.hibernate.orm.core;
    requires java.persistence;
    requires java.naming;
    requires java.sql;

    opens org.example.traveldesktopapp to javafx.fxml;
    opens org.example.traveldesktopapp.model to org.hibernate.orm.core;

    exports org.example.traveldesktopapp;
    exports org.example.traveldesktopapp.view;
    opens org.example.traveldesktopapp.view to javafx.fxml;
}
