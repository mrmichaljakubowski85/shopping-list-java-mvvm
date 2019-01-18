package com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.callback;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.ProductsDataSource;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepositoryImpl;

public class ProductLocalCallback implements ProductsDataSource.LoadProductCallback {

    private final ProductsRepositoryImpl productsRepositoryImpl;
    private final String productId;
    private final ProductsRepository.LoadProductCallback repositoryLoadProductCallback;

    public ProductLocalCallback(ProductsRepositoryImpl productsRepositoryImpl, String productId, ProductsRepository.LoadProductCallback repositoryLoadProductCallback) {
        this.productsRepositoryImpl = productsRepositoryImpl;
        this.productId = productId;
        this.repositoryLoadProductCallback = repositoryLoadProductCallback;
    }

    @Override
    public void onProductLoaded(Product product) {
        productsRepositoryImpl.refreshCache(product);
        repositoryLoadProductCallback.onProductLoaded(product);
    }

    @Override
    public void onDataNotAvailable() {
        productsRepositoryImpl.getProductFromRemoteDataSource(productId, repositoryLoadProductCallback);
    }
}
