package com.inspirationlogical.receipt.dummy;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.Max;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@Entity
@Table(name = "PRODUCT")
@NamedQueries({
    @NamedQuery(name = Product.GET_TEST_PRODUCTS,
            query="FROM Product p")
})

public class Product extends AbstractEntity {

    public static final String GET_TEST_PRODUCTS = "Product.GetTestProducts";

    @NotEmpty
    private String category;

    @NotEmpty
    @Length(max = 20, message = "The field has to be less then 20 characters")
    private String shortName;

    @NotEmpty
    private String LongName;

    @Max(10000)
    private int rapidCode;

    @Enumerated(EnumType.STRING)
    private QunatityUnit quantityUnit;

    @Enumerated(EnumType.STRING)
    private EtalonQuantity etalonQuantity;

    private double quantityMultiplier;

    private int purchasePrice;

    private int salePrice;

    @Max(100)
    private int VATLocal;

    @Max(100)
    private int VATTakeAway;

    @Enumerated(EnumType.STRING)
    private ProductType type;

    private int minimumStore;

    private int storeWindow;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return LongName;
    }

    public void setLongName(String longName) {
        LongName = longName;
    }

    public int getRapidCode() {
        return rapidCode;
    }

    public void setRapidCode(int rapidCode) {
        this.rapidCode = rapidCode;
    }

    public QunatityUnit getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(QunatityUnit quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public EtalonQuantity getEtalonQuantity() {
        return etalonQuantity;
    }

    public void setEtalonQuantity(EtalonQuantity etalonQuantity) {
        this.etalonQuantity = etalonQuantity;
    }

    public double getQuantityMultiplier() {
        return quantityMultiplier;
    }

    public void setQuantityMultiplier(double quantityMultiplier) {
        this.quantityMultiplier = quantityMultiplier;
    }

    public int getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(int purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(int salePrice) {
        this.salePrice = salePrice;
    }

    public int getVATLocal() {
        return VATLocal;
    }

    public void setVATLocal(int vATLocal) {
        VATLocal = vATLocal;
    }

    public int getVATTakeAway() {
        return VATTakeAway;
    }

    public void setVATTakeAway(int vATTakeAway) {
        VATTakeAway = vATTakeAway;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public int getMinimumStore() {
        return minimumStore;
    }

    public void setMinimumStore(int minimumStore) {
        this.minimumStore = minimumStore;
    }

    public int getStoreWindow() {
        return storeWindow;
    }

    public void setStoreWindow(int storeWindow) {
        this.storeWindow = storeWindow;
    }
}
