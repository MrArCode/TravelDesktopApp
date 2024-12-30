package org.example.traveldesktopapp.service;

import org.example.traveldesktopapp.model.Offer;
import org.example.traveldesktopapp.repository.OfferRepository;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OfferService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final OfferRepository offerRepository;

    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public List<Offer> readOffersFromFile(String path) {
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


    public void saveAll(List<Offer> offers) {
        for (Offer offer : offers) {
            offerRepository.save(offer);
        }
    }

    public List<Offer> findAll() {
        return offerRepository.findAll();
    }

    public void deleteAll(){
        offerRepository.deleteAll();
    }

    public Set<String> getDistinctLocalizations() {
        return offerRepository.getDistinctLocalizations();
    }


    private Offer parseOffer(String line) {
        String[] fields = line.split("\t");

        if (fields.length != 7) {
            throw new IllegalArgumentException("Line must have exactly 7 fields");
        }

        String language = extractLanguage(fields[0]);
        String localization = createLocale(fields[0]);
        String country = fields[1];
        LocalDate startDate = parseDate(fields[2]);
        LocalDate endDate = parseDate(fields[3]);
        String location = fields[4];
        double price = parsePrice(fields[5], localization);
        String currency = fields[6];

        return new Offer(0, language, localization, country, startDate, endDate, location, price, currency);
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, DATE_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + date, e);
        }
    }

    private String extractLanguage(String localization) {
        int index = localization.indexOf('_');
        if (index == -1) {
            return localization;
        }
        return localization.substring(0, index);
    }

    private String createLocale(String localeString) {
        if (!localeString.contains("_")) {
            return null;
        }
        int index = localeString.indexOf('_');
        return localeString.substring(index + 1);
    }

    private double parsePrice(String priceString, String localization) {
        if (priceString == null || priceString.isEmpty()) {
            throw new IllegalArgumentException("Price cannot be null or empty");
        }

        if (localization == null) {
            return Double.parseDouble(priceString.replace(",", "."));
        }

        try {
            String normalizedPrice = switch (localization.toUpperCase()) {
                case "GB" -> priceString.replace(",", "");
                case "PL" -> priceString.replace(",", ".");
                default -> priceString.replace(",", ".");
            };
            return Double.parseDouble(normalizedPrice);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid price format: " + priceString, e);
        }
    }
}