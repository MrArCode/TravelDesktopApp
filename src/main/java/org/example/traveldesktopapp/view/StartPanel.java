package org.example.traveldesktopapp.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.example.traveldesktopapp.controller.OfferController;
import org.example.traveldesktopapp.model.Offer;
import org.example.traveldesktopapp.repository.OfferRepository;
import org.example.traveldesktopapp.service.OfferService;

import java.io.File;
import java.text.NumberFormat;
import java.util.*;

public class StartPanel {

    private final OfferRepository offerRepository = new OfferRepository();
    private final OfferService offerService = new OfferService(offerRepository);
    private final OfferController offerController = new OfferController(offerService);
    private List<Offer> allOffers;

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

    @FXML
    private Menu fileMenu;

    @FXML
    private Menu editMenu;

    @FXML
    private Menu helpMenu;

    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private MenuItem deleteMenuItem;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private TextField searchLabel;

    private Locale currentLocale;

    @FXML
    public void initialize() {
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

        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        dateFromColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        dateToColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        priceColumn.setCellValueFactory(cellData -> {
            double price = cellData.getValue().getPrice();
            String formattedPrice = formatPrice(price, currentLocale);
            return new SimpleStringProperty(formattedPrice);
        });
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));

        allOffers = offerService.findAll();

        setLocale(Locale.ENGLISH);

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void setLocale(Locale locale) {
        this.currentLocale = locale;
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);

        updateMenuBar(bundle);

        countryColumn.setText(bundle.getString("column.country"));
        dateFromColumn.setText(bundle.getString("column.dateFrom"));
        dateToColumn.setText(bundle.getString("column.dateTo"));
        destinationColumn.setText(bundle.getString("column.destination"));
        priceColumn.setText(bundle.getString("column.price"));
        currencyColumn.setText(bundle.getString("column.currency"));

        refreshTableView();
    }

    private List<Offer> translateOffers(List<Offer> offers, Locale locale) {
        List<Offer> localizedOffers = new ArrayList<>();
        for (Offer offer : offers) {
            Offer localizedOffer = new Offer();
            localizedOffer.setCountry(translateCountry(offer.getCountry(), locale));
            localizedOffer.setDestination(translateDestination(offer.getDestination(), locale));
            localizedOffer.setStartDate(offer.getStartDate());
            localizedOffer.setEndDate(offer.getEndDate());
            localizedOffer.setPrice(offer.getPrice());
            localizedOffer.setCurrency(offer.getCurrency());
            localizedOffers.add(localizedOffer);
        }
        return localizedOffers;
    }

    private String translateCountry(String country, Locale locale) {
        for (String isoCode : Locale.getISOCountries()) {
            Locale countryLocale = new Locale("", isoCode);
            if (countryLocale.getDisplayCountry(Locale.ENGLISH).equalsIgnoreCase(country)) {
                return countryLocale.getDisplayCountry(locale);
            }
        }
        return country;
    }

    private String translateDestination(String destination, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        String lowercased = destination.toLowerCase();
        return bundle.containsKey(lowercased) ? bundle.getString(lowercased) : destination;
    }

    private String formatPrice(double price, Locale locale) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        return numberFormat.format(price);
    }

    @FXML
    void searchOnMouseClicked(MouseEvent event) {
        searchLabel.setVisible(!searchLabel.isVisible());
        if (!searchLabel.isVisible()) {
            searchLabel.clear();
            table.getSelectionModel().clearSelection();
        }
    }

    @FXML
    void serachLabelOnKeyTyped(KeyEvent event) {
        String query = searchLabel.getText().toLowerCase();

        table.getSelectionModel().clearSelection();

        if (query.isEmpty()) {
            return;
        }

        ObservableList<Offer> items = table.getItems();

        for (int i = 0; i < items.size(); i++) {
            Offer offer = items.get(i);

            boolean matches =
                    (offer.getCountry() != null && offer.getCountry().toLowerCase().contains(query))
                    || (offer.getDestination() != null && offer.getDestination().toLowerCase().contains(query))
                    || String.valueOf(offer.getPrice()).contains(query)
                    || (offer.getCurrency() != null && offer.getCurrency().toLowerCase().contains(query));

            if (matches) {
                table.getSelectionModel().select(i);
            }
        }
    }

    private void refreshTableView() {
        List<Offer> localizedOffers = translateOffers(allOffers, currentLocale);
        table.setItems(FXCollections.observableArrayList(localizedOffers));
        table.refresh();
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

    @FXML
    void trashOnMouseClicked(MouseEvent event) {
        offerController.deleteAllOffer();
        allOffers.clear();
        table.setItems(FXCollections.observableArrayList());
        showAlert(Alert.AlertType.INFORMATION, "Cleared", "All offers deleted.");
    }

    @FXML
    void uploadOnMouseClicked(MouseEvent event) {
        if (files.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Files", "No files to upload.");
            return;
        }

        for (File file : files) {
            offerController.importOffers(file);
        }
        allOffers = offerService.findAll();
        showAlert(Alert.AlertType.INFORMATION, "Upload Completed", "Files uploaded successfully.");
        files.clear();
        listOfFiles.setItems(FXCollections.observableArrayList(files));
        setLocale(currentLocale);
    }

    @FXML
    void englandOnMouseClicked(MouseEvent event) {
        setLocale(Locale.ENGLISH);
    }

    @FXML
    void germanOnMouseClicked(MouseEvent event) {
        setLocale(Locale.GERMAN);
    }

    @FXML
    void polandOnMouseClicked(MouseEvent event) {
        setLocale(Locale.forLanguageTag("pl"));
    }

    private void updateMenuBar(ResourceBundle bundle) {
        fileMenu.setText(bundle.getString("menu.file"));
        editMenu.setText(bundle.getString("menu.edit"));
        helpMenu.setText(bundle.getString("menu.help"));

        closeMenuItem.setText(bundle.getString("menu.file.close"));
        deleteMenuItem.setText(bundle.getString("menu.edit.delete"));
        aboutMenuItem.setText(bundle.getString("menu.help.about"));
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
