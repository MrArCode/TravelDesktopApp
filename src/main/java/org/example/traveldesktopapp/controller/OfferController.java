package org.example.traveldesktopapp.controller;

import org.example.traveldesktopapp.model.Offer;
import org.example.traveldesktopapp.service.OfferService;

import java.io.File;
import java.util.List;

public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    public void importOffers(File file) {
        List<Offer> offers = offerService.readOffersFromFile(file);
        offerService.saveAll(offers);
    }

    public void deleteAllOffer(){
        offerService.deleteAll();
    }

}
