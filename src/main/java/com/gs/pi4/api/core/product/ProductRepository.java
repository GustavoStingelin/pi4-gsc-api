package com.gs.pi4.api.core.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.company.id = :companyId")
    Page<Product> findAllByCompany(Pageable pageable, @Param("companyId") Long companyId);

    @Query("SELECT p FROM Product p WHERE p.company.id IN :companyPartnersId")
    List<Product> findAllByCompanyInPartner(@Param("companyPartnersId") List<Long> companyPartnersId);

}
