package com.tomtre.android.architecture.shoppinglistmvp.ui.products;

import android.app.Activity;

import com.google.common.collect.ImmutableList;
import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepositoryImpl;
import com.tomtre.android.architecture.shoppinglistmvp.util.RequestCodes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createCheckedProduct;
import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createUncheckedProduct;
import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.prepareSortedByTitleProducts;
import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.prepareUnsortedProducts;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;

@RunWith(MockitoJUnitRunner.class)
public class ProductsPresenterTest {

    private static List<Product> PRODUCTS = prepareUnsortedProducts();

    @Mock
    private ProductsRepositoryImpl productsRepositoryImpl;

    @Mock
    private ProductsContract.View productsView;

    @Captor
    private ArgumentCaptor<List<Product>> showProductsArgumentCaptor;

    private ProductsPresenter productsPresenter;

    @Before
    public void setUp() {
        productsPresenter = new ProductsPresenter(productsRepositoryImpl);
        productsPresenter.takeView(productsView);
        given(productsView.isActive()).willReturn(true);
    }

    @Test
    public void shouldGetProductsFromRepositoryWhenPresenterStarts() {
        //when
        productsPresenter.start();

        //then
        then(productsRepositoryImpl).should().getProducts(any(ProductsRepository.LoadProductListCallback.class));
    }

    @Test
    public void shouldShowLoadingIndicatorWhenLoadProducts() {
        //when
        productsPresenter.loadProducts(false);

        //then
        then(productsView).should().setLoadingIndicator(true);
    }

    @Test
    public void shouldShowAllProductsWhenProcessProductsAndFilterSetOnAllProducts() {
        //given
        productsPresenter.setFilterType(ProductsFilterType.ALL_PRODUCTS);

        //when
        productsPresenter.processProducts(PRODUCTS);

        //then
        then(productsView).should().showProducts(showProductsArgumentCaptor.capture());
        List<Product> actualProducts = showProductsArgumentCaptor.getValue();
        assertThat(actualProducts, equalTo(PRODUCTS));
    }

    @Test
    public void shouldShowCheckedProductsWhenProcessProductsAndFilterSetOnChecked() {
        //given
        productsPresenter.setFilterType(ProductsFilterType.CHECKED_PRODUCTS);

        //when
        productsPresenter.processProducts(PRODUCTS);

        //then
        then(productsView).should().showProducts(showProductsArgumentCaptor.capture());
        List<Product> actualProducts = showProductsArgumentCaptor.getValue();
        assertThat(actualProducts, hasSize(equalTo(1)));
    }

    @Test
    public void shouldShowUncheckedProductsWhenProcessProductsAndFilterSetOnUnchecked() {
        //given
        productsPresenter.setFilterType(ProductsFilterType.UNCHECKED_PRODUCTS);

        //when
        productsPresenter.processProducts(PRODUCTS);

        //then
        then(productsView).should().showProducts(showProductsArgumentCaptor.capture());
        List<Product> actualProducts = showProductsArgumentCaptor.getValue();
        assertThat(actualProducts, hasSize(equalTo(3)));
    }

    @Test
    public void shouldShowSortedProductsWhenProcessProductsAndFilterSetOnSortedByTitle() {
        //given
        productsPresenter.setFilterType(ProductsFilterType.SORTED_BY_PRODUCTS_TITLE);

        //when
        productsPresenter.processProducts(PRODUCTS);

        //then
        then(productsView).should().showProducts(showProductsArgumentCaptor.capture());
        List<Product> actualProducts = showProductsArgumentCaptor.getValue();
        List<Product> expectedProducts = prepareSortedByTitleProducts();
        assertThat(actualProducts, equalTo(expectedProducts));
    }

    @Test
    public void shouldShowNoProductsWhenProcessNoProductsAndFilterSetOnAllProducts() {
        //given
        productsPresenter.setFilterType(ProductsFilterType.ALL_PRODUCTS);

        //when
        productsPresenter.processProducts(ImmutableList.of());

        //then
        then(productsView).should().showNoProducts();
    }

    @Test
    public void shouldShowNoProductsWhenProcessNoProductsAndFilterSetOnSortedByTitle() {
        //given
        productsPresenter.setFilterType(ProductsFilterType.SORTED_BY_PRODUCTS_TITLE);

        //when
        productsPresenter.processProducts(ImmutableList.of());

        //then
        then(productsView).should().showNoProducts();
    }

    @Test
    public void shouldShowNoCheckedProductsWhenProcessUncheckedProductsAndFilterSetOnChecked() {
        //given
        productsPresenter.setFilterType(ProductsFilterType.CHECKED_PRODUCTS);

        //when
        productsPresenter.processProducts(ImmutableList.of(createUncheckedProduct()));

        //then
        then(productsView).should().showNoCheckedProducts();
    }

    @Test
    public void shouldShowNoUncheckedProductsWhenProcessCheckedProductsAndFilterSetOnUnchecked() {
        //given
        productsPresenter.setFilterType(ProductsFilterType.UNCHECKED_PRODUCTS);

        //when
        productsPresenter.processProducts(ImmutableList.of(createCheckedProduct()));

        //then
        then(productsView).should().showNoUncheckedProducts();
    }

    @Test
    public void shouldShowAllFilterLabelWhenProcessProductsAndFilterSetOnAllProducts() {
        //given
        productsPresenter.setFilterType(ProductsFilterType.ALL_PRODUCTS);

        //when
        productsPresenter.processProducts(PRODUCTS);

        //then
        then(productsView).should().showAllFilterLabel();
    }

