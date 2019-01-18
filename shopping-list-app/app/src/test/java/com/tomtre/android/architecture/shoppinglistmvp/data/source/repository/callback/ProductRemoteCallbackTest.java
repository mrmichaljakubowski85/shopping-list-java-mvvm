package com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepositoryImpl;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback.ProductRemoteCallback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createUncheckedProduct;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class ProductRemoteCallbackTest {

    private static Product PRODUCT  = createUncheckedProduct();

    @Mock
    private ProductsRepositoryImpl productsRepositoryImpl;

    @Mock
    private ProductsRepository.LoadProductCallback loadProductCallback;

    private ProductRemoteCallback productRemoteCallback;

    @Before
    public void setUp() {
        productRemoteCallback = new ProductRemoteCallback(productsRepositoryImpl, loadProductCallback);
    }

    @Test
    public void shouldRefreshCashWhenProductLoaded() {
        //when
        productRemoteCallback.onProductLoaded(PRODUCT);

        //then
        then(productsRepositoryImpl).should().refreshCache(PRODUCT);
    }

    @Test
    public void shouldRefreshLocalDataSourceWhenProductLoaded() {
        //when
        productRemoteCallback.onProductLoaded(PRODUCT);

        //then
        then(productsRepositoryImpl).should().refreshLocalDataSource(PRODUCT);
    }

    @Test
    public void shouldPassDataToCallbackWhenProductLoaded() {
        //when
        productRemoteCallback.onProductLoaded(PRODUCT);

        //then
        then(loadProductCallback).should().onProductLoaded(PRODUCT);
    }

    @Test
    public void shouldDelegateToCallbackWhenDataNotAvailable() {
        //when
        productRemoteCallback.onDataNotAvailable();

        //then
        then(loadProductCallback).should().onDataNotAvailable();
    }

}