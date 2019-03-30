package com.tomtre.android.architecture.shoppinglistmvp.data.source.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.VisibleForTesting;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.local.ProductsDao;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.remote.ProductsRemoteDataSource;
import com.tomtre.android.architecture.shoppinglistmvp.util.AppExecutors;

import java.util.List;

import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.isNull;


@SuppressWarnings("Guava")
public class ProductsRepositoryImpl implements ProductsRepository {

    private static final Object LOCK = new Object();
    private static ProductsRepositoryImpl INSTANCE;
    private final ProductsRemoteDataSource productsRemoteDataSource;
    private final ProductsDao productsDao;
    private final AppExecutors appExecutors;
    private boolean initialized = false;


    private ProductsRepositoryImpl(ProductsRemoteDataSource productsRemoteDataSource,
                                   ProductsDao productsDao,
                                   AppExecutors appExecutors) {
        this.productsRemoteDataSource = productsRemoteDataSource;
        this.productsDao = productsDao;
        this.appExecutors = appExecutors;
    }

    public static ProductsRepositoryImpl getInstance(ProductsRemoteDataSource productsRemoteDataSource,
                                                     ProductsDao productsDao,
                                                     AppExecutors appExecutors) {
        if (isNull(INSTANCE)) {
            synchronized (LOCK) {
                if (isNull(INSTANCE)) {
                    INSTANCE = new ProductsRepositoryImpl(productsRemoteDataSource, productsDao, appExecutors);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    static void clearInstance() {
        INSTANCE = null;
    }

    private void initializeData() {
        if (initialized)
            return;
        initialized = true;
        fetchProductsFromRemoteDataSource();
    }

    @Override
    public void refreshProducts() {
        fetchProductsFromRemoteDataSource();
    }

    @Override
    public LiveData<List<Product>> getProducts() {
        Timber.d("getProducts");
        initializeData();
        return productsDao.getProducts();
    }

    @Override
    public void saveProduct(final Product product) {
        Timber.d("saveProduct: %s", product);
        checkNotNull(product);
        productsRemoteDataSource.saveProduct(product);
        executeOnDiskIOThread(() -> productsDao.insertProduct(product));
    }

    @Override
    public void checkProduct(final Product product) {
        Timber.d("checkProduct: %s", product);
        checkNotNull(product);
        productsRemoteDataSource.checkProduct(product);
        executeOnDiskIOThread(() -> productsDao.updateChecked(product.getId(), true));
    }

    @Override
    public void checkProduct(String productId) {
        Timber.d("checkProduct - id: %s", productId);
        checkNotNull(productId);
        productsRemoteDataSource.checkProduct(productId);
        executeOnDiskIOThread(() -> productsDao.updateChecked(productId, true));
    }

    @Override
    public void uncheckProduct(Product product) {
        Timber.d("uncheckProduct: %s", product);
        checkNotNull(product);
        productsRemoteDataSource.uncheckProduct(product);
        executeOnDiskIOThread(() -> productsDao.updateChecked(product.getId(), false));
    }

    @Override
    public void uncheckProduct(String productId) {
        Timber.d("uncheckProduct - id: %s", productId);
        checkNotNull(productId);
        productsRemoteDataSource.uncheckProduct(productId);
        executeOnDiskIOThread(() -> productsDao.updateChecked(productId, true));
    }

    @Override
    public void removeCheckedProducts() {
        Timber.d("removeCheckedProducts");
        productsRemoteDataSource.removeCheckedProducts();
        executeOnDiskIOThread(productsDao::deleteCheckedProducts);
    }

    @Override
    public LiveData<Product> getProduct(final String productId) {
        Timber.d("getProduct");
        checkNotNull(productId);
        //TODO check local database
        return productsDao.getProductById(productId);
    }

    @Override
    public void removeAllProducts() {
        Timber.d("removeAllProducts");
        productsRemoteDataSource.removeAllProducts();
        executeOnDiskIOThread(productsDao::deleteProducts);
    }

    @Override
    public void removeProduct(final String productId) {
        Timber.d("removeProduct - id: %s", productId);
        checkNotNull(productId);
        productsRemoteDataSource.removeProduct(productId);
        executeOnDiskIOThread(() -> productsDao.deleteProductById(productId));
    }

    public void forceToLoadFromRemoteNextCall() {
        Timber.d("forceToLoadFromRemoteNextCall");

    }

    public void refreshLocalDataSource(final List<Product> products) {
        Timber.d("refreshLocalDataSource");
        executeOnDiskIOThread(() -> productsDao.deleteAndInsertProducts(products));
    }

    public void refreshLocalDataSource(Product product) {
        Timber.d("refreshLocalDataSource:");
        executeOnDiskIOThread(() -> productsDao.insertProduct(product));
    }

    private void fetchProductsFromRemoteDataSource() {
        productsRemoteDataSource.getProducts(this::refreshLocalDataSource);
    }


    private void executeOnDiskIOThread(Runnable runnable) {
        appExecutors.getDiskIOExecutor().execute(runnable);
    }

}
