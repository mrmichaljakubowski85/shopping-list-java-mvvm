package com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback;

import com.google.common.collect.ImmutableList;
import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.ProductsDataSource;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepositoryImpl;

import java.util.List;

import timber.log.Timber;

public class ProductListLocalCallback implements ProductsDataSource.LoadProductListCallback {

    private final ProductsRepositoryImpl productsRepositoryImpl;
    private final ProductsRepository.LoadProductListCallback repositoryLoadProductListCallback;

    public ProductListLocalCallback(ProductsRepositoryImpl productsRepositoryImpl, ProductsRepository.LoadProductListCallback repositoryLoadProductListCallback) {
        this.productsRepositoryImpl = productsRepositoryImpl;
        this.repositoryLoadProductListCallback = repositoryLoadProductListCallback;
    }

    @Override
    public void onProductsLoaded(List<Product> products) {
        Timber.d("Local data source - onProductsLoaded: %s", products);
        productsRepositoryImpl.refreshCache(products);
        repositoryLoadProductListCallback.onProductsLoaded(ImmutableList.copyOf(products));
    }

    @Override
    public void onDataNotAvailable() {
        Timber.d("Local data source - onDataNotAvailable");
        productsRepositoryImpl.getProductsFromRemoteDataSource(repositoryLoadProductListCallback);
    }
}
