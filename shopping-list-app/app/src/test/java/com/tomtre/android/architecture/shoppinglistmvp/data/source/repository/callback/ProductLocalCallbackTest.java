package com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepositoryImpl;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback.ProductLocalCallback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createUncheckedProduct;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class ProductLocalCallbackTest {

    private static Product PRODUCT  = createUncheckedProduct();

    @Mock
    private ProductsRepositoryImpl productsRepositoryImpl;

    @Mock
    private ProductsRepository.LoadProductCallback loadProductCallback;

    private ProductLocalCallback productLocalCallback;

    @Before
    public void setUp() {
        productLocalCallback = new ProductLocalCallback(productsRepositoryImpl, PRODUCT.getId(), loadProductCallback);
    }

    @Test
    public void shouldRefreshCashWhenProductLoaded() {
        //when
        productLocalCallback.onProductLoaded(PRODUCT);

        //then
        then(productsRepositoryImpl).should().refreshCache(PRODUCT);
    }

    @Test
    public void shouldPassDataToCallbackWhenProductLoaded() {
        //when
        productLocalCallback.onProductLoaded(PRODUCT);

        //then
        then(loadProductCallback).should().onProductLoaded(PRODUCT);
    }

    @Test
    public void shouldGetProductFromRemoteDataSourceWhenDataNotAvailable() {
        //when
        productLocalCallback.onDataNotAvailable();

        //then
        then(productsRepositoryImpl).should().getProductFromRemoteDataSource(PRODUCT.getId(), loadProductCallback);
    }
}