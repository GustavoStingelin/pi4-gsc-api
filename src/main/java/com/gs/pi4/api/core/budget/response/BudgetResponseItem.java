package com.gs.pi4.api.core.budget.response;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.gs.pi4.api.core.budget.request.BudgetRequestItem;
import com.gs.pi4.api.core.product.Product;
import com.gs.pi4.api.core.user.User;

import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "public", name = "budget_response_item")
@Where(clause = "deleted_at is null")
@Getter @Setter @EqualsAndHashCode @Builder @NoArgsConstructor @AllArgsConstructor
public class BudgetResponseItem implements Serializable  {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "quantity", nullable = false)
    private Float quantity;

    @Column(name = "unit_price", nullable = false)
    private Float unitPrice;

    @ManyToOne(fetch = FetchType.EAGER, optional=false)
    @JoinColumn(name = "budget_response_id")
    private BudgetResponse budgetResponse;

    @ManyToOne(fetch = FetchType.EAGER, optional=false)
    @JoinColumn(name = "reference_budget_request_item_id")
    private BudgetRequestItem reference;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;


    //user log
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "changed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date changedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;
    
    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;
    

}
