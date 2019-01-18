package com.tomtre.android.architecture.shoppinglistmvp.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;

import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.isNull;

@Database(entities = {Product.class}, version = 1)
public abstract class ProductsDatabase extends RoomDatabase {
    private static ProductsDatabase INSTANCE;

    public abstract ProductsDao productsDao();

    private static final Object LOCK = new Object();

    public static ProductsDatabase getInstance(Context context) {
        if (isNull(INSTANCE)) {
            synchronized (LOCK) {
                if (isNull(INSTANCE)) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ProductsDatabase.class, "Products.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
