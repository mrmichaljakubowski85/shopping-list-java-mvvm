package com.tomtre.android.architecture.shoppinglistmvp.data.source.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;

import java.util.List;

@Dao
public abstract class ProductsDao {

    @Query("SELECT * FROM Products")
    public abstract LiveData<List<Product>> getProducts();

    @Query("SELECT * FROM Products WHERE id = :productId")
    public abstract LiveData<Product> getProductById(String productId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertProduct(Product product);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertProducts(List<Product> products);

    @Query("DELETE FROM Products WHERE id = :productId")
    public abstract void deleteProductById(String productId);

    @Query("UPDATE Products SET checked = :checked WHERE id = :productId")
    public abstract void updateChecked(String productId, boolean checked);

    @Query("DELETE FROM Products WHERE checked = 1")
    public abstract void deleteCheckedProducts();

    @Query("DELETE FROM Products")
    public abstract void deleteProducts();

    @Transaction
    public void deleteAndInsertProducts(List<Product> products) {
        deleteProducts();
        insertProducts(products);
    }
}
