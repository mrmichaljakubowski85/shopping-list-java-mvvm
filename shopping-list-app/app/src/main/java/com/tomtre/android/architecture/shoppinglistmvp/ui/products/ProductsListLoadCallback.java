package com.tomtre.android.architecture.shoppinglistmvp.ui.products;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.util.EspressoIdlingResource;

import java.util.List;

public class ProductsListLoadCallback implements ProductsRepository.LoadProductListCallback {

    private final ProductsPresenter productsPresenter;
    private final boolean showLoadingIndicator;

    ProductsListLoadCallback(ProductsPresenter productsPresenter, boolean showLoadingIndicator) {
        this.productsPresenter = productsPresenter;
        this.showLoadingIndicator = showLoadingIndicator;
    }

    @Override
    public void onProductsLoaded(List<Product> products) {
        EspressoIdlingResource.decrementWithIdleCheck();
        if (showLoadingIndicator)
            productsPresenter.setInactiveLoadingIndicatorInView();
        productsPresenter.processProducts(products);

    }

    @Override
    public void onDataNotAvailable() {
        EspressoIdlingResource.decrementWithIdleCheck();
        if (showLoadingIndicator)
            productsPresenter.setInactiveLoadingIndicatorInView();
        productsPresenter.showLoadingProductsErrorInView();
    }
}
