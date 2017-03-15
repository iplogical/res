package com.inspirationlogical.receipt.model.entity;

import java.util.Collection;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.inspirationlogical.receipt.model.annotations.ValidTables;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@javax.persistence.Table(name = "RESTAURANT")
@NamedQueries({
    @NamedQuery(name = Restaurant.GET_TEST_RESTAURANTS,
            query="FROM Restaurant r"),
    @NamedQuery(name = Restaurant.GET_ACTIVE_RESTAURANT,
            query="FROM Restaurant r"),
})
@AttributeOverride(name = "id", column = @Column(name = "RESTAURANT_ID"))
@ValidTables
public @Data class Restaurant extends AbstractEntity {

    public static final String GET_TEST_RESTAURANTS = "Restaurant.GetTestRestaurants";
    public static final String GET_ACTIVE_RESTAURANT = "Restaurant.GetActiveRestaurant";

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    private Collection<Table> table;

    @NotNull
    private String restaurantName;

    @NotNull
    private String companyName;

    @NotNull
    private String companyTaxPayerId;

    @NotNull
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "ZIPCode", column = @Column(name = "restaurantZIPCode", nullable = false)),
        @AttributeOverride(name = "city", column = @Column(name = "restaurantCity", nullable = false)),
        @AttributeOverride(name = "street", column = @Column(name = "restaurantStreet", nullable = false))
    })
    private Address restaurantAddress;

    @NotNull
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "ZIPCode", column = @Column(name = "companyZIPCode", nullable = false)),
        @AttributeOverride(name = "city", column = @Column(name = "companyCity", nullable = false)),
        @AttributeOverride(name = "street", column = @Column(name = "companyStreet", nullable = false))
    })
    private Address companyAddress;

    @Lob
    private byte[] logo;

    @Tolerate
    Restaurant(){}
}
