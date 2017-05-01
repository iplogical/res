package com.inspirationlogical.receipt.corelib.model.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
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

import com.inspirationlogical.receipt.corelib.utility.Hash;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Builder(toBuilder = true)
//@EqualsAndHashCode(callSuper = true)
@javax.persistence.Table(name = "RESTAURANT")
@NamedQueries({
        @NamedQuery(name = Restaurant.GET_TEST_RESTAURANTS,
                query = "FROM Restaurant r"),
        @NamedQuery(name = Restaurant.GET_ACTIVE_RESTAURANT,
                query = "FROM Restaurant r"),
})
@AttributeOverride(name = "id", column = @Column(name = "RESTAURANT_ID"))
@ValidTables
public @Data class Restaurant extends AbstractEntity {

    public static final String GET_TEST_RESTAURANTS = "Restaurant.GetTestRestaurants";
    public static final String GET_ACTIVE_RESTAURANT = "Restaurant.GetActiveRestaurant";
    private static final Restaurant PROTOTYPE = new Restaurant();

    public static RestaurantBuilder builder() {
        return PROTOTYPE.toBuilder();
    }

    @NotNull
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private Collection<Table> tables = new HashSet<>();

    @NotNull
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private Collection<DailyClosure> dailyClosures = new HashSet<>();

    @NotEmpty
    @NotNull
    private String restaurantName;

    @NotEmpty
    @NotNull
    private String companyName;

    @NotEmpty
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

    private String phoneNumber;

    private String webSite;

    private String socialMediaInfo;

    private String receiptNote;

    private String receiptDisclaimer = Resources.PRINTER.getString("Disclaimer");

    private String receiptGreet = Resources.PRINTER.getString("Greet");

    @Tolerate
    Restaurant() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Restaurant)) {
            return false;
        }


        Restaurant r = (Restaurant) o;
        if (r.hashCode() != hashCode() || tables.size() != r.tables.size()) return false;
        else {
            Set<Table> tableSetLHS = tables.stream().collect(Collectors.toSet());
            Set<Table> tableSetRHS = r.tables.stream().collect(Collectors.toSet());
            return tableSetLHS.equals(tableSetRHS) &&
                    r.restaurantName.equals(restaurantName) &&
                    r.companyName.equals(companyName) &&
                    r.companyTaxPayerId.equals(companyTaxPayerId) &&
                    r.restaurantAddress.equals(restaurantAddress) &&
                    r.companyAddress.equals(companyAddress) &&
                    (r.phoneNumber == null) == (phoneNumber == null) &&
                    (r.phoneNumber == null || r.phoneNumber.equals(phoneNumber)) &&
                    (r.webSite == null) == (webSite == null) &&
                    (r.webSite == null || r.webSite.equals(webSite)) &&
                    (r.socialMediaInfo == null) == (socialMediaInfo == null) &&
                    (r.socialMediaInfo == null || r.socialMediaInfo.equals(socialMediaInfo)) &&
                    (r.receiptNote == null) == (receiptNote == null) &&
                    (r.receiptNote == null || r.receiptNote.equals(receiptNote)) &&
                    (r.receiptDisclaimer == null) == (receiptDisclaimer == null) &&
                    (r.receiptDisclaimer == null || r.receiptDisclaimer.equals(receiptDisclaimer)) &&
                    (r.receiptGreet == null) == (receiptGreet == null) &&
                    (r.receiptGreet == null || r.receiptGreet.equals(receiptGreet));

        }
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = Hash.combine(result,restaurantName);
        result = Hash.combine(result,companyName);
        result = Hash.combine(result,companyTaxPayerId);
        result = Hash.combine(result,restaurantAddress);
        result = Hash.combine(result,companyAddress);
        if (phoneNumber != null)        result = Hash.combine(result,phoneNumber);
        if (webSite != null)            result = Hash.combine(result,webSite);
        if (socialMediaInfo != null)    result = Hash.combine(result,socialMediaInfo);
        if (receiptNote != null)        result = Hash.combine(result,receiptNote);
        if (receiptDisclaimer != null)  result = Hash.combine(result,receiptDisclaimer);
        if (receiptGreet != null)  result = Hash.combine(result,receiptGreet);
        for (Table t : tables) {
            result = Hash.combine(result ,t);
        }
        return result;
    }
}
