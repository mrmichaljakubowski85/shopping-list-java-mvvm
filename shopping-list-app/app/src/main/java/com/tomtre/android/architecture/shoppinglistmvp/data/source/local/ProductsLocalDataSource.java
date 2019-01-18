package com.tomtre.android.architecture.shoppinglistmvp.data.source.local;

import android.support.annotation.VisibleForTesting;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.ProductsDataSource;
import com.tomtre.android.architecture.shoppinglistmvp.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.isNull;

public class ProductsLocalDataSource implements ProductsDataSource {

    private static final Object LOCK = new Object();
    private static ProductsLocalDataSource INSTANCE;
    private final AppExecutors appExecutors;
    private final ProductsDao productsDao;

    private ProductsLocalDataSource(AppExecutors appExecutors, ProductsDao productsDao) {
        this.appExecutors = appExecutors;
        this.productsDao = productsDao;
    }

    public static ProductsLocalDataSource getInstance(AppExecutors appExecutors, ProductsDao productsDao) {
        if (isNull(INSTANCE)) {
            synchronized (LOCK) {
                if (isNull(INSTANCE)) {
                    INSTANCE = new ProductsLocalDataSource(appExecutors, productsDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getProducts(final LoadProductListCallback loadProductListCallback) {
        Runnable runnable = () -> {
            final List<Product> products = productsDao.getProducts();
            executeOnMainThread(() -> {
                if (products.isEmpty())
                    loadProductListCallback.onDataNotAvailable();
                else
                    loadProductListCallback.onProductsLoaded(products);
            });
        };
        executeOnDiskIOThread(runnable);

    }

    @Override
    public void removeCheckedProducts() {
        executeOnDiskIOThread(productsDao::deleteCheckedProducts);
    }

    @Override
    public void removeAllProducts() {
        executeOnDiskIOThread(productsDao::deleteProducts);
    }

    @Override
    public void getProduct(final String productId, final LoadProductCallback loadProductCallback) {
        Runnable runnable = () -> {
            final Product product = productsDao.getProductById(productId);
            executeOnMainThread(() -> {
                if (isNull(product))
                    loadProductCallback.onDataNotAvailable();
                else
                    loadProductCallback.onProductLoaded(product);
            });
        };
        executeOnDiskIOThread(runnable);
    }

    @Override
    public void saveProduct(final Product product) {
        checkNotNull(product);
        executeOnDiskIOThread(() -> productsDao.insertProduct(product));
    }

    @Override
    public void removeProduct(final String productId) {
        executeOnDiskIOThread(() -> productsDao.deleteProductById(productId));
    }

    @Override
    public void checkProduct(final Product product) {
        executeOnDiskIOThread(() -> productsDao.updateChecked(product.getId(), true));
    }

    @Override
    public void checkProduct(String productId) {
        executeOnDiskIOThread(() -> productsDao.updateChecked(productId, true));
    }

    @Override
    public void uncheckProduct(final Product product) {
        executeOnDiskIOThread(() -> productsDao.updateChecked(product.getId(), false));
    }

    @Override
    public void uncheckProduct(String productId) {
        executeOnDiskIOThread(() -> productsDao.updateChecked(productId, false));
    }

    private void executeOnMainThread(Runnable runnable) {
        appExecutors.getMainThreadExecutor().execute(runnable);
    }

    private void executeOnDiskIOThread(Runnable runnable) {
        appExecutors.getDiskIOExecutor().execute(runnable);
    }

    @VisibleForTesting
    static void clearInstance() {
        INSTANCE = null;
    }
}
