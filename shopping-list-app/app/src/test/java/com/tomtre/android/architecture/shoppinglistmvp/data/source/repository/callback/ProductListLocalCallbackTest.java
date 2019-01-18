package com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepositoryImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.prepareUnsortedProducts;
import static org.mockito.BDDMockito.then;


@RunWith(MockitoJUnitRunner.class)
public class ProductListLocalCallbackTest {

    private static List<Product> PRODUCTS = prepareUnsortedProducts();

    @Mock
    private ProductsRepositoryImpl productsRepositoryImpl;

    @Mock
    private ProductsRepository.LoadProductListCallback loadProductListCallback;

    private ProductListLocalCallback productListLocalCallback;

    @Before
    public void setUp() {
        productListLocalCallback = new ProductListLocalCallback(productsRepositoryImpl, loadProductListCallback);
    }

    @Test
    public void shouldRefreshCashWhenProductsLoaded() {
        //when
        productListLocalCallback.onProductsLoaded(PRODUCTS);

        //then
        then(productsRepositoryImpl).should().refreshCache(PRODUCTS);
    }

    @Test
    public void shouldPassDataToCallbackWhenProductsLoaded() {
        //when
        productListLocalCallback.onProductsLoaded(PRODUCTS);

        //then
        then(loadProductListCallback).should().onProductsLoaded(PRODUCTS);
    }

    @Test
    public void shouldGetProductsFromRemoteDataSourceWhenDataNotAvailable() {
        //when
        productListLocalCallback.onDataNotAvailable();

        //then
        then(productsRepositoryImpl).should().getProductsFromRemoteDataSource(loadProductListCallback);
    }
}