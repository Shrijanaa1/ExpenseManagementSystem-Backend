package com.project.ems_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Budget extends BaseIdEntity{
    @Enumerated(EnumType.STRING)
    private CategoryType category;

    private BigDecimal budgetLimit;

    private BigDecimal remainingAmount;

    private Date startDate;

    private Date endDate;

    private String remark;
}
