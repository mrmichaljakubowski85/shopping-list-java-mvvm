package com.tomtre.android.architecture.shoppinglistmvp.data.source.local;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createCheckedProduct;
import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createUncheckedProduct;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class ProductsDaoTest {

    private static final Product PRODUCT = createUncheckedProduct();

    private ProductsDatabase productsDatabase;
    private ProductsDao productsDao;

    @Before
    public void setUp() {
        productsDatabase = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getContext(),
                ProductsDatabase.class).build();
        productsDao = productsDatabase.productsDao();
    }

    @After
    public void cleanUp() {
        productsDatabase.close();
    }

    @Test
    public void shouldInsertProduct() {
        //when
        insertProduct();

        //then
        Product product = productsDao.getProductById(PRODUCT.getId());
        assertThat(product, equalTo(PRODUCT));

    }

    @Test
    public void shouldGetProduct() {
        //given
        insertProduct();

        //when
        Product product = productsDao.getProductById(PRODUCT.getId());

        //then
        assertThat(product, equalTo(PRODUCT));
    }

    @Test
    public void shouldReplaceProduct() {
        //given
        insertProduct();
        Product replacementProduct = new Product(
                "Title 2",
                PRODUCT.getDescription(),
                PRODUCT.getQuantity(),
                PRODUCT.getUnit(),
                PRODUCT.isChecked(),
                PRODUCT.getId()
        );

        //when
        productsDao.insertProduct(replacementProduct);

        //then
        Product product = productsDao.getProductById(replacementProduct.getId());
        assertThat(product.getTitle(), equalTo("Title 2"));
    }

    @Test
    public void shouldGetProducts() {
        //given
        insertProduct();

        //when
        List<Product> products = productsDao.getProducts();

        //then
        assertThat(products, hasSize(1));
        assertThat(products, hasItem(is(PRODUCT)));

    }

    @Test
    public void shouldCheckProduct() {
        //given
        Product uncheckedProduct = createUncheckedProduct();
        productsDao.insertProduct(uncheckedProduct);

        //when
        productsDao.updateChecked(uncheckedProduct.getId(), true);

        //then
        Product product = productsDao.getProductById(uncheckedProduct.getId());
        ProductsTestUtils.assertProduct(product,
                uncheckedProduct.getId(),
                uncheckedProduct.getDescription(),
                uncheckedProduct.getQuantity(),
                uncheckedProduct.getUnit(),
                true);
    }

    @Test
    public void shouldDeleteProduct() {
        //given
        insertProduct();

        //when
        productsDao.deleteProductById(PRODUCT.getId());

        //then
        List<Product> products = productsDao.getProducts();
        assertThat(products, is(empty()));
    }

    @Test
    public void shouldDeleteCheckedProducts() {
        //given
        Product checkedProduct = createCheckedProduct();
        productsDao.insertProduct(checkedProduct);
        Product uncheckedProduct = createUncheckedProduct();
        productsDao.insertProduct(uncheckedProduct);

        //when
        productsDao.deleteCheckedProducts();

        //then
        List<Product> products = productsDao.getProducts();
        assertThat(products, hasSize(1));
        assertThat(products.get(0), equalTo(uncheckedProduct));
    }

    private void insertProduct() {
        productsDao.insertProduct(PRODUCT);
    }

}