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

    @Column(name = "NAME")
    private String name;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private PriceModifierType type;

    @Column(name = "REPEATPERIOD")
    @Enumerated(EnumType.STRING)
    private PriceModifierRepeatPeriod repeatPeriod;

    @Column(name = "REPEATPERIODMULTIPLIER")
    private int repeatPeriodMultiplier;

    @Column(name = "STARTDATE")
    private LocalDateTime startDate;

    @Column(name = "ENDDATE")
    private LocalDateTime endDate;

    @Column(name = "DAYOFWEEK")
    private DayOfWeek dayOfWeek;

    @Column(name = "STARTTIME")
    private LocalTime startTime;

    @Column(name = "ENDTIME")
    private LocalTime endTime;

    @Column(name = "QUANTITYLIMIT")
    private int quantityLimit;

    @Column(name = "DISCOUNTPERCENT")
    private double discountPercent;

    @Tolerate
    PriceModifier() {
    }
}
