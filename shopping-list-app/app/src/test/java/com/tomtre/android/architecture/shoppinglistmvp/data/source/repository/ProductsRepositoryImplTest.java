package com.tomtre.android.architecture.shoppinglistmvp.data.source.repository;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.ProductsCache;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.remote.ProductsRemoteDataSource;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback.ProductListRemoteCallback;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback.ProductRemoteCallback;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createCheckedProduct;
import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createUncheckedProduct;
import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.prepareUnsortedProducts;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class ProductsRepositoryImplTest {

    private static List<Product> PRODUCTS = prepareUnsortedProducts();
    private static Product PRODUCT = createUncheckedProduct();

    @Mock
    private ProductsCache productsCache;

    @Mock
    private ProductsRemoteDataSource productsRemoteDataSource;

    @Mock
    private ProductsRemoteDataSource productsLocalDataSource;

    @Mock
    private ProductsRepository.LoadProductListCallback loadProductListCallback;

    @Mock
    private ProductsRepository.LoadProductCallback loadProductCallback;

    private ProductsRepositoryImpl productsRepositoryImpl;

    @Before
    public void setUp() {
        productsRepositoryImpl = ProductsRepositoryImpl.getInstance(productsCache, productsRemoteDataSource, productsLocalDataSource);
    }

    @After
    public void cleanUp() {
        ProductsRepositoryImpl.clearInstance();
    }

    @Test
    public void shouldRequestProductsFromCacheWhenGetProducts() {
        //when
        productsRepositoryImpl.getProducts(loadProductListCallback);

        //then
        then(productsCache).should().getProducts();
    }

    @Test
    public void shouldPassDataToCallbackWhenGotProductsFromCache() {
        //given
        given(productsCache.getProducts()).willReturn(PRODUCTS);

        //then
        productsRepositoryImpl.getProducts(loadProductListCallback);

        //then
        then(loadProductListCallback).should().onProductsLoaded(PRODUCTS);
    }

    @Test
    public void shouldRequestProductsFromLocalDataSourceWhenGetProductsAndCacheIsEmpty() {
        //given
        setCacheAsReturningEmptyListOfProducts();

        //when
        productsRepositoryImpl.getProducts(loadProductListCallback);

        //then
        then(productsLocalDataSource).should().getProducts(any(ProductsRemoteDataSource.LoadProductListCallback.class));
    }

    @Test
    public void shouldClearCacheWhenForceToLoadFromRemoteNextCall() {
        //when
        productsRepositoryImpl.forceToLoadFromRemoteNextCall();

        //then
        then(productsCache).should().clear();
    }

    @Test
    public void shouldClearLocalDataSourceWhenForceToLoadFromRemoteNextCall() {
        //when
        productsRepositoryImpl.forceToLoadFromRemoteNextCall();

        //then
        then(productsLocalDataSource).should().removeAllProducts();
    }

    @Test
    public void shouldPerformAllSaveProductActionsWhenSaveProduct() {
        //when
        productsRepositoryImpl.saveProduct(PRODUCT);

        //then
        then(productsLocalDataSource).should().saveProduct(PRODUCT);
        then(productsRemoteDataSource).should().saveProduct(PRODUCT);
        then(productsCache).should().save(PRODUCT);
    }

    @Test
    public void shouldPerformAllCheckProductActionsWhenCheckProduct() {
        //given
        Product uncheckedProduct = createUncheckedProduct();
        Product checkedProduct = new Product(uncheckedProduct, true);

        //when
        productsRepositoryImpl.checkProduct(uncheckedProduct);

        //then
        then(productsLocalDataSource).should().checkProduct(uncheckedProduct);
        then(productsRemoteDataSource).should().checkProduct(uncheckedProduct);
        then(productsCache).should().save(checkedProduct);
    }

    @Test
    public void shouldPerformAllCheckProductActionsWhenCheckProductWithId() {
        //given
        Product uncheckedProduct = createUncheckedProduct();
        Product checkedProduct = new Product(uncheckedProduct, true);
        //noinspection Guava
        given(productsCache.getProduct(uncheckedProduct.getId())).willReturn(Optional.of(checkedProduct));

        //when
        productsRepositoryImpl.checkProduct(uncheckedProduct.getId());

        //then
        then(productsLocalDataSource).should().checkProduct(uncheckedProduct.getId());
        then(productsRemoteDataSource).should().checkProduct(uncheckedProduct.getId());
        then(productsCache).should().save(checkedProduct);
    }

    @Test
    public void shouldPerformAllUncheckProductActionsWhenUncheckProduct() {
        //given
        Product checkedProduct = createCheckedProduct();
        Product uncheckedProduct = new Product(checkedProduct, false);

        //when
        productsRepositoryImpl.uncheckProduct(checkedProduct);

        //then
        then(productsLocalDataSource).should().uncheckProduct(checkedProduct);
        then(productsRemoteDataSource).should().uncheckProduct(checkedProduct);
        then(productsCache).should().save(uncheckedProduct);
    }

    @Test
    public void shouldPerformAllUncheckProductActionsWhenUncheckProductWithId() {
        //given
        Product checkedProduct = createCheckedProduct();
        Product uncheckedProduct = new Product(checkedProduct, false);
        //noinspection Guava
        given(productsCache.getProduct(checkedProduct.getId())).willReturn(Optional.of(uncheckedProduct));

        //when
        productsRepositoryImpl.uncheckProduct(checkedProduct.getId());

        //then
        then(productsLocalDataSource).should().uncheckProduct(checkedProduct.getId());
        then(productsRemoteDataSource).should().uncheckProduct(checkedProduct.getId());
        then(productsCache).should().save(uncheckedProduct);
    }

    @Test
    public void shouldRequestProductFromCacheWhenGetProduct() {
        //given
        setCacheAsReturningAbsentProduct();

        //when
        productsRepositoryImpl.getProduct(PRODUCT.getId(), loadProductCallback);

        //then
        then(productsCache).should().getProduct(PRODUCT.getId());
    }

    @Test
    public void shouldPassDataToCallbackWhenGotProduct() {
        //given
        //noinspection Guava
        given(productsCache.getProduct(PRODUCT.getId())).willReturn(Optional.of(PRODUCT));

        //then
        productsRepositoryImpl.getProduct(PRODUCT.getId(), loadProductCallback);

        //then
        then(loadProductCallback).should().onProductLoaded(PRODUCT);
    }

    @Test
    public void shouldRequestProductFromLocalDataSourceWhenGetProductAndNoProductInCache() {
        //given
        setCacheAsReturningAbsentProduct();

        //when
        productsRepositoryImpl.getProduct(PRODUCT.getId(), loadProductCallback);

        //then
        then(productsLocalDataSource).should().getProduct(eq(PRODUCT.getId()), any(ProductsRemoteDataSource.LoadProductCallback.class));
    }

    @Test
    public void shouldPerformAllRemoveCheckedProductsActionsWhenRemoveCheckedProducts() {
        //when
        productsRepositoryImpl.removeCheckedProducts();

        //then
        //noinspection unchecked
        then(productsCache).should().removeAllIf(any(Predicate.class));
        then(productsLocalDataSource).should().removeCheckedProducts();
        then(productsRemoteDataSource).should().removeCheckedProducts();
    }

    @Test
    public void shouldPerformAllRemoveProductsActionsWhenRemoveAllProducts() {
        //when
        productsRepositoryImpl.removeAllProducts();

        //then
        then(productsCache).should().clear();
        then(productsLocalDataSource).should().removeAllProducts();
        then(productsRemoteDataSource).should().removeAllProducts();
    }

    @Test
    public void shouldPerformAllRemoveProductActionsWhenRemoveProduct() {
        //when
        productsRepositoryImpl.removeProduct(PRODUCT.getId());

        //then
        then(productsCache).should().remove(PRODUCT.getId());
        then(productsLocalDataSource).should().removeProduct(PRODUCT.getId());
        then(productsRemoteDataSource).should().removeProduct(PRODUCT.getId());
    }

    @Test
    public void shouldSaveProductsToCacheWhenRefreshCacheWithProducts() {
        //when
        productsRepositoryImpl.refreshCache(PRODUCTS);

        //then
        then(productsCache).should(times(PRODUCTS.size())).save(any(Product.class));
    }

    @Test
    public void shouldSaveProductToCacheWhenRefreshCacheWithProduct() {
        //when
        productsRepositoryImpl.refreshCache(PRODUCT);

        //then
        then(productsCache).should().save(PRODUCT);
    }

    @Test
    public void shouldSaveProductsInLocalDataSourceWhenRefreshLocalDataSourceWithProducts() {
        //when
        productsRepositoryImpl.refreshLocalDataSource(PRODUCTS);

        //then
        then(productsLocalDataSource).should(times(PRODUCTS.size())).saveProduct(any(Product.class));
    }

    @Test
    public void shouldSaveProductInLocalDataSourceWhenRefreshLocalDataSourceWithProduct() {
        //when
        productsRepositoryImpl.refreshLocalDataSource(PRODUCT);

        //then
        then(productsLocalDataSource).should().saveProduct(PRODUCT);
    }

    @Test
    public void shouldRequestProductsFromRemoteDataSourceWhenGetProductsFromRemoteDataSource() {
        //when
        productsRepositoryImpl.getProductsFromRemoteDataSource(loadProductListCallback);

        //then
        then(productsRemoteDataSource).should().getProducts(any(ProductListRemoteCallback.class));
    }

    @Test
    public void shouldRequestProductFromRemoteDataSourceWhenGetProductFromRemoteDataSource() {
        //when
        productsRepositoryImpl.getProductFromRemoteDataSource(PRODUCT.getId(), loadProductCallback);

        //then
        then(productsRemoteDataSource).should().getProduct(eq(PRODUCT.getId()), any(ProductRemoteCallback.class));
    }

    private void setCacheAsReturningEmptyListOfProducts() {
        given(productsCache.getProducts()).willReturn(new ArrayList<>());
    }

    private void setCacheAsReturningAbsentProduct() {
        given(productsCache.getProduct(anyString())).willReturn(Optional.absent());
    }
}