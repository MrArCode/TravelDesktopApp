package org.example.traveldesktopapp.repository;

import java.util.List;

public interface GenericRepository<T> {
    void save(T entity);
    List<T> findAll();
    T findById(Long id);
    void delete(T entity);
    void deleteAll();
    void resetIdSequence();

}
