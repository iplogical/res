package com.inspirationlogical.receipt.corelib.model.entity;

import java.util.Collection;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.inspirationlogical.receipt.corelib.model.annotations.ValidTables;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Entity
@Builder
//@EqualsAndHashCode(callSuper = true)
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

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private Collection<Table> tables;

    @NotNull
    private String restaurantName;

    @NotNull
    private String companyName;

    @NotNull
    private String companyTaxPayerId;

    private String receiptNote;

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

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(!(o instanceof Restaurant)) {
            return false;
        }
        Restaurant r = (Restaurant) o;
        return r.restaurantName.equals(restaurantName) &&
                r.companyName.equals(companyName) &&
                r.companyTaxPayerId.equals(companyTaxPayerId) &&
                r.restaurantAddress.equals(restaurantAddress) &&
                r.companyAddress.equals(companyAddress) &&
                r.restaurantName.equals(restaurantName) &&
                r.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result * 31 + restaurantName.hashCode();
        result = result * 31 + companyName.hashCode();
        result = result * 31 + companyTaxPayerId.hashCode();
        result = result * 31 + restaurantAddress.hashCode();
        result = result * 31 + companyAddress.hashCode();
        for(Table t : tables) {
            result = result * 31 + t.hashCode();
        }
        return result;
    }
}
