package com.tomtre.android.architecture.shoppinglistmvp.data.source.local;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.remote.ProductsRemoteDataSource;
import com.tomtre.android.architecture.shoppinglistmvp.util.AppExecutors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.Executor;

import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.assertProduct;
import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createCheckedProduct;
import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createUncheckedProduct;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class ProductsLocalDataSourceTest {

    @Mock
    private ProductsRemoteDataSource.LoadProductCallback loadProductCallback;

    @Mock
    private ProductsRemoteDataSource.LoadProductListCallback loadProductListCallback;

    @Captor
    private ArgumentCaptor<Product> productArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<Product>> productListArgumentCaptor;

    private ProductsDatabase productsDatabase;
    private ProductsLocalDataSource productsLocalDataSource;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        productsDatabase = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getContext(),
                ProductsDatabase.class).build();

        ProductsDao productsDao = productsDatabase.productsDao();

        // Make sure we're not keeping a reference to the wrong instance.
        ProductsLocalDataSource.clearInstance();
        AppExecutors.clearInstance();

        Executor executor = Runnable::run;
        productsLocalDataSource = ProductsLocalDataSource.getInstance(
                new AppExecutors(executor, executor),
                productsDao);
    }

    @After
    public void cleanUp() {
        productsDatabase.close();
        ProductsLocalDataSource.clearInstance();
        AppExecutors.clearInstance();
    }

    @Test
    public void shouldRetrieveProduct() {
        //given
        Product givenProduct = createUncheckedProduct();
        productsLocalDataSource.saveProduct(givenProduct);

        //when
        productsLocalDataSource.getProduct(givenProduct.getId(), loadProductCallback);

        //then
        then(loadProductCallback).should().onProductLoaded(productArgumentCaptor.capture());
        then(loadProductCallback).should(never()).onDataNotAvailable();
        Product product = productArgumentCaptor.getValue();
        assertThat(product, equalTo(givenProduct));
    }

    @Test
    public void shouldRetrieveAllProducts() {
        //given
        Product givenProduct1 = createCheckedProduct();
        productsLocalDataSource.saveProduct(givenProduct1);
        Product givenProduct2 = createUncheckedProduct();
        productsLocalDataSource.saveProduct(givenProduct2);

        //when
        productsLocalDataSource.getProducts(loadProductListCallback);

        //then
        then(loadProductListCallback).should().onProductsLoaded(productListArgumentCaptor.capture());
        then(loadProductListCallback).should(never()).onDataNotAvailable();
        List<Product> products = productListArgumentCaptor.getValue();
        assertThat(products, contains(givenProduct1, givenProduct2));

    }

    @Test
    public void shouldCheckProduct() {
        //given
        Product givenUncheckedProduct = createUncheckedProduct();
        productsLocalDataSource.saveProduct(givenUncheckedProduct);

        //when
        productsLocalDataSource.checkProduct(givenUncheckedProduct);

        //then
        productsLocalDataSource.getProduct(givenUncheckedProduct.getId(), loadProductCallback);
        then(loadProductCallback).should().onProductLoaded(productArgumentCaptor.capture());
        then(loadProductCallback).should(never()).onDataNotAvailable();

        Product product = productArgumentCaptor.getValue();
        assertProduct(product,
                givenUncheckedProduct.getId(),
                givenUncheckedProduct.getDescription(),
                givenUncheckedProduct.getQuantity(),
                givenUncheckedProduct.getUnit(),
                true);
    }

    @Test
    public void shouldUncheckProduct() {
        //given
        Product givenCheckedProduct = createCheckedProduct();
        productsLocalDataSource.saveProduct(givenCheckedProduct);

        //when
        productsLocalDataSource.uncheckProduct(givenCheckedProduct);

        //then
        productsLocalDataSource.getProduct(givenCheckedProduct.getId(), loadProductCallback);
        then(loadProductCallback).should().onProductLoaded(productArgumentCaptor.capture());
        then(loadProductCallback).should(never()).onDataNotAvailable();

        Product product = productArgumentCaptor.getValue();
        assertProduct(product,
                givenCheckedProduct.getId(),
                givenCheckedProduct.getDescription(),
                givenCheckedProduct.getQuantity(),
                givenCheckedProduct.getUnit(),
                false);
    }

    @Test
    public void shouldRemoveCheckedProducts() {
        //given
        ProductsRemoteDataSource.LoadProductCallback loadProductCallback1 = mock(ProductsRemoteDataSource.LoadProductCallback.class);
        ProductsRemoteDataSource.LoadProductCallback loadProductCallback2 = mock(ProductsRemoteDataSource.LoadProductCallback.class);

        Product givenCheckedProduct = createCheckedProduct();
        productsLocalDataSource.saveProduct(givenCheckedProduct);
        Product givenUncheckedProduct = createUncheckedProduct();
        productsLocalDataSource.saveProduct(givenUncheckedProduct);

        //when
        productsLocalDataSource.removeCheckedProducts();

        //then
        //verify check product was removed
        productsLocalDataSource.getProduct(givenCheckedProduct.getId(), loadProductCallback1);
        then(loadProductCallback1).should().onDataNotAvailable();
        then(loadProductCallback1).should(never()).onProductLoaded(any(Product.class));
        //verify unchecked product exists
        productsLocalDataSource.getProduct(givenUncheckedProduct.getId(), loadProductCallback2);
        then(loadProductCallback2).should().onProductLoaded(productArgumentCaptor.capture());
        then(loadProductCallback2).should(never()).onDataNotAvailable();
        Product product = productArgumentCaptor.getValue();
        assertThat(product, equalTo(givenUncheckedProduct));

    }

    @Test
    public void shouldRemoveAllProducts() {
        //given
        Product givenProduct = createCheckedProduct();
        productsLocalDataSource.saveProduct(givenProduct);

        //when
        productsLocalDataSource.removeAllProducts();

        //then
        productsLocalDataSource.getProducts(loadProductListCallback);
        then(loadProductListCallback).should().onDataNotAvailable();
        then(loadProductListCallback).should(never()).onProductsLoaded(anyList());
    }
}