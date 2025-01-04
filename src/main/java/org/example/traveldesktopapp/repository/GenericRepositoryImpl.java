package org.example.traveldesktopapp.repository;


import org.example.traveldesktopapp.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import java.util.List;

public abstract class GenericRepositoryImpl<T> implements GenericRepository<T> {
    private final Class<T> entityClass;

    public GenericRepositoryImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void save(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
        } catch (Exception e){
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            return session.createQuery("FROM " + entityClass.getName(), entityClass).list();
        }
    }

    @Override
    public void deleteAll() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            String hql = "DELETE FROM " + entityClass.getName();
            Query<?> query = session.createQuery(hql);
            query.executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void resetIdSequence() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String sql = "ALTER TABLE " + getTableName() + " ALTER COLUMN id RESTART WITH 1";
            NativeQuery<?> query = session.createNativeQuery(sql);
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    private String getTableName() {
        try {
            javax.persistence.Table table = entityClass.getAnnotation(javax.persistence.Table.class);
            if (table != null && !table.name().isEmpty()) {
                return table.name();
            } else {
                return entityClass.getSimpleName();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return entityClass.getSimpleName();
        }
    }
}
