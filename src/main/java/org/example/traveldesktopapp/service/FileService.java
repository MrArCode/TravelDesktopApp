package org.example.traveldesktopapp.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileService {

    public ObservableList<String> processFiles(List<File> files) {
        ObservableList<String> fileNames = FXCollections.observableArrayList();
        for (File file : files) {
            if (file.getName().endsWith(".txt")) {
                try {
                    Files.readString(file.toPath()); // Możesz dodać dalsze przetwarzanie zawartości pliku
                    fileNames.add(file.getName());
                } catch (IOException e) {
                    System.err.println("Nie udało się odczytać pliku: " + file.getName());
                }
            }
        }
        return fileNames;
    }
}
