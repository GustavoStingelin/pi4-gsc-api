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
    Optional<BudgetResponse> findByBudgetWithCompany(@Param("companyId") Long companyId, @Param("budgetId") Long budgetId);

}