package com.tomtre.android.architecture.shoppinglistmvp.ui.productdetail;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createUncheckedProduct;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailLoadCallbackTest {

    private final Product PRODUCT = createUncheckedProduct();

    @Mock
    private ProductDetailPresenter productDetailPresenter;

    private ProductDetailLoadCallback productDetailLoadCallback;

    @Before
    public void setUp() {
        productDetailLoadCallback = new ProductDetailLoadCallback(productDetailPresenter);
    }

    @Test
    public void shouldSetInactiveLoadingIndicatorWhenProductLoaded() {
        //when
        productDetailLoadCallback.onProductLoaded(PRODUCT);

        //then
        then(productDetailPresenter).should().setInactiveLoadingIndicatorInView();
    }

    @Test
    public void shouldProcessProductWhenProductLoaded() {
        //when
        productDetailLoadCallback.onProductLoaded(PRODUCT);

        //then
        then(productDetailPresenter).should().processProduct(PRODUCT);
    }

    @Test
    public void shouldSetInactiveLoadingIndicatorWhenDataNotAvailable() {
        //when
        productDetailLoadCallback.onDataNotAvailable();

        //then
        then(productDetailPresenter).should().setInactiveLoadingIndicatorInView();
    }

    @Test
    public void shouldShowMissingProductInViewWhenDataNotAvailable() {
        //when
        productDetailLoadCallback.onDataNotAvailable();

        //then
        then(productDetailPresenter).should().showMissingProductInView();
    }
}