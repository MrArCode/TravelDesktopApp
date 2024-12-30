package org.example.traveldesktopapp;

import org.example.traveldesktopapp.config.HibernateUtil;
import org.example.traveldesktopapp.controller.OfferController;
import org.example.traveldesktopapp.model.Offer;
import org.example.traveldesktopapp.repository.OfferRepository;
import org.example.traveldesktopapp.service.OfferService;


import java.util.List;



public class Main {
    public static void main(String[] args) {
        var offerRepository = new OfferRepository();

        var offerService = new OfferService(offerRepository);

        var offerController = new OfferController(offerService);

//        String filePath = "src/main/resources/data.txt";
//        offerController.importOffers(filePath);

//        List<Offer> allOffers = offerController.getAllOffers();
//        allOffers.forEach(System.out::println);
        offerController.showOfferInAllLanguages();

    }
}
