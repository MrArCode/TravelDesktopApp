package org.example.traveldesktopapp.view;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class StartPanel {

    @FXML
    private ImageView home_icon;

    @FXML
    void homeOnClicked(MouseEvent event) {
        System.out.println("Home icon clicked!");
        // Możesz dodać tutaj logikę na kliknięcie
    }

    @FXML
    public void initialize() {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), home_icon);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.2);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), home_icon);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("F9F7F7"));
        shadow.setRadius(10);
        shadow.setSpread(0.2);

        home_icon.setOnMouseEntered(event -> {
            scaleUp.playFromStart();
            home_icon.setEffect(shadow);
        });


        home_icon.setOnMouseExited(_ -> {
            scaleDown.playFromStart();
            home_icon.setEffect(null);
        });
    }




}
