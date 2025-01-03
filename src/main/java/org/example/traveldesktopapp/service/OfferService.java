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

    private static final Map<String, String> PL_TO_INTERNAL = Map.of(
            "Japonia", "COUNTRY_JAPAN",
            "Włochy", "COUNTRY_ITALY",
            "Stany Zjednoczone Ameryki", "COUNTRY_USA",
            "jezioro", "DESTINATION_LAKE",
            "morze", "DESTINATION_SEA",
            "góry", "DESTINATION_MOUNTAINS"
    );

    private static final Map<String, String> EN_TO_INTERNAL = Map.of(
            "Japan", "COUNTRY_JAPAN",
            "Italy", "COUNTRY_ITALY",
            "United States", "COUNTRY_USA",
            "lake", "DESTINATION_LAKE",
            "sea", "DESTINATION_SEA",
            "mountains", "DESTINATION_MOUNTAINS"
    );

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

    public Set<String> getDistinctLocalizations() {
        return offerRepository.getDistinctLocalizations();
    }

    public Set<String> getDistinctLanguages() {
        return offerRepository.getDistinctLanguages();
    }

    public Map<Locale, List<Offer>> getAllOffersLocalized() {
        Map<Locale, List<Offer>> offersByLocale = new HashMap<>();
        Set<String> languages = offerRepository.getDistinctLanguages();

        // Przygotuj docelowe lokalizacje
        List<Locale> targetLocales = new ArrayList<>();
        for (String language : languages) {
            targetLocales.add(Locale.forLanguageTag(language));
        }

        List<Offer> offers = findAll();

        for (Locale locale : targetLocales) {
            ResourceBundle bundle;
            try {
                bundle = ResourceBundle.getBundle("messages", locale);
            } catch (MissingResourceException e) {
                System.err.println("Brak pliku zasobów dla locale: " + locale);
                continue;
            }

            List<Offer> localizedOffers = new ArrayList<>();

            for (Offer offer : offers) {
                String translatedCountry = bundle.containsKey(offer.getCountry())
                        ? bundle.getString(offer.getCountry())
                        : offer.getCountry();

                String translatedDestination = bundle.containsKey(offer.getDestination())
                        ? bundle.getString(offer.getDestination())
                        : offer.getDestination();

                Offer localizedOffer = new Offer();
                localizedOffer.setCountry(translatedCountry);
                localizedOffer.setDestination(translatedDestination);
                localizedOffer.setStartDate(offer.getStartDate());
                localizedOffer.setEndDate(offer.getEndDate());
                localizedOffer.setPrice(offer.getPrice());
                localizedOffer.setCurrency(offer.getCurrency());

                localizedOffers.add(localizedOffer);
            }

            offersByLocale.put(locale, localizedOffers);
        }

        return offersByLocale;
    }


    private Offer parseOffer(String line) {
        String[] fields = line.split("\t");

        if (fields.length != 7) {
            throw new IllegalArgumentException("Line must have exactly 7 fields");
        }

        String language = extractLanguage(fields[0]);
        String localization = createLocale(fields[0]);
        String countryRaw = fields[1];
        String startDateRaw = fields[2];
        String endDateRaw = fields[3];
        String destinationRaw = fields[4];
        String priceRaw = fields[5];
        String currency = fields[6];

        LocalDate startDate = parseDate(startDateRaw);
        LocalDate endDate = parseDate(endDateRaw);
        double price = parsePrice(priceRaw, localization);


        String countryKey = normalizeToInternalKey(countryRaw, language);
        String destinationKey = normalizeToInternalKey(destinationRaw, language);

        return new Offer(
                0,
                language,
                localization,
                countryKey,
                startDate,
                endDate,
                destinationKey,
                price,
                currency
        );
    }

    private String normalizeToInternalKey(String rawText, String lang) {
        if (lang.startsWith("pl")) {
            return PL_TO_INTERNAL.getOrDefault(rawText, rawText);
        } else if (lang.startsWith("en")) {
            return EN_TO_INTERNAL.getOrDefault(rawText, rawText);
        }
        return rawText;
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

        priceString = priceString.replace(" ", "");

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
