package com.tomtre.android.architecture.shoppinglistmvp.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;

import java.util.List;

@Dao
public interface ProductsDao {

    @Query("SELECT * FROM Products")
    List<Product> getProducts();

    @Query("SELECT * FROM Products WHERE id = :productId")
    Product getProductById(String productId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProduct(Product product);

    @Query("DELETE FROM Products WHERE id = :productId")
    void deleteProductById(String productId);

    @Query("UPDATE Products SET checked = :checked WHERE id = :productId")
    void updateChecked(String productId, boolean checked);

    @Query("DELETE FROM Products WHERE checked = 1")
    void deleteCheckedProducts();

    @Query("DELETE FROM Products")
    void deleteProducts();
}
