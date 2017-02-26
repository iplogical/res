package com.inspirationlogical.receipt.dummy;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ProductCategoryTest {

    private EntityManager manager;
    private Product product;
    private ProductCategory category;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Before
    public void createObjects() {
        product = new Product();
        product.setLongName("Jack and Coke");
        product.setShortName("Jack and Coke");
        product.setSalePrice(1000);
        product.setQuantityUnit(QunatityUnit.LITER);
        product.setEtalonQuantity(EtalonQuantity.LITER);
        product.setType(ProductType.SELLABLE);

        category = new ProductCategory();
        category.setName("Beverage");
        category.setProduct(product);
        product.setCategory(category);
    }

    @Test
    public void testProductCategoryCreation() {
        assertEquals(1, persistCategoryAndGetList().size());
    }

    @Test
    public void testProductCategoryName() {
        assertEquals("Beverage", persistCategoryAndGetList().get(0).getName());
    }

    @Test
    public void testProductConstraint() {
        category.setProduct(null);
        assertEquals(1, persistCategoryAndGetList().size());
    }

    private List<ProductCategory> persistCategoryAndGetList() {
        persistCategory();
        @SuppressWarnings("unchecked")
        List<ProductCategory> entries = manager.createNamedQuery(ProductCategory.GET_TEST_CATEGORIES).getResultList();
        return entries;
    }

    private void persistCategory() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(category);
        manager.getTransaction().commit();
    }
}
