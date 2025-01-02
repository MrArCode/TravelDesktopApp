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

    public List<String> getAllOffersFormatted() {
        List<String> formattedOffers = new ArrayList<>();
        List<Locale> targetLocales = new ArrayList<>();
        Set<String> languages = offerRepository.getDistinctLanguages();

        // Prepare target locales
        for (String language : languages) {
            targetLocales.add(Locale.of(language));
        }

        List<Offer> offers = findAll();

        // Process offers for each locale
        for (Locale locale : targetLocales) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
            NumberFormat nf = NumberFormat.getInstance(locale);

            for (Offer offer : offers) {
                String keyCountry = offer.getCountry();
                String keyDestination = offer.getDestination();

                String displayedCountry = bundle.containsKey(keyCountry)
                        ? bundle.getString(keyCountry)
                        : keyCountry;

                String displayedDestination = bundle.containsKey(keyDestination)
                        ? bundle.getString(keyDestination)
                        : keyDestination;

                String formattedPrice = nf.format(offer.getPrice());

                String line = displayedCountry + " "
                              + offer.getStartDate() + " "
                              + offer.getEndDate() + " "
                              + displayedDestination + " "
                              + formattedPrice + " "
                              + offer.getCurrency();

                formattedOffers.add(line);
            }
        }

        return formattedOffers;
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
