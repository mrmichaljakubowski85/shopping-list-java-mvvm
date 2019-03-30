package com.tomtre.android.architecture.shoppinglistmvp.data.source.remote;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;

import java.util.List;


public interface ProductsRemoteDataSource {

    interface LoadProductListCallback {

        void onProductsLoaded(List<Product> products);

    }

    interface LoadProductCallback {

        void onProductLoaded(Product product);

    }

    void getProducts(LoadProductListCallback loadProductListCallback);

    void removeCheckedProducts();

    void removeAllProducts();

    void getProduct(String productId, LoadProductCallback loadProductCallback);

    void saveProduct(Product product);

    void removeProduct(String productId);

    void checkProduct(Product product);

    void checkProduct(String productId);

    void uncheckProduct(Product product);

    void uncheckProduct(String productId);
}
