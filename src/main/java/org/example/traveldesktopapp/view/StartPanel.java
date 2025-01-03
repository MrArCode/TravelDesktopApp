package org.example.traveldesktopapp.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.example.traveldesktopapp.controller.OfferController;
import org.example.traveldesktopapp.model.Offer;
import org.example.traveldesktopapp.repository.OfferRepository;
import org.example.traveldesktopapp.service.OfferService;

import java.io.File;
import java.util.*;

public class StartPanel {

    private final OfferRepository offerRepository = new OfferRepository();
    private final OfferService offerService = new OfferService(offerRepository);
    private final OfferController offerController = new OfferController(offerService);
    private Map<Locale, List<Offer>> offers;

    @FXML
    private ImageView addButton;

    @FXML
    private ImageView homeButton;

    @FXML
    private ListView<File> listOfFiles;
    private ObservableList<File> files;

    @FXML
    private ImageView searchButton;

    @FXML
    private TableView<Offer> table;

    @FXML
    private TableColumn<Offer, String> countryColumn;

    @FXML
    private TableColumn<Offer, String> dateFromColumn;

    @FXML
    private TableColumn<Offer, String> dateToColumn;

    @FXML
    private TableColumn<Offer, String> destinationColumn;

    @FXML
    private TableColumn<Offer, String> priceColumn;

    @FXML
    private TableColumn<Offer, String> currencyColumn;

    @FXML
    private ImageView trashButton;

    @FXML
    private ImageView uploadButton;

    @FXML
    private ImageView englandButton;

    @FXML
    private ImageView germanButton;

    @FXML
    private ImageView polandButton;

    // Aktualnie wybrany Locale
    private Locale currentLocale;

    @FXML
    public void initialize() {
        // Inicjalizacja listy plików
        files = FXCollections.observableArrayList();
        listOfFiles.setItems(files);

        listOfFiles.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);
                if (empty || file == null) {
                    setText(null);
                } else {
                    setText(file.getName());
                }
            }
        });

        // Ustawienie kolumn tabeli
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        dateFromColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        dateToColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));

        // Pobranie zlokalizowanych ofert
        offers = offerService.getAllOffersLocalized();

        // Ustawienie domyślnej lokalizacji na polski
        Locale polishLocale = Locale.forLanguageTag("pl");
        setLocale(polishLocale);
    }

    @FXML
    void addOnMouseClicked(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        List<File> chosenFiles = fileChooser.showOpenMultipleDialog(null);
        if (chosenFiles != null) {
            files.addAll(chosenFiles);
        }
    }

    @FXML
    void homeOnMouseClicked(MouseEvent event) {
        System.out.println("Home clicked!");
    }

    private void setLocale(Locale locale) {
        this.currentLocale = locale;

        // Ustawienie nagłówków kolumn na podstawie Locale
        switch (locale.getLanguage()) {
            case "pl":
                countryColumn.setText("Kraj");
                dateFromColumn.setText("Data Od");
                dateToColumn.setText("Data Do");
                destinationColumn.setText("Destynacja");
                priceColumn.setText("Cena");
                currencyColumn.setText("Waluta");
                break;
            case "en":
                countryColumn.setText("Country");
                dateFromColumn.setText("Start Date");
                dateToColumn.setText("End Date");
                destinationColumn.setText("Destination");
                priceColumn.setText("Price");
                currencyColumn.setText("Currency");
                break;
            case "de":
                countryColumn.setText("Land");
                dateFromColumn.setText("Startdatum");
                dateToColumn.setText("Enddatum");
                destinationColumn.setText("Ziel");
                priceColumn.setText("Preis");
                currencyColumn.setText("Währung");
                break;
            default:
                countryColumn.setText("Country");
                dateFromColumn.setText("Start Date");
                dateToColumn.setText("End Date");
                destinationColumn.setText("Destination");
                priceColumn.setText("Price");
                currencyColumn.setText("Currency");
                break;
        }

        // Ustaw dane w tabeli
        List<Offer> localizedOffers = offers.get(locale);
        if (localizedOffers == null) {
            showAlert(Alert.AlertType.ERROR,
                    "Brak danych",
                    "Brak ofert dla wybranej lokalizacji.");
            table.setItems(FXCollections.observableArrayList());
            return;
        }
        ObservableList<Offer> observableOffers = FXCollections.observableArrayList(localizedOffers);
        table.setItems(observableOffers);
    }

    @FXML
    void searchOnMouseClicked(MouseEvent event) {
        // Obecnie wyświetla wszystkie oferty dla aktualnie wybranej lokalizacji
        if (currentLocale != null) {
            setLocale(currentLocale);
        } else {
            showAlert(Alert.AlertType.WARNING,
                    "Brak lokalizacji",
                    "Nie wybrano lokalizacji.");
        }
    }

    @FXML
    void trashOnMouseClicked(MouseEvent event) {
        offerController.deleteAllOffer();
        table.setItems(FXCollections.observableArrayList());
        showAlert(Alert.AlertType.INFORMATION,
                "List Deleted",
                "All offers have been erased from the table.");
    }

    @FXML
    void uploadOnMouseClicked(MouseEvent event) {
        if (files.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION,
                    "Empty List",
                    "There are no files in the list.");
            return;
        }

        for (File file : files) {
            offerController.importOffers(file);
        }
        offers = offerService.getAllOffersLocalized();

        listOfFiles.getItems().clear();
        showAlert(Alert.AlertType.INFORMATION,
                "Import Completed",
                "The files have been successfully imported!");

        // Ponowne ustawienie tabeli z aktualną lokalizacją
        if (currentLocale != null) {
            setLocale(currentLocale);
        }
    }

    @FXML
    void englandOnMouseClicked(MouseEvent event) {
        Locale locale = Locale.forLanguageTag("en");
        setLocale(locale);
    }

    @FXML
    void germanOnMouseClicked(MouseEvent event) {
        Locale locale = Locale.forLanguageTag("de");
        setLocale(locale);
    }

    @FXML
    void polandOnMouseClicked(MouseEvent event) {
        Locale locale = Locale.forLanguageTag("pl");
        setLocale(locale);
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
