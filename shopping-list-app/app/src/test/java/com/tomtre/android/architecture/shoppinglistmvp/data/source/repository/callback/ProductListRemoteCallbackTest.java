package com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepositoryImpl;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback.ProductListRemoteCallback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.prepareUnsortedProducts;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class ProductListRemoteCallbackTest {

    private static List<Product> PRODUCTS = prepareUnsortedProducts();

    @Mock
    private ProductsRepositoryImpl productsRepositoryImpl;

    @Mock
    private ProductsRepository.LoadProductListCallback loadProductListCallback;

    private ProductListRemoteCallback productListRemoteCallback;

    @Before
    public void setUp() {
        productListRemoteCallback = new ProductListRemoteCallback(productsRepositoryImpl, loadProductListCallback);
    }

    @Test
    public void shouldRefreshCashWhenProductsLoaded() {
        //when
        productListRemoteCallback.onProductsLoaded(PRODUCTS);

        //then
        then(productsRepositoryImpl).should().refreshCache(PRODUCTS);
    }

    @Test
    public void shouldRefreshLocalDataSourceWhenProductsLoaded() {
        //when
        productListRemoteCallback.onProductsLoaded(PRODUCTS);

        //then
        then(productsRepositoryImpl).should().refreshLocalDataSource(PRODUCTS);
    }

    @Test
    public void shouldPassDataToCallbackWhenProductsLoaded() {
        //when
        productListRemoteCallback.onProductsLoaded(PRODUCTS);

        //then
        then(loadProductListCallback).should().onProductsLoaded(PRODUCTS);
    }

    @Test
    public void shouldDelegateToCallbackWhenDataNotAvailable() {
        //when
        productListRemoteCallback.onDataNotAvailable();

        //then
        then(loadProductListCallback).should().onDataNotAvailable();
    }
}