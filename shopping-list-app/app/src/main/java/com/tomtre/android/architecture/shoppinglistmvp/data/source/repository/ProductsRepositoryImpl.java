package com.tomtre.android.architecture.shoppinglistmvp.data.source.repository;

import android.support.annotation.VisibleForTesting;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.ProductsCache;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.ProductsDataSource;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback.ProductListLocalCallback;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback.ProductListRemoteCallback;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback.ProductLocalCallback;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback.ProductRemoteCallback;

import java.util.List;

import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.isNull;


@SuppressWarnings("Guava")
public class ProductsRepositoryImpl implements ProductsRepository {

    private static final Object LOCK = new Object();
    private static ProductsRepositoryImpl INSTANCE;
    private final ProductsCache productsCache;
    private final ProductsDataSource productsRemoteDataSource;
    private final ProductsDataSource productsLocalDataSource;


    private ProductsRepositoryImpl(ProductsCache productsCache, ProductsDataSource productsRemoteDataSource, ProductsDataSource productsLocalDataSource) {
        this.productsCache = productsCache;
        this.productsRemoteDataSource = productsRemoteDataSource;
        this.productsLocalDataSource = productsLocalDataSource;
    }

    public static ProductsRepositoryImpl getInstance(ProductsCache productsCache, ProductsDataSource productsRemoteDataSource, ProductsDataSource productsLocalDataSource) {
        if (isNull(INSTANCE)) {
            synchronized (LOCK) {
                if (isNull(INSTANCE)) {
                    INSTANCE = new ProductsRepositoryImpl(productsCache, productsRemoteDataSource, productsLocalDataSource);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    static void clearInstance() {
        INSTANCE = null;
    }

    @Override
    public void getProducts(final LoadProductListCallback repositoryLoadProductListCallback) {
        Timber.d("getProducts");
        checkNotNull(repositoryLoadProductListCallback);
        List<Product> productsFromCache = getProductsFromCache();
        if (!productsFromCache.isEmpty()) {
            Timber.d("Cache - products: %s", productsFromCache);
            repositoryLoadProductListCallback.onProductsLoaded(productsFromCache);
        } else {
            Timber.d("Cache is empty");
            getProductsFromLocalDataSource(repositoryLoadProductListCallback);
        }
    }

    @Override
    public void saveProduct(Product product) {
        Timber.d("saveProduct: %s", product);
        checkNotNull(product);
        productsRemoteDataSource.saveProduct(product);
        productsLocalDataSource.saveProduct(product);
        productsCache.save(product);
    }

    @Override
    public void checkProduct(Product product) {
        Timber.d("checkProduct: %s", product);
        checkNotNull(product);
        productsRemoteDataSource.checkProduct(product);
        productsLocalDataSource.checkProduct(product);
        Product checkedProduct = new Product(product, true);
        productsCache.save(checkedProduct);
    }

    @Override
    public void checkProduct(String productId) {
        Timber.d("checkProduct - id: %s", productId);
        checkNotNull(productId);
        productsRemoteDataSource.checkProduct(productId);
        productsLocalDataSource.checkProduct(productId);
        Optional<Product> productOptional = productsCache.getProduct(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            Product checkedProduct = new Product(product, true);
            productsCache.save(checkedProduct);
        }
    }

    @Override
    public void uncheckProduct(Product product) {
        Timber.d("uncheckProduct: %s", product);
        checkNotNull(product);
        productsRemoteDataSource.uncheckProduct(product);
        productsLocalDataSource.uncheckProduct(product);
        Product uncheckedProduct = new Product(product, false);
        productsCache.save(uncheckedProduct);
    }

    @Override
    public void uncheckProduct(String productId) {
        Timber.d("uncheckProduct - id: %s", productId);
        checkNotNull(productId);
        productsRemoteDataSource.uncheckProduct(productId);
        productsLocalDataSource.uncheckProduct(productId);
        Optional<Product> productOptional = productsCache.getProduct(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            Product uncheckedProduct = new Product(product, false);
            productsCache.save(uncheckedProduct);
        }
    }

    @Override
    public void removeCheckedProducts() {
        Timber.d("removeCheckedProducts");
        productsRemoteDataSource.removeCheckedProducts();
        productsLocalDataSource.removeCheckedProducts();
        productsCache.removeAllIf(Product::isChecked);
    }

    @Override
    public void getProduct(final String productId, final LoadProductCallback repositoryLoadProductCallback) {
        Timber.d("getProduct");
        checkNotNull(productId);
        checkNotNull(repositoryLoadProductCallback);
        Optional<Product> productOptional = productsCache.getProduct(productId);
        if (productOptional.isPresent()) {
            Timber.d("Cache - product: %s", productOptional.get());
            repositoryLoadProductCallback.onProductLoaded(productOptional.get());
        } else {
            Timber.d("Cache is empty");
            getProductFromLocalDataSource(productId, repositoryLoadProductCallback);
        }
    }

    @Override
    public void removeAllProducts() {
        Timber.d("removeAllProducts");
        productsRemoteDataSource.removeAllProducts();
        productsLocalDataSource.removeAllProducts();
        productsCache.clear();
    }

    @Override
    public void removeProduct(String productId) {
        Timber.d("removeProduct - id: %s", productId);
        checkNotNull(productId);
        productsRemoteDataSource.removeProduct(productId);
        productsLocalDataSource.removeProduct(productId);
        productsCache.remove(productId);
    }

    public void forceToLoadFromRemoteNextCall() {
        Timber.d("forceToLoadFromRemoteNextCall");
        productsLocalDataSource.removeAllProducts();
        productsCache.clear();
    }

    public void refreshLocalDataSource(List<Product> products) {
        Timber.d("refreshLocalDataSource");
        productsLocalDataSource.removeAllProducts();
        for (Product product : products) {
            productsLocalDataSource.saveProduct(product);
        }
    }

    public void refreshLocalDataSource(Product product) {
        Timber.d("refreshLocalDataSource:");
        productsLocalDataSource.saveProduct(product);
    }

    public void refreshCache(List<Product> products) {
        Timber.d("refreshCache");
        productsCache.clear();
        for (Product product : products) {
            productsCache.save(product);
        }
    }

    public void refreshCache(Product product) {
        Timber.d("refreshCache");
        productsCache.save(product);
    }

    public void getProductsFromRemoteDataSource(final LoadProductListCallback loadProductListCallback) {
        Timber.d("getProductsFromRemoteDataSource");
        productsRemoteDataSource.getProducts(new ProductListRemoteCallback(this, loadProductListCallback));
    }

    public void getProductFromRemoteDataSource(final String productId, final LoadProductCallback loadProductCallback) {
        Timber.d("getProductFromRemoteDataSource - id: %s", productId);
        productsRemoteDataSource.getProduct(productId, new ProductRemoteCallback(this, loadProductCallback));
    }

    private void getProductsFromLocalDataSource(final LoadProductListCallback loadProductListCallback) {
        Timber.d("getProductsFromLocalDataSource");
        productsLocalDataSource.getProducts(new ProductListLocalCallback(this, loadProductListCallback));
    }

    private void getProductFromLocalDataSource(final String productId, final LoadProductCallback loadProductCallback) {
        Timber.d("getProductFromLocalDataSource - id: %s", productId);
        productsLocalDataSource.getProduct(productId, new ProductLocalCallback(this, productId, loadProductCallback));
    }

    private List<Product> getProductsFromCache() {
        return ImmutableList.copyOf(productsCache.getProducts());
    }
}
