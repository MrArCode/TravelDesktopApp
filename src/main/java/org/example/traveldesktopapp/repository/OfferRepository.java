package org.example.traveldesktopapp.repository;

import org.example.traveldesktopapp.model.Offer;
import org.example.traveldesktopapp.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OfferRepository extends GenericRepositoryImpl<Offer> {

    public OfferRepository() {
        super(Offer.class);
    }

    public Set<String> getDistinctLocalizations() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<String> localizations = session.createQuery(
                    "SELECT DISTINCT e.localization FROM Offer e WHERE e.localization IS NOT NULL",
                    String.class
            ).list();

            return new HashSet<>(localizations);
        }
    }

    public Set<String> getDistinctLanguages() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<String> localizations = session.createQuery(
                    "SELECT DISTINCT e.language FROM Offer e WHERE e.language IS NOT NULL",
                    String.class
            ).list();

            return new HashSet<>(localizations);
        }
    }

    public List<Offer> getAllOffers() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Offer> offers = null;
        try {
            Query<Offer> query = session.createQuery("FROM Offer", Offer.class);
            offers = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return offers;
    }
}
