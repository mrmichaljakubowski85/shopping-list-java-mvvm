package com.tomtre.android.architecture.shoppinglistmvp.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tomtre.android.architecture.shoppinglistmvp.data.source.local.ProductsDatabase;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.local.ProductsLocalDataSource;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.remote.ProductsRemoteDataSource;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepositoryImpl;
import com.tomtre.android.architecture.shoppinglistmvp.util.AppExecutors;

import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;

public class Injection {
    public static ProductsRepository provideProductsRepository(@NonNull Context context) {
        checkNotNull(context);
        Context appContext = context.getApplicationContext();

        ProductsCache productsCache = ProductsCache.getInstance();
        ProductsRemoteDataSource productsRemoteDataSource = ProductsRemoteDataSource.getInstance();
        AppExecutors appExecutors = AppExecutors.getInstance(Executors.newSingleThreadExecutor(), new AppExecutors.MainThreadExecutor());
        ProductsDatabase productsDatabase = ProductsDatabase.getInstance(appContext);
        ProductsLocalDataSource productsLocalDataSource = ProductsLocalDataSource.getInstance(
                appExecutors, productsDatabase.productsDao());

        return ProductsRepositoryImpl.getInstance(productsCache, productsRemoteDataSource, productsLocalDataSource);
    }
}
