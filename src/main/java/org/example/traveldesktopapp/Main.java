package org.example.traveldesktopapp;

import lombok.extern.slf4j.Slf4j;
import org.example.traveldesktopapp.config.HibernateUtil;
import org.example.traveldesktopapp.model.Offer;
import org.example.traveldesktopapp.repository.OfferRepository;
import org.example.traveldesktopapp.service.OfferService;


import java.util.List;



public class Main {
    public static void main(String[] args) {
        List<Offer> offers = OfferService.readOffersFromFile("src/main/resources/org/example/traveldesktopapp/data.txt");

        OfferRepository repository = new OfferRepository();
//        repository.resetIdSequence();
//        repository.deleteAll();
        for (Offer offer : offers) repository.save(offer);

        List<Offer> offersFromDb = repository.findAll();
        offersFromDb.forEach(o -> System.out.println("Z bazy: " + o));

        HibernateUtil.shutdown();
    }
}
