package org.example.traveldesktopapp.repository;

import java.util.List;

public interface GenericRepository<T> {
    void save(T entity);
    List<T> findAll();
    void deleteAll();
}