    @Test
    public void shouldShowCheckedFilterLabelWhenProcessProductsAndFilterSetOnChecked() {
        //given
        productsPresenter.setFilterType(ProductsFilterType.CHECKED_PRODUCTS);

        //when
        productsPresenter.processProducts(PRODUCTS);

        //then
        then(productsView).should().showCheckedFilterLabel();
    }

    @Test
    public void shouldShowUncheckedFilterLabelWhenProcessProductsAndFilterSetOnUnchecked() {
        //given
        productsPresenter.setFilterType(ProductsFilterType.UNCHECKED_PRODUCTS);

        //when
        productsPresenter.processProducts(PRODUCTS);

        //then
        then(productsView).should().showUncheckedFilterLabel();
    }

    @Test
    public void shouldShowSortedByTitleFilterLabelWhenProcessProductsAndFilterSetOnSortedByTitle() {
        //given
        productsPresenter.setFilterType(ProductsFilterType.SORTED_BY_PRODUCTS_TITLE);

        //when
        productsPresenter.processProducts(PRODUCTS);

        //then
        then(productsView).should().showSortedByTitleFilterLabel();
    }

    @Test
    public void shouldForceToLoadFromRemoteNextCallWhenLoadProductsAndForceUpdateSetOnTrue() {
        //when
        productsPresenter.loadProducts(true);

        //then
        then(productsRepositoryImpl).should().forceToLoadFromRemoteNextCall();
    }

    @Test
    public void shouldDelegateShowLoadingProductsErrorToViewWhenShowLoadingProductsError() {
        //when
        productsPresenter.showLoadingProductsErrorInView();

        //then
        then(productsView).should().showLoadingProductsError();
    }

    @Test
    public void shouldDelegateSetLoadingIndicatorToViewWhenSetInactiveLoadingIndicator() {
        //when
        productsPresenter.setInactiveLoadingIndicatorInView();

        //then
        then(productsView).should().setLoadingIndicator(false);
    }

    @Test
    public void shouldShowProductDetailsUiWhenOpenProductDetails() {
        //given
        Product product = createUncheckedProduct();

        //when
        productsPresenter.openProductDetails(product);

        //then
        then(productsView).should().showProductDetailsUI(product.getId());
    }

    @Test
    public void shouldCheckProductInRepositoryAndReloadProductsWhenCheckProduct() {
        //given
        Product product = createUncheckedProduct();

        //when
        productsPresenter.checkProduct(product);

        //then
        InOrder inOrder = inOrder(productsRepositoryImpl);
        then(productsRepositoryImpl).should(inOrder).checkProduct(product);
        then(productsRepositoryImpl).should(inOrder).getProducts(any(ProductsRepository.LoadProductListCallback.class));
    }

    @Test
    public void shouldMarkProductAsCheckedWhenCheckProduct() {
        //given
        Product product = createUncheckedProduct();

        //when
        productsPresenter.checkProduct(product);

        //then
        then(productsView).should().showProductMarkedAsChecked();
    }

    @Test
    public void shouldUncheckProductInRepositoryAndReloadProductsWhenUncheckProduct() {
        //given
        Product product = createUncheckedProduct();

        //when
        productsPresenter.uncheckProduct(product);

        //then
        InOrder inOrder = inOrder(productsRepositoryImpl);
        then(productsRepositoryImpl).should(inOrder).uncheckProduct(product);
        then(productsRepositoryImpl).should(inOrder).getProducts(any(ProductsRepository.LoadProductListCallback.class));
    }

    @Test
    public void shouldMarkProductAsUncheckedWhenUncheckProduct() {
        //given
        Product requestedProduct = createUncheckedProduct();

        //when
        productsPresenter.uncheckProduct(requestedProduct);

        //then
        then(productsView).should().showProductMarkedAsUnchecked();
    }

    @Test
    public void shouldShowAddProductUiWhenAddNewProduct() {
        //when
        productsPresenter.addNewProduct();

        //then
        then(productsView).should().showAddProductUI();
    }

    @Test
    public void shouldRemoveCheckedProductsInRepositoryAndReloadProductsWhenRemoveAllCheckedProducts() {
        //when
        productsPresenter.removeCheckedProducts();

        //then
        InOrder inOrder = inOrder(productsRepositoryImpl);
        then(productsRepositoryImpl).should(inOrder).removeCheckedProducts();
        then(productsRepositoryImpl).should(inOrder).getProducts(any(ProductsRepository.LoadProductListCallback.class));
    }

    @Test
    public void shouldShowRemovedCheckedProductsWhenRemoveAllCheckedProducts() {
        //when
        productsPresenter.removeCheckedProducts();

        //then
        then(productsView).should().showRemovedCheckedProducts();
    }

    @Test
    public void shouldShowSuccessfullySavedMessageWhenCallActivityResultAndRequestCodeIsAddProduct() {
        //given
        int requestCode = RequestCodes.ADD_PRODUCT;
        int resultCode = Activity.RESULT_OK;

        //when
        productsPresenter.activityResult(requestCode, resultCode);

        //then
        then(productsView).should().showSuccessfullySavedMessage();
    }

    @Test
    public void shouldShowRemovedProductWhenCallActivityResultAndRequestCodeIsRemoveProduct() {
        //given
        int requestCode = RequestCodes.REMOVE_PRODUCT;
        int resultCode = Activity.RESULT_OK;

        //when
        productsPresenter.activityResult(requestCode, resultCode);

        //then
        then(productsView).should().showRemovedProduct();
    }

}