package org.example.traveldesktopapp.view;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    @FXML
    private ImageView drag_and_drop_icon;

    @FXML
    private ImageView home_icon;

    @FXML
    private ImageView add_icon;

    @FXML
    void onHomeOnClicked(MouseEvent event) {
        System.out.println("Home icon clicked!");
    }

    @FXML
    void onAddClicked(MouseEvent event) {
        if (drag_and_drop_icon.isVisible()) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), drag_and_drop_icon);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(_ -> drag_and_drop_icon.setVisible(false));
            fadeOut.play();
        } else {
            drag_and_drop_icon.setVisible(true);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), drag_and_drop_icon);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
    }

    @FXML
    public void initialize() {
        addHoverEffect(home_icon);
        addHoverEffect(add_icon);
        setupDragAndDrop();
    }

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

    private void setupDragAndDrop() {
        DropShadow activeShadow = new DropShadow();
        activeShadow.setColor(Color.LIGHTBLUE);
        activeShadow.setRadius(15);

        // Tworzenie płynnego powiększenia
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(500), drag_and_drop_icon);
        scaleUp.setToX(1.3);
        scaleUp.setToY(1.3);
        scaleUp.setInterpolator(javafx.animation.Interpolator.EASE_BOTH); // Płynna interpolacja

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(500), drag_and_drop_icon);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        scaleDown.setInterpolator(javafx.animation.Interpolator.EASE_BOTH); // Płynna interpolacja

        // Obsługa przeciągania nad ikoną
        drag_and_drop_icon.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles() && dragboard.getFiles().stream().anyMatch(file -> file.getName().endsWith(".txt"))) {
                event.acceptTransferModes(TransferMode.COPY);
                drag_and_drop_icon.setEffect(activeShadow);
                if (!scaleUp.getStatus().equals(javafx.animation.Animation.Status.RUNNING)) {
                    scaleUp.playFromStart(); // Płynne powiększenie
                }
            }
            event.consume();
        });

        // Obsługa opuszczenia obszaru przeciągania
        drag_and_drop_icon.setOnDragExited(event -> {
            drag_and_drop_icon.setEffect(null);
            if (!scaleDown.getStatus().equals(javafx.animation.Animation.Status.RUNNING)) {
                scaleDown.playFromStart(); // Płynne zmniejszenie
            }
            event.consume();
        });

        // Obsługa upuszczania plików na ikonę
        drag_and_drop_icon.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                File file = dragboard.getFiles().get(0);
                if (file.getName().endsWith(".txt")) {
                    handleTextFile(file);
                } else {
                    showAlert("Nieprawidłowy plik", "Proszę upuścić plik tekstowy (.txt).");
                }
            }
            drag_and_drop_icon.setEffect(null);
            scaleDown.playFromStart();
            event.setDropCompleted(true);
            event.consume();
        });
    }

    private void handleTextFile(File file) {
        try {
            String content = Files.readString(file.toPath());
            System.out.println("Zawartość pliku:\n" + content);
            showAlert("Zawartość pliku", content);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Błąd", "Nie udało się odczytać pliku: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
