package fr.greta.soutenance2.controller;

import fr.greta.soutenance2.modele.VehiculeAnnonce;
import fr.greta.soutenance2.service.ScrapingService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchController {
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
    private TableView<VehiculeAnnonce> list;
    @FXML
    private TableColumn<VehiculeAnnonce, String> modeleColumn;
    @FXML
    private TableColumn<VehiculeAnnonce, String> energieColumn;
    @FXML
    private TableColumn<VehiculeAnnonce, String> kmsColumn;
    @FXML
    private TableColumn<VehiculeAnnonce, String> prixColumn;
    @FXML
    private TableColumn<VehiculeAnnonce, String> anneeColumn;
    @FXML
    private TableColumn<VehiculeAnnonce, String> lieuColumn;
    @FXML
    private TableColumn<VehiculeAnnonce, String> transmissionColumn;
    @FXML
    private TableColumn<VehiculeAnnonce, String> photoColumn;

    private final Map<String, List<String>> modelesParMarque = new HashMap<>();

    private final ObservableList<VehiculeAnnonce> vehicules = FXCollections.observableArrayList();

    /**
     * Cette fonction initialise les valeurs observables dans le tableau.
     */
    @FXML
    public void initialize() {
        // TODO Les marques ne s'affichent pas. Réparer le pb
        // Initialise les listes de modèles pour chaque marque
        modelesParMarque.put("Renault", List.of(
                "Clio", "Megane", "Captur", "Austral"));

        modelesParMarque.put("Peugeot", List.of(
                "208", "308", "2008", "3008"));

        modelesParMarque.put("Citroën", List.of(
                "C3", "C4", "C5 Aircross"));

        modele.getItems().setAll(modelesParMarque.keySet());

        // Ajoute un Listener pour changer la liste des modèles selon la marque sélectionnée
        marque.valueProperty().addListener((obs, oldValue, newValue) -> {
            modele.getItems().clear();
            modele.setValue(null);

            if (newValue != null) {
                modele.getItems().setAll(
                        modelesParMarque.getOrDefault(newValue, List.of())
                );
            }
        });

        // Initialise le tableau dynamique des annonces
        list.setItems(vehicules);
        modeleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModele()));
        energieColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnergie()));
        kmsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKms()));
        prixColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrix()));
        anneeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAnnee()));
        lieuColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLieu()));
        transmissionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTransmission()));
    }

    /**
     * Lance une recherche de véhicules.
     */
    @FXML
    public void onConfirm() {
        String marque = this.marque.getValue();
        String modele = this.modele.getValue();
        String ville = this.ville.getText();
        int choixSite = 1;

        try {
            vehicules.setAll(scrapingService.search(choixSite, marque, modele, ville));
        } catch (IOException e) {
            // TODO Gestion exception
        }
    }

    /**
     * Efface les champs.
     */
    @FXML
    public void onClear() {
        marque.getSelectionModel().clearSelection();
        marque.setValue(null);
        modele.getSelectionModel().clearSelection();
        modele.setValue(null);
        ville.clear();
    }

    /**
     * Sauvegarde les résultats de recherche dans un fichier.
     */
    public void onSaveToFile() {
        // TODO
    }

    /**
     * Envoie les résultats par mail.
     */
    public void onSendAsEmail() {
        // TODO
    }

    /**
     * Sauvegarde les résultats dans la base de données.
     */
    public void onSaveToDatabase() {
        // TODO
    }

    /**
     * Ferme l'application.
     */
    public void onClose() {
        // TODO
    }

    /**
     * Change les paramètres de la base de donnée.
     */
    public void onDatabaseSettings() {
        // TODO
    }

    /**
     * Ouvre le guide d'utilisation.
     */
    public void onGuide() {
        // TODO
    }
}
