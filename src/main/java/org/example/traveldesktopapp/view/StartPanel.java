package org.example.traveldesktopapp.view;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class StartPanel {

    // Komponenty FXML
    @FXML
    private ImageView home_icon;

    @FXML
    private ImageView add_icon;

    @FXML
    private ImageView table_icon;

    @FXML
    private ImageView drag_and_drop_icon;

    @FXML
    private ListView<String> list_of_files;

    @FXML
    private Button load_file_button;

    @FXML
    private TableView<?> table;


    @FXML
    void onLoadFilesClicked(MouseEvent event) {
        list_of_files.getItems().clear();
    }

    // Obsługa kliknięcia ikony "Home"
    @FXML
    void onHomeOnClicked(MouseEvent event) {
        System.out.println("Home icon clicked!");
    }

    // Obsługa kliknięcia ikony "Add"
    @FXML
    void onAddClicked(MouseEvent event) {

        if(table.isVisible()){
            toggleVisibility(table);
        }

        toggleVisibility(drag_and_drop_icon);
        toggleVisibility(list_of_files);
        toggleVisibility(load_file_button);
    }

    @FXML
    void onTableIconClicked(MouseEvent event) {
        if(drag_and_drop_icon.isVisible() || list_of_files.isVisible() || load_file_button.isVisible()){
            toggleVisibility(drag_and_drop_icon);
            toggleVisibility(list_of_files);
            toggleVisibility(load_file_button);
        }

        if (table.isVisible()) {
            fadeOut(table);
        } else {
            fadeInWithSlideAndScale(table);
        }
    }

    // Metoda do zmiany widoczności elementów z animacją
    private void toggleVisibility(Node element) {
        if (element.isVisible()) {
            fadeOut(element);
        } else {
            fadeInWithSlideAndScale(element);
        }
    }

    private void fadeOut(Node element) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), element);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(_ -> element.setVisible(false));
        fadeOut.play();
    }

    private void fadeInWithSlideAndScale(Node element) {
        element.setVisible(true);

        // Fade Transition
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), element);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Translate Transition
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), element);
        slideIn.setFromX(-300);
        slideIn.setToX(0);

        // Scale Transition
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(300), element);
        scaleUp.setFromX(0.5);
        scaleUp.setFromY(0.5);
        scaleUp.setToX(1.0);
        scaleUp.setToY(1.0);

        // Combine All Transitions
        ParallelTransition combined = new ParallelTransition(fadeIn, slideIn, scaleUp);
        combined.play();
    }


    // Inicjalizacja komponentów i logiki
    @FXML
    public void initialize() {
        addHoverEffect(home_icon);
        addHoverEffect(add_icon);
        addHoverEffect(table_icon);
        setupDragAndDrop();
    }

    // Dodanie efektów najechania na ikony
    private void addHoverEffect(ImageView icon) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), icon);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.2);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), icon);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#F9F7F7"));
        shadow.setRadius(10);
        shadow.setSpread(0.2);

        icon.setOnMouseEntered(event -> {
            scaleUp.playFromStart();
            icon.setEffect(shadow);
        });

        icon.setOnMouseExited(event -> {
            scaleDown.playFromStart();
            icon.setEffect(null);
        });
    }

    // Konfiguracja obsługi Drag and Drop
    private void setupDragAndDrop() {
        DropShadow activeShadow = new DropShadow();
        activeShadow.setColor(Color.LIGHTBLUE);
        activeShadow.setRadius(15);

        ScaleTransition scaleUp = createScaleTransition(drag_and_drop_icon, 1.3);
        ScaleTransition scaleDown = createScaleTransition(drag_and_drop_icon, 1.0);

        drag_and_drop_icon.setOnDragOver(event -> {
            if (isValidFile(event)) {
                event.acceptTransferModes(TransferMode.COPY);
                drag_and_drop_icon.setEffect(activeShadow);
                scaleUp.playFromStart();
            }
            event.consume();
        });

        drag_and_drop_icon.setOnDragExited(event -> {
            drag_and_drop_icon.setEffect(null);
            scaleDown.playFromStart();
            event.consume();
        });

        drag_and_drop_icon.setOnDragDropped(this::handleFileDrop);
    }

    private boolean isValidFile(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        return dragboard.hasFiles() && dragboard.getFiles().stream().anyMatch(file -> file.getName().endsWith(".txt"));
    }

    private void handleFileDrop(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            for (File file : dragboard.getFiles()) {
                if (file.getName().endsWith(".txt")) {
                    handleTextFile(file); // Przetwarzaj każdy plik tekstowy
                } else {
                    showAlert("Nieprawidłowy plik", "Pominięto plik: " + file.getName() + ". Akceptowane są tylko pliki tekstowe (.txt).");
                }
            }
        }
        drag_and_drop_icon.setEffect(null);
        event.setDropCompleted(true);
        event.consume();
    }

    // Obsługa pliku tekstowego
    private void handleTextFile(File file) {
        try {
            String content = Files.readString(file.toPath());
            list_of_files.getItems().add(file.getName()); // Dodaj nazwę pliku do listy
        } catch (IOException e) {
            showAlert("Błąd", "Nie udało się odczytać pliku: " + e.getMessage());
        }
    }

    // Tworzenie efektu skalowania
    private ScaleTransition createScaleTransition(Node node, double scale) {
        ScaleTransition transition = new ScaleTransition(Duration.millis(500), node);
        transition.setToX(scale);
        transition.setToY(scale);
        transition.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
        return transition;
    }

    // Wyświetlanie alertu
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
