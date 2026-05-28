package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.DueType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DueTypeRepository extends JpaRepository<DueType, Long> {

    DueType findByEnumerator(String enumerator);
}
