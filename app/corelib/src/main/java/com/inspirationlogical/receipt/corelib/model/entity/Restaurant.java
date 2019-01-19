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

    @Column(name = "RESTAURANTNAME")
    private String restaurantName;

    @Column(name = "COMPANYNAME")
    private String companyName;

    @Column(name = "COMPANYTAXPAYERID")
    private String companyTaxPayerId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "ZIPCode", column = @Column(name = "RESTAURANTZIPCODE", nullable = false)),
            @AttributeOverride(name = "city", column = @Column(name = "RESTAURANTCITY", nullable = false)),
            @AttributeOverride(name = "street", column = @Column(name = "RESTAURANTSTREET", nullable = false))
    })
    private Address restaurantAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "ZIPCode", column = @Column(name = "COMPANYZIPCODE", nullable = false)),
            @AttributeOverride(name = "city", column = @Column(name = "COMPANYCITY", nullable = false)),
            @AttributeOverride(name = "street", column = @Column(name = "COMPANYSTREET", nullable = false))
    })
    private Address companyAddress;

    @Column(name = "PHONENUMBER")
    private String phoneNumber;

    @Column(name = "WEBSITE")
    private String webSite;

    @Column(name = "SOCIALMEDIAINFO")
    private String socialMediaInfo;

    @Column(name = "RECEIPTNOTE")
    private String receiptNote;

    @Column(name = "RECEIPTDISCLAIMER")
    private String receiptDisclaimer;

    @Column(name = "RECEIPTGREET")
    private String receiptGreet;

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
