package com.gs.pi4.api.core.budget.response;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetResponseItemRepository extends JpaRepository<BudgetResponseItem, Long> {

}
