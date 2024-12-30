package org.example.traveldesktopapp.controller;

import org.example.traveldesktopapp.model.Offer;
import org.example.traveldesktopapp.service.OfferService;

import java.util.List;

public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    public void importOffers(String filePath) {
        List<Offer> offers = offerService.readOffersFromFile(filePath);
        offerService.saveAll(offers);
    }

    public List<Offer> getAllOffers() {
        return offerService.findAll();
    }

    public void deleteAllOffer(){
        offerService.deleteAll();
    }

}
