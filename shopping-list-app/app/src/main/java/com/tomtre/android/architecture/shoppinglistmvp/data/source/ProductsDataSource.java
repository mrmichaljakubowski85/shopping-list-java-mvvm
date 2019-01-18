package com.tomtre.android.architecture.shoppinglistmvp.data.source;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;

import java.util.List;


public interface ProductsDataSource {

    interface LoadProductListCallback {

        void onProductsLoaded(List<Product> products);

        void onDataNotAvailable();
    }

    interface LoadProductCallback {

        void onProductLoaded(Product product);

        void onDataNotAvailable();
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
