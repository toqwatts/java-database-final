package com.project.code.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    // Retrieve a store by its ID
    Store findById(long id);

    // Retrieve stores whose name contains a given substring
    @Query("SELECT s FROM Store s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    List<Store> findBySubName(String pname);
}