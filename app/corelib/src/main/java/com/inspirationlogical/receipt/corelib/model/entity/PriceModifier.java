package com.inspirationlogical.receipt.corelib.model.entity;

import java.time.LocalDateTime;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@Table(name = "PRICE_MODIFIER")
@NamedQueries({
    @NamedQuery(name = PriceModifier.GET_TEST_PRICE_MODIFIERS,
            query="FROM PriceModifier pm"),
    @NamedQuery(name = PriceModifier.GET_PRICE_MODIFIERS,
            query="FROM PriceModifier pm"),
    @NamedQuery(name = PriceModifier.GET_PRICE_MODIFIERS_BY_PRODUCT_AND_DATES,
            query="FROM PriceModifier pm WHERE pm.owner.id =:owner_id AND pm.startTime <:time AND pm.endTime >:time"),
    @NamedQuery(name = PriceModifier.GET_PRICE_MODIFIERS_BY_NAME,
            query="FROM PriceModifier pm WHERE pm.name =:name")
})
@AttributeOverride(name = "id", column = @Column(name = "PRICE_MODIFIER_ID"))
public @Data class PriceModifier extends AbstractEntity {

    public static final String GET_TEST_PRICE_MODIFIERS = "PriceModifier.GetTestPriceModifiers";
    public static final String GET_PRICE_MODIFIERS = "PriceModifier.GetPriceModifiers";
    public static final String GET_PRICE_MODIFIERS_BY_PRODUCT_AND_DATES = "PriceModifier.GetPriceModifiersByProductsAndDates";
    public static final String GET_PRICE_MODIFIERS_BY_NAME = "PriceModifier.GetPriceModifiersByName";
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
    private PriceModifierRepeatPeriod repeatPeriod;

    private int repeatPeriodMultiplier;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    private int quantityLimit;

    private double discountPercent;

    @Tolerate
    PriceModifier() {}
}
