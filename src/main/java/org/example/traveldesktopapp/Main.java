package org.example.traveldesktopapp;

import org.example.traveldesktopapp.config.HibernateUtil;
import org.example.traveldesktopapp.controller.OfferController;
import org.example.traveldesktopapp.model.Offer;
import org.example.traveldesktopapp.repository.OfferRepository;
import org.example.traveldesktopapp.service.OfferService;


import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Main {
    public static void main(String[] args) {
        var offerRepository = new OfferRepository();

        var offerService = new OfferService(offerRepository);

        var offerController = new OfferController(offerService);

//        offerController.deleteAllOffer();

        List<String> map = offerController.getAllOffersFormatted();
        for (String s : map) {
            System.out.println(s);
        }

    }
}
