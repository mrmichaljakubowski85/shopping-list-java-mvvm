package com.tomtre.android.architecture.shoppinglistmvp.data.source.repository;

import android.arch.lifecycle.LiveData;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;

import java.util.List;

public interface ProductsRepository {

    LiveData<List<Product>> getProducts();

    void refreshProducts();

    void removeCheckedProducts();

    void removeAllProducts();

    LiveData<Product> getProduct(String productId);

    void saveProduct(Product product);

    void removeProduct(String productId);

    void checkProduct(Product product);

    void checkProduct(String productId);

    void uncheckProduct(Product product);

    void uncheckProduct(String productId);

    void forceToLoadFromRemoteNextCall();

}
