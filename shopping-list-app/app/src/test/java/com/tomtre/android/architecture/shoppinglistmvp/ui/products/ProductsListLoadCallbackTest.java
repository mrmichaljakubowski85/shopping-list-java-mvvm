package com.tomtre.android.architecture.shoppinglistmvp.ui.products;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.prepareUnsortedProducts;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class ProductsListLoadCallbackTest {

    private final static List<Product> PRODUCTS = prepareUnsortedProducts();

    @Mock
    private ProductsPresenter productsPresenter;

    private ProductsListLoadCallback productsListLoadCallback;

    @Before
    public void setUp() {
        productsListLoadCallback = new ProductsListLoadCallback(productsPresenter, true);
    }

    @Test
    public void shouldProcessProductsWhenProductsLoaded() {
        //given
        productsListLoadCallback = new ProductsListLoadCallback(productsPresenter, true);

        //when
        productsListLoadCallback.onProductsLoaded(PRODUCTS);

        //then
        then(productsPresenter).should().processProducts(PRODUCTS);
    }

    @Test
    public void shouldSetInactiveLoadingIndicatorWhenProductsLoadedAndShowLoadingIndicatorSetAsTrue() {
        //given
        productsListLoadCallback = new ProductsListLoadCallback(productsPresenter, true);

        //when
        productsListLoadCallback.onProductsLoaded(PRODUCTS);

        //then
        then(productsPresenter).should().setInactiveLoadingIndicatorInView();
    }

    @Test
    public void shouldNotPerformSetInactiveLoadingIndicatorActionWhenProductsLoadedAndShowLoadingIndicatorSetAsFalse() {
        //given
        productsListLoadCallback = new ProductsListLoadCallback(productsPresenter, false);

        //when
        productsListLoadCallback.onProductsLoaded(PRODUCTS);

        //then
        then(productsPresenter).should(never()).setInactiveLoadingIndicatorInView();
    }

    @Test
    public void shouldShowLoadingProductsErrorWhenDataNotAvailable() {
        //given
        productsListLoadCallback = new ProductsListLoadCallback(productsPresenter, true);

        //when
        productsListLoadCallback.onDataNotAvailable();

        //then
        then(productsPresenter).should().showLoadingProductsErrorInView();
    }

    @Test
    public void shouldSetInactiveLoadingIndicatorWhenDataNotAvailableAndShowLoadingIndicatorSetAsTrue() {
        //given
        productsListLoadCallback = new ProductsListLoadCallback(productsPresenter, true);

        //when
        productsListLoadCallback.onDataNotAvailable();

        //then
        then(productsPresenter).should().setInactiveLoadingIndicatorInView();
    }

    @Test
    public void shouldNotPerformSetInactiveLoadingIndicatorActionWhenDataNotAvailableAndShowLoadingIndicatorSetAsFalse() {
        //given
        productsListLoadCallback = new ProductsListLoadCallback(productsPresenter, false);

        //when
        productsListLoadCallback.onDataNotAvailable();

        //then
        then(productsPresenter).should(never()).setInactiveLoadingIndicatorInView();
    }
}