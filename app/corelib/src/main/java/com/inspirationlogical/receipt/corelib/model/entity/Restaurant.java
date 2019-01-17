package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;

@Entity
@Builder
@javax.persistence.Table(name = "RESTAURANT")
@AttributeOverride(name = "id", column = @Column(name = "RESTAURANT_ID"))
public @Data
class Restaurant extends AbstractEntity {

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private Collection<Table> tables = new HashSet<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private Collection<DailyClosure> dailyClosures = new HashSet<>();

    private String restaurantName;

    private String companyName;

    private String companyTaxPayerId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "ZIPCode", column = @Column(name = "restaurantZIPCode", nullable = false)),
            @AttributeOverride(name = "city", column = @Column(name = "restaurantCity", nullable = false)),
            @AttributeOverride(name = "street", column = @Column(name = "restaurantStreet", nullable = false))
    })
    private Address restaurantAddress;

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
    public String toString() {
        return "Restaurant{" +
                "restaurantName='" + restaurantName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyTaxPayerId='" + companyTaxPayerId + '\'' +
                ", restaurantAddress=" + restaurantAddress +
                ", companyAddress=" + companyAddress +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", webSite='" + webSite + '\'' +
                ", socialMediaInfo='" + socialMediaInfo + '\'' +
                ", receiptNote='" + receiptNote + '\'' +
                ", receiptDisclaimer='" + receiptDisclaimer + '\'' +
                ", receiptGreet='" + receiptGreet + '\'' +
                '}';
    }
}
