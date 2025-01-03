package org.example.traveldesktopapp.service;

import lombok.val;
import org.example.traveldesktopapp.model.Offer;
import org.example.traveldesktopapp.repository.OfferRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OfferService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final OfferRepository offerRepository;

    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public List<Offer> readOffersFromFile(File file) {
        List<Offer> offers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

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
            throw new RuntimeException("Error reading the file: " + file.getAbsolutePath(), e);
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

    public void deleteAll() {
        offerRepository.deleteAll();
    }

    private Offer parseOffer(String line) {
        String[] fields = line.split("\t");

        if (fields.length != 7) {
            throw new IllegalArgumentException("Line must have exactly 7 fields");
        }

        String localeTag = fields[0];
        String countryRaw = fields[1];
        String startDateRaw = fields[2];
        String endDateRaw = fields[3];
        String destinationRaw = fields[4];
        String priceRaw = fields[5];
        String currency = fields[6];

        Locale locale = Locale.forLanguageTag(localeTag.replace('_', '-'));
        LocalDate startDate = parseDate(startDateRaw);
        LocalDate endDate = parseDate(endDateRaw);
        double price = parsePrice(priceRaw, locale);

        String translatedCountry = translateCountry(countryRaw, locale);
        String translatedDestination = translateDestination(destinationRaw, locale);

        return new Offer(
                0,
                localeTag,
                locale.toLanguageTag(),
                translatedCountry,
                startDate,
                endDate,
                translatedDestination,
                price,
                currency
        );
    }

    private String translateCountry(String countryRaw, Locale locale) {
        for (String isoCode : Locale.getISOCountries()) {
            Locale countryLocale = new Locale("", isoCode);
            if (countryLocale.getDisplayCountry(locale).equalsIgnoreCase(countryRaw)) {
                return countryLocale.getDisplayCountry(Locale.ENGLISH);
            }
        }
        return countryRaw;
    }

    private String translateDestination(String destinationRaw, Locale locale) {
        Map<String, String> translations = Map.of(
                "jezioro", "lake",
                "morze", "sea",
                "góry", "mountains"
        );

        String lowercased = destinationRaw.toLowerCase(locale);
        return translations.getOrDefault(lowercased, destinationRaw);
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, DATE_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + date, e);
        }
    }

    private double parsePrice(String priceString, Locale locale) {
        if (priceString == null || priceString.isEmpty()) {
            throw new IllegalArgumentException("Price cannot be null or empty");
        }

        priceString = priceString.replace(" ", "");
        try {
            NumberFormat format = NumberFormat.getInstance(locale);
            return format.parse(priceString).doubleValue();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid price format: " + priceString, e);
        }
    }
}
