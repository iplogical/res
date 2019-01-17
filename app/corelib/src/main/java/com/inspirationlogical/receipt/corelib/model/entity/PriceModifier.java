package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import javax.persistence.Table;
import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "owner")
@Table(name = "PRICE_MODIFIER")
@AttributeOverride(name = "id", column = @Column(name = "PRICE_MODIFIER_ID"))
public @Data
class PriceModifier extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_CATEGORY_ID")
    private ProductCategory owner;

    private String name;

    @Enumerated(EnumType.STRING)
    private PriceModifierType type;

    @Enumerated(EnumType.STRING)
    private PriceModifierRepeatPeriod repeatPeriod;

    private int repeatPeriodMultiplier;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private int quantityLimit;

    private double discountPercent;

    @Tolerate
    PriceModifier() {
    }
}
