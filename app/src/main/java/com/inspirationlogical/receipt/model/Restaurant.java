package com.inspirationlogical.receipt.model;

import java.util.Collection;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.inspirationlogical.receipt.model.annotations.ValidTables;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@javax.persistence.Table(name = "RESTAURANT")
@NamedQueries({
    @NamedQuery(name = Restaurant.GET_TEST_RESTAURANTS,
            query="FROM Restaurant r")
})
@AttributeOverride(name = "id", column = @Column(name = "RESTAURANT_ID"))
@ValidTables
public @Data class Restaurant extends AbstractEntity {

    public static final String GET_TEST_RESTAURANTS = "Restaurant.GetTestRestaurants";

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<Table> table;

    @NotNull
    private String name;

    @NotNull
    private String companyName;

    @NotNull
    private String address;

    @Lob
    private byte[] logo;
}
