module fr.greta.soutenance2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.htmlunit;
    requires org.slf4j;
    requires org.apache.commons.logging;
    requires org.apache.logging.log4j;

    opens fr.greta.soutenance2 to javafx.fxml;
    exports fr.greta.soutenance2.application;
    exports fr.greta.soutenance2.controller;
    opens fr.greta.soutenance2.controller to javafx.fxml;
}