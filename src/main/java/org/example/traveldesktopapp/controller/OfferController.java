package org.example.traveldesktopapp.controller;

import org.example.traveldesktopapp.model.Offer;
import org.example.traveldesktopapp.service.OfferService;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    public void importOffers(File file) {
        List<Offer> offers = offerService.readOffersFromFile(file);
        offerService.saveAll(offers);
    }

    public List<Offer> getAllOffers() {
        return offerService.findAll();
    }

    public void deleteAllOffer(){
        offerService.deleteAll();
    }

    public Set<String> getDistinctLocalizations() {
        return offerService.getDistinctLocalizations();
    }

    public List<String> getAllOffersFormatted(){
        return offerService.getAllOffersFormatted();
    }

}
