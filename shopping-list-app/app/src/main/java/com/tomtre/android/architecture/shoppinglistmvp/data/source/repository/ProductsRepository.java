package com.tomtre.android.architecture.shoppinglistmvp.data.source.repository;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;

import java.util.List;

public interface ProductsRepository {

    interface LoadProductListCallback {

        void onProductsLoaded(List<Product> products);

        void onDataNotAvailable();

    }
    interface LoadProductCallback {

        void onProductLoaded(Product product);

        void onDataNotAvailable();

    }

    void getProducts(ProductsRepository.LoadProductListCallback loadProductListCallback);

    void removeCheckedProducts();

    void removeAllProducts();

    void getProduct(String productId, ProductsRepository.LoadProductCallback loadProductCallback);

    void saveProduct(Product product);

    void removeProduct(String productId);

    void checkProduct(Product product);

    void checkProduct(String productId);

    void uncheckProduct(Product product);

    void uncheckProduct(String productId);

    void forceToLoadFromRemoteNextCall();
}
