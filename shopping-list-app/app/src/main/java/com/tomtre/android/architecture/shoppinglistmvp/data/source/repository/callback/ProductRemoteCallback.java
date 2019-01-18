package com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback;

import android.support.annotation.Nullable;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.ProductsDataSource;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepositoryImpl;

public class ProductRemoteCallback implements ProductsDataSource.LoadProductCallback {

    private final ProductsRepositoryImpl productsRepositoryImpl;
    private final ProductsRepository.LoadProductCallback repositoryLoadProductCallback;

    public ProductRemoteCallback(ProductsRepositoryImpl productsRepositoryImpl, ProductsRepository.LoadProductCallback repositoryLoadProductCallback) {
        this.productsRepositoryImpl = productsRepositoryImpl;
        this.repositoryLoadProductCallback = repositoryLoadProductCallback;
    }

    @Override
    public void onProductLoaded(@Nullable Product product) {
        productsRepositoryImpl.refreshCache(product);
        productsRepositoryImpl.refreshLocalDataSource(product);
        repositoryLoadProductCallback.onProductLoaded(product);
    }

    @Override
    public void onDataNotAvailable() {
        repositoryLoadProductCallback.onDataNotAvailable();
    }

}
