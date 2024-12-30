package org.example.traveldesktopapp.repository;


import org.example.traveldesktopapp.config.HibernateUtil;
import org.example.traveldesktopapp.model.Offer;
import org.hibernate.Session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OfferRepository extends GenericRepositoryImpl<Offer> {
    public OfferRepository() {
        super(Offer.class);
    }

    public Set<String> findAllLocalizations() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Używamy nazwy klasy "Offer" bezpośrednio
            List<String> localizations = session.createQuery(
                    "SELECT DISTINCT e.localization FROM Offer e", String.class
            ).list();

            // Konwertujemy listę na zbiór, aby usunąć potencjalne duplikaty
            return new HashSet<>(localizations);
        }
    }

}
