package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.DueType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DueTypeRepository extends JpaRepository<DueType, Long> {

    DueType findByEnumerator(String enumerator);
}
