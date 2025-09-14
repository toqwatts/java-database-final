package com.project.code.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find customer by email
    Customer findByEmail(String email);

    // Find customer by ID
    Customer findById(long id);

    // Example of a custom query: find customers by name
    List<Customer> findByName(String name);

    // Example of a custom query: find customers by phone number
    List<Customer> findByPhoneNumber(String phoneNumber);
}