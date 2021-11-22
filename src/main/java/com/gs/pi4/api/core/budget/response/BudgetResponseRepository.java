package com.gs.pi4.api.core.budget.response;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetResponseRepository extends JpaRepository<BudgetResponse, Long> {

    @Query("SELECT b FROM BudgetResponse b WHERE b.company.id = :companyId")
    List<BudgetResponse> findAllByCompany(@Param("companyId") Long companyId);

    @Query("SELECT b FROM BudgetResponse b WHERE b.id = :budgetId AND b.company.id = :companyId")
    Optional<BudgetResponse> findByBudgetWithCompany(@Param("budgetId") Long budgetId, @Param("companyId") Long companyId);

    @Query("SELECT b FROM BudgetResponse b WHERE b.budgetRequest.company.id = :companyId")
    List<BudgetResponse> findAllByCompanyToBuyer(@Param("companyId") Long companyId);

    @Query("SELECT b FROM BudgetResponse b WHERE b.id = :budgetId AND b.budgetRequest.company.id = :companyId")
    Optional<BudgetResponse> findByIdWithCompanyToBuyer(@Param("budgetId") Long budgetId, @Param("companyId") Long companyId);

}
