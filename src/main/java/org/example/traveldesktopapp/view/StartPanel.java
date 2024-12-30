package org.example.traveldesktopapp.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import org.example.traveldesktopapp.controller.OfferController;
import org.example.traveldesktopapp.model.Offer;
import org.example.traveldesktopapp.repository.OfferRepository;
import org.example.traveldesktopapp.service.OfferService;

import java.io.File;
import java.util.List;

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
    private TableView<?> table;

    @FXML
    private ImageView trashButton;

    @FXML
    private ImageView uploadButton;

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
        System.out.println("Search clicked!");
    }

    @FXML
    void trashOnMouseClicked(MouseEvent event) {
        System.out.println("Trash clicked!");
    }

    @FXML
    void uploadOnMouseClicked(MouseEvent event) {
        for (File file : files) {
            System.out.println("Importing file: " + file.getName());
            offerController.importOffers(file);
        }
    }
}
