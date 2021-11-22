package com.gs.pi4.api.core.budget.request;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRequestRepository extends JpaRepository<BudgetRequest, Long> {

    
    @Query("SELECT b FROM BudgetRequest b WHERE b.company.id = :companyId")
    List<BudgetRequest> findAllByCompany(@Param("companyId") Long companyId);

    @Query("SELECT b FROM BudgetRequest b WHERE b.id = :budgetId AND b.company.id = :companyId")
    Optional<BudgetRequest> findByBudgetWithCompany(@Param("companyId") Long companyId, @Param("budgetId") Long budgetId);

    @Query("SELECT b FROM BudgetRequest b JOIN b.itens i WHERE i.product.company.id = :companyId")
    List<BudgetRequest> findAllByCompanyToPartner(@Param("companyId") Long companyId);

    @Query("SELECT b FROM BudgetRequest b JOIN b.itens i WHERE b.id = :budgetId AND i.product.company.id = :companyId")
    Optional<BudgetRequest> findByIdWithCompanyToPartner(@Param("budgetId") Long budgetId, @Param("companyId") Long companyId);

}
