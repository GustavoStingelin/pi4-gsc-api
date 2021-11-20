package com.gs.pi4.api.core.budget.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRequestItemRepository extends JpaRepository<BudgetRequestItem, Long> {

}
