package fr.greta.soutenance2.controller;

import fr.greta.soutenance2.service.ScrapingService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

public class AccueilController {
    private final ScrapingService scrapingService = new ScrapingService();

    @FXML
    private ComboBox<String> marque;
    @FXML
    private ComboBox<String> modele;
    @FXML
    private TextField ville;
    @FXML
    private CheckBox leboncoinCheckbox;
    @FXML
    private CheckBox lacentraleCheckbox;
    @FXML
    private CheckBox autoscout24Checkbox;
    @FXML
    private Text siteCheckboxMessage;


    @FXML
    public void onConfirm() {
        String marque = "RENAULT";
        String modele = "Clio";
        String ville = "Vannes_56000";
        int choixSite = 1;
        double lon = 0;
        double lat = 0;

        try {
            scrapingService.search(choixSite, marque, modele, ville, lon, lat);
        } catch (IOException e) {
            // TODO Gestion exception
        }
    }

    @FXML
    public void onClear() {
        // TODO
    }

    public void onSaveToFile(ActionEvent actionEvent) {
        // TODO
    }

    public void onSendAsEmail(ActionEvent actionEvent) {
        // TODO
    }

    public void onSaveToDatabase(ActionEvent actionEvent) {
        // TODO
    }

    public void onClose(ActionEvent actionEvent) {
        // TODO
    }

    public void onDatabaseSettings(ActionEvent actionEvent) {
        // TODO
    }

    public void onGuide(ActionEvent actionEvent) {
        // TODO
    }
}
