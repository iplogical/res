package com.inspirationlogical.receipt.model.entity;

import java.util.Calendar;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.experimental.Tolerate;
import org.hibernate.validator.constraints.NotEmpty;

import com.inspirationlogical.receipt.model.enums.PriceModifierType;
import com.inspirationlogical.receipt.model.enums.PriceModifierLimitType;
import com.inspirationlogical.receipt.model.enums.PriceModifierRepeatPeriod;
import com.inspirationlogical.receipt.model.enums.PriceModifierStatus;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@Table(name = "PRICE_MODIFIER")
@NamedQueries({
    @NamedQuery(name = PriceModifier.GET_TEST_PRICE_MODIFIERS,
            query="FROM PriceModifier pm")
})
@AttributeOverride(name = "id", column = @Column(name = "PRICE_MODIFIER_ID"))
public @Data class PriceModifier extends AbstractEntity {

    public static final String GET_TEST_PRICE_MODIFIERS = "PriceModifier.GetTestPriceModifiers";
    public static final String DROP_ALL = "PriceModifier.DropAll";

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_CATEGORY_ID")
    private ProductCategory owner;

    @NotEmpty
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PriceModifierType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PriceModifierStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PriceModifierRepeatPeriod period;

    private int periodMultiplier;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar startTime;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar endTime;

    private int quantityLimit;

    private int valueLimit;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PriceModifierLimitType limitType;

    private double discountPercent;

    @Tolerate
    PriceModifier() {}
}
