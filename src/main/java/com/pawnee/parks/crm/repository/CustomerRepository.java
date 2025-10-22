package com.pawnee.parks.crm.repository;

import com.pawnee.parks.crm.domain.entity.Customer;
import com.pawnee.parks.crm.domain.enums.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByEmail(String email);

    Page<Customer> findByStatus(CustomerStatus status, Pageable pageable);

    Page<Customer> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Customer> findByStatusAndDeletedAtIsNull(CustomerStatus status, Pageable pageable);

    @Query("""
       SELECT c FROM Customer c
       WHERE c.deletedAt IS NULL
         AND (
              LOWER(c.firstName) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(c.lastName)  LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(c.email)     LIKE LOWER(CONCAT('%', :q, '%'))
         )
       """)
    Page<Customer> searchActive(@org.springframework.data.repository.query.Param("q") String q, Pageable pageable);

}
