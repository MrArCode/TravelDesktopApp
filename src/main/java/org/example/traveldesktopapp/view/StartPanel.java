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
import org.example.traveldesktopapp.repository.OfferRepository;
import org.example.traveldesktopapp.service.OfferService;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartPanel {

    private final OfferRepository offerRepository = new OfferRepository();
    private final OfferService offerService = new OfferService(offerRepository);
    private final OfferController offerController = new OfferController(offerService);

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
    private TableView<OfferToDisplay> table;

    @FXML
    private TableColumn<OfferToDisplay, String> countryColumn;

    @FXML
    private TableColumn<OfferToDisplay, String> dateFromColumn;

    @FXML
    private TableColumn<OfferToDisplay, String> dateToColumn;

    @FXML
    private TableColumn<OfferToDisplay, String> destinationColumn;

    @FXML
    private TableColumn<OfferToDisplay, String> priceColumn;

    @FXML
    private TableColumn<OfferToDisplay, String> currencyColumn;

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
        dateFromColumn.setCellValueFactory(new PropertyValueFactory<>("dateFrom"));
        dateToColumn.setCellValueFactory(new PropertyValueFactory<>("dateTo"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));
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
    void searchOnMouseClicked(MouseEvent event) {
        List<String> rawList = offerController.getAllOffersFormatted();
        ObservableList<OfferToDisplay> offers = FXCollections.observableArrayList();

        Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

        for (String line : rawList) {
            Matcher matcher = datePattern.matcher(line);
            if (matcher.find()) {
                int dateStartIndex = matcher.start();
                String country = line.substring(0, dateStartIndex).trim();

                String remaining = line.substring(dateStartIndex).trim();

                String[] tokens = remaining.split("\\s+");
                if (tokens.length >= 5) {
                    String dateFrom = tokens[0];
                    String dateTo = tokens[1];
                    String destination = tokens[2];

                    StringBuilder priceBuilder = new StringBuilder();
                    for (int i = 3; i < tokens.length - 1; i++) {
                        priceBuilder.append(tokens[i]).append(" ");
                    }
                    String price = priceBuilder.toString().trim();
                    String currency = tokens[tokens.length - 1];

                    OfferToDisplay offer = new OfferToDisplay(
                            country,
                            dateFrom,
                            dateTo,
                            destination,
                            price,
                            currency
                    );
                    offers.add(offer);
                }
            } else {
                System.err.println("Failed to match date in line: " + line);
            }
        }
        table.setItems(offers);
    }


    @FXML
    void trashOnMouseClicked(MouseEvent event) {
        offerController.deleteAllOffer();
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

        listOfFiles.getItems().clear();
        showAlert(Alert.AlertType.INFORMATION,
                "Import Completed",
                "The files have been successfully imported!");
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
