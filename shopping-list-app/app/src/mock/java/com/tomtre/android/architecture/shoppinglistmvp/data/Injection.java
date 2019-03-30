package com.tomtre.android.architecture.shoppinglistmvp.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tomtre.android.architecture.shoppinglistmvp.data.source.local.ProductsDatabase;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.remote.FakeProductsRemoteDataSource;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepositoryImpl;
import com.tomtre.android.architecture.shoppinglistmvp.util.AppExecutors;

import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;

public class Injection {

    public static ProductsRepository provideProductsRepository(@NonNull Context context) {
        checkNotNull(context);
        Context appContext = context.getApplicationContext();

        FakeProductsRemoteDataSource fakeProductsRemoteDataSource = FakeProductsRemoteDataSource.getInstance();
        AppExecutors appExecutors = AppExecutors.getInstance(Executors.newSingleThreadExecutor(), new AppExecutors.MainThreadExecutor());
        ProductsDatabase productsDatabase = ProductsDatabase.getInstance(appContext);

        return ProductsRepositoryImpl.getInstance(fakeProductsRemoteDataSource, productsDatabase.productsDao(), appExecutors);
    }

}
