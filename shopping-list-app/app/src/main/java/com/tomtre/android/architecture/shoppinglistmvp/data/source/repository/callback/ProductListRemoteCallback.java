package com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.ProductsDataSource;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepositoryImpl;

import java.util.List;

import timber.log.Timber;

public class ProductListRemoteCallback implements ProductsDataSource.LoadProductListCallback {

    private final ProductsRepositoryImpl productsRepositoryImpl;
    private final ProductsRepository.LoadProductListCallback repositoryLoadProductListCallback;

    public ProductListRemoteCallback(ProductsRepositoryImpl productsRepositoryImpl, ProductsRepository.LoadProductListCallback repositoryLoadProductListCallback) {
        this.productsRepositoryImpl = productsRepositoryImpl;
        this.repositoryLoadProductListCallback = repositoryLoadProductListCallback;
    }

    @Override
    public void onProductsLoaded(List<Product> products) {
        Timber.d("Remote data source - onProductsLoaded: %s", products);
        productsRepositoryImpl.refreshCache(products);
        productsRepositoryImpl.refreshLocalDataSource(products);
        repositoryLoadProductListCallback.onProductsLoaded(products);
    }

    @Override
    public void onDataNotAvailable() {
        Timber.d("Remote data source - onDataNotAvailable");
        repositoryLoadProductListCallback.onDataNotAvailable();
    }
}
