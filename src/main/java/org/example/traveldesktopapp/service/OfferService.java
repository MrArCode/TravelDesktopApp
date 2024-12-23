package org.example.traveldesktopapp.service;

import org.example.traveldesktopapp.model.Offer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OfferService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static List<Offer> readOffersFromFile(String path) {
        List<Offer> offers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Offer offer = parseOffer(line);
                    offers.add(offer);
                } catch (IllegalArgumentException e) {
                    System.err.println("Skipping invalid line: " + line + " - " + e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error reading the file: " + path, e);
        }

        return offers;
    }

    private static Offer parseOffer(String line) {
        String[] fields = line.split("\t");

        if (fields.length != 7) {
            throw new IllegalArgumentException("Line must have exactly 7 fields");
        }

        String localization = createLocale(fields[0]);
        String country = fields[1];
        LocalDate startDate = parseDate(fields[2]);
        LocalDate endDate = parseDate(fields[3]);
        String location = fields[4];
        double price = parsePrice(fields[5], localization);
        String currency = fields[6];

        return new Offer(0, localization, country, startDate, endDate, location, price, currency);
    }

    private static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, DATE_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + date, e);
        }
    }

    private static String createLocale(String localeString) {
        localeString = localeString.replace("_", "-");
        String[] parts = localeString.split("-");

        if (parts.length == 1) {
            return parts[0];
        }

        return localeString;
    }

    private static double parsePrice(String priceString, String localization) {
        try {
            String normalizedPrice = switch (localization.split("-")[0]) {
                case "pl" -> priceString.replace(",", ".");
                case "en" -> priceString.replace(",", "");
                default -> priceString.replace(",", ".");
            };
            return Double.parseDouble(normalizedPrice);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid price format: " + priceString, e);
        }
    }
}
