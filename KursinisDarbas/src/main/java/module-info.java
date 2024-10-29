module com.kursinis.prif4kursinis {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;


    opens com.kursinis.KursinisDarbas to javafx.fxml;
    exports com.kursinis.KursinisDarbas;
    opens com.kursinis.KursinisDarbas.model to javafx.fxml, org.hibernate.orm.core;
    exports com.kursinis.KursinisDarbas.model;
    opens com.kursinis.KursinisDarbas.fxControllers to javafx.fxml;
    exports com.kursinis.KursinisDarbas.fxControllers to javafx.fxml;
    exports com.kursinis.KursinisDarbas.tableviewparameters;
    opens com.kursinis.KursinisDarbas.tableviewparameters to javafx.fxml;
}