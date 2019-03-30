package com.tomtre.android.architecture.shoppinglistmvp.ui.productdetail;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.util.EspressoIdlingResource;

public class ProductDetailLoadCallback  {

    private final ProductDetailPresenter productDetailPresenter;

    ProductDetailLoadCallback(ProductDetailPresenter productDetailPresenter) {
        this.productDetailPresenter = productDetailPresenter;
    }


    public void onProductLoaded(Product product) {
        EspressoIdlingResource.decrementWithIdleCheck();
        productDetailPresenter.setInactiveLoadingIndicatorInView();
        productDetailPresenter.processProduct(product);
    }


    public void onDataNotAvailable() {
        EspressoIdlingResource.decrementWithIdleCheck();
        productDetailPresenter.setInactiveLoadingIndicatorInView();
        productDetailPresenter.showMissingProductInView();
    }
}
