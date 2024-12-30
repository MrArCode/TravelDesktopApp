package org.example.traveldesktopapp.view;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class StartPanel {

    @FXML
    private ImageView addButton;

    @FXML
    private ImageView homeButton;

    @FXML
    private ListView<?> listOfFiles;

    @FXML
    private ImageView searchButton;

    @FXML
    private TableView<?> table;

    @FXML
    private ImageView trashButton;

    @FXML
    void addOnMouseClicked(MouseEvent event) {
        System.out.println("tak");

    }

    @FXML
    void homeOnMouseClicked(MouseEvent event) {
        System.out.println("tak");
    }

    @FXML
    void searchOnMouseClicked(MouseEvent event) {
        System.out.println("tak");
    }

    @FXML
    void trashOnMouseClicked(MouseEvent event) {
        System.out.println("tak");
    }



}
