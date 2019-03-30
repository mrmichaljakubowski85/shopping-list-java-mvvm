package com.tomtre.android.architecture.shoppinglistmvp.ui.productdetail;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.util.EspressoIdlingResource;
import com.tomtre.android.architecture.shoppinglistmvp.util.RequestCodes;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.nonNull;

public class ProductDetailPresenter implements ProductDetailContract.Presenter {

    @Nullable
    private final String productId;
    private final ProductsRepository productsRepository;
    private ProductDetailContract.View productDetailView;

    ProductDetailPresenter(@Nullable String productId, ProductsRepository productsRepository) {
        this.productId = productId;
        this.productsRepository = checkNotNull(productsRepository);
    }

    @Override
    public void takeView(ProductDetailContract.View view) {
        productDetailView = view;
    }

    @Override
    public void start() {
        loadProduct();
    }

    @Override
    public void dropView() {
        productDetailView = null;
    }

    private void loadProduct() {
        if (isNullOrEmpty(productId)) {
            if (isViewAvailableAndActive())
                productDetailView.showMissingProduct();
            return;
        }
        if (isViewAvailableAndActive())
            productDetailView.setLoadingIndicator(true);
        EspressoIdlingResource.increment();
//        productsRepository.getProduct(productId, new ProductDetailLoadCallback(this));
    }

    void showMissingProductInView() {
        if (isViewAvailableAndActive())
            productDetailView.showMissingProduct();
    }

    void setInactiveLoadingIndicatorInView() {
        if (isViewAvailableAndActive())
            productDetailView.setLoadingIndicator(false);
    }

    void processProduct(Product product) {
        showProduct(product);
    }

    private void showProduct(Product product) {
        if (!isViewAvailableAndActive())
            return;

        productDetailView.showTitle(product.getTitle());

        String description = product.getDescription();
        if (isNullOrEmpty(description))
            productDetailView.hideDescription();
        else
            productDetailView.showDescription(description);

        String quantity = product.getQuantity();
        if (isNullOrEmpty(quantity))
            productDetailView.hideQuantity();
        else
            productDetailView.showQuantity(quantity);

        String unit = product.getUnit();
        if (isNullOrEmpty(unit))
            productDetailView.hideUnit();
        else
            productDetailView.showUnit(unit);

        productDetailView.showCheckedStatus(product.isChecked());
    }

    @Override
    public void removeProduct() {
        if (isNullOrEmpty(productId)) {
            if (isViewAvailableAndActive())
                productDetailView.showMissingProduct();
        } else {
            productsRepository.removeProduct(productId);
            if (isViewAvailableAndActive())
                productDetailView.showProductRemoved();
        }
    }

    @Override
    public void checkProduct() {
        if (isNullOrEmpty(productId)) {
            if (isViewAvailableAndActive())
                productDetailView.showMissingProduct();
        } else {
            productsRepository.checkProduct(productId);
            if (isViewAvailableAndActive())
                productDetailView.showProductMarkedAsChecked();
        }
    }

    @Override
    public void uncheckProduct() {
        if (isNullOrEmpty(productId)) {
            if (isViewAvailableAndActive())
                productDetailView.showMissingProduct();
        } else {
            productsRepository.uncheckProduct(productId);
            productDetailView.showProductMarkedAsUnchecked();
        }
    }

    @Override
    public void editProduct() {
        if (!isViewAvailableAndActive())
            return;
        if (isNullOrEmpty(productId)) {
            productDetailView.showMissingProduct();
        } else {
            productDetailView.showEditProductUI(productId);
        }
    }

    @Override
    public void activityResult(int requestCode, int resultCode) {
        if (!isViewAvailableAndActive())
            return;
        if (RequestCodes.EDIT_PRODUCT == requestCode && Activity.RESULT_OK == resultCode)
            productDetailView.showProductEdited();
    }

    private boolean isViewAvailableAndActive() {
        return nonNull(productDetailView) && productDetailView.isActive();
    }

}





