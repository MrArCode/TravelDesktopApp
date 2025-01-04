package org.example.traveldesktopapp.repository;

import org.example.traveldesktopapp.model.Offer;

public class OfferRepository extends GenericRepositoryImpl<Offer> {

    public OfferRepository() {
        super(Offer.class);
    }
}
