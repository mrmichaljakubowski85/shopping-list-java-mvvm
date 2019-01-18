package com.tomtre.android.architecture.shoppinglistmvp.data;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createCheckedProduct;
import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createUncheckedProduct;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings("Guava")
public class ProductsCacheTest {


    private static Product CHECKED_PRODUCT_1 = createCheckedProduct(1);
    private static Product CHECKED_PRODUCT_2 = createCheckedProduct(2);
    private static Product UNCHECKED_PRODUCT_1 = createUncheckedProduct(1);
    private static List<Product> GIVEN_PRODUCTS = productsToSave();

    private ProductsCache productsCache;

    @Before
    public void setUp() {
        productsCache = ProductsCache.getInstance();
    }


    @Test
    public void shouldGetProducts() {
        //given
        saveProductsToCache(GIVEN_PRODUCTS);

        //when
        Collection<Product> products = productsCache.getProducts();

        //then
        assertThat(products, contains(GIVEN_PRODUCTS.toArray()));
    }

    @Test
    public void shouldSaveProducts() {
        //when
        saveProductsToCache(GIVEN_PRODUCTS);

        //then
        Collection<Product> products = productsCache.getProducts();
        assertThat(products, contains(GIVEN_PRODUCTS.toArray()));

    }

    @Test
    public void shouldGetProduct() {
        //given
        saveProductsToCache(GIVEN_PRODUCTS);

        //when
        Optional<Product> productOptional = productsCache.getProduct(CHECKED_PRODUCT_1.getId());

        //then
        assertThat(productOptional.isPresent(), is(true));
        assertThat(productOptional.get(), equalTo(CHECKED_PRODUCT_1));
    }

    @Test
    public void shouldRemoveProduct() {
        //given
        saveProductsToCache(GIVEN_PRODUCTS);

        //when
        productsCache.remove(CHECKED_PRODUCT_1.getId());

        //then
        Collection<Product> products = productsCache.getProducts();
        assertThat(products, contains(CHECKED_PRODUCT_2, UNCHECKED_PRODUCT_1));

    }

    @Test
    public void shouldReturnAbsentOptional_whenNoProductFound() {
        //given
        productsCache.clear();

        //when
        Optional<Product> productOptional = productsCache.getProduct("id");

        //then
        assertThat(productOptional.isPresent(), is(false));

    }

    @Test
    public void shouldClearCache() {
        //given
        List<Product> givenProducts = productsToSave();
        saveProductsToCache(givenProducts);

        //when
        productsCache.clear();

        //then
        Collection<Product> products = productsCache.getProducts();
        assertThat(products, is(empty()));
    }

    @Test
    public void shouldContainsUncheckedProducts_whenRemoveChecked() {
        //given
        List<Product> givenProducts = productsToSave();
        saveProductsToCache(givenProducts);

        //when
        productsCache.removeAllIf(Product::isChecked);

        //then
        Collection<Product> products = productsCache.getProducts();
        assertThat(products, contains(UNCHECKED_PRODUCT_1));
    }

    private static List<Product> productsToSave() {
        return Lists.newArrayList(CHECKED_PRODUCT_1, CHECKED_PRODUCT_2, UNCHECKED_PRODUCT_1);
    }

    private void saveProductsToCache(List<Product> products) {
        for (Product product : products) {
            productsCache.save(product);
        }
    }
}