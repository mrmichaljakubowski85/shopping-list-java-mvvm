package com.tomtre.android.architecture.shoppinglistmvp.ui.addeditproduct;

import android.support.annotation.Nullable;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.util.EspressoIdlingResource;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.isNull;
import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.nonNull;

public class AddEditProductPresenter implements AddEditProductContract.Presenter {

    @Nullable
    private final String productId;
    private final ProductsRepository productsRepository;
    private AddEditProductContract.View addEditProductView;

    //decides if we need to refresh data (configuration change, background/foreground etc)
    private boolean dataMissing;
    private boolean productCheckedState = false;

    AddEditProductPresenter(@Nullable String productId, ProductsRepository productsRepository, boolean loadDataFromRepository) {
        this.productId = productId;
        this.productsRepository = checkNotNull(productsRepository);
        dataMissing = loadDataFromRepository;
    }

    @Override
    public void takeView(AddEditProductContract.View view) {
        addEditProductView = view;
    }

    @Override
    public void start() {
        if (!isNewProduct() && dataMissing)
            loadProduct();
    }

    @Override
    public void dropView() {
        addEditProductView = null;
    }

    void setDataIsMissing() {
        dataMissing = false;
    }

    void setProductCheckedState(boolean productCheckedState) {
        this.productCheckedState = productCheckedState;
    }

    void showProduct(Product product) {
        if (!isViewAvailableAndActive())
            return;
        addEditProductView.setTitle(product.getTitle());
        addEditProductView.setDescription(product.getDescription());
        addEditProductView.setQuantity(product.getQuantity());
        addEditProductView.setUnit(product.getUnit());
    }

    @Override
    public void saveProduct(String title, String description, String quantity, String unit) {
        if (isNewProduct())
            createProduct(title, description, quantity, unit);
        else
            updateProduct(title, description, quantity, unit);
    }

    @Override
    public boolean isDataMissing() {
        return dataMissing;
    }

    void showEmptyProductErrorInView() {
        if (isViewAvailableAndActive())
            addEditProductView.showEmptyProductError();
    }

    void showMissingProductInView() {
        if (isViewAvailableAndActive())
            addEditProductView.showMissingProduct();
    }

    private boolean isNewProduct() {
        return isNull(productId);
    }

    private void loadProduct() {
        EspressoIdlingResource.increment();
//        productsRepository.getProduct(productId, new AddEditProductLoadCallback(this));
    }

    private void createProduct(String title, String description, String quantity, String unit) {
        Product product = new Product(title, description, quantity, unit, productCheckedState);
        if (product.isEmpty()) {
            if (isViewAvailableAndActive())
                addEditProductView.showEmptyProductError();
        } else {
            productsRepository.saveProduct(product);
            if (isViewAvailableAndActive())
                addEditProductView.showProductListUI();
        }
    }

    private void updateProduct(String title, String description, String quantity, String unit) {
        //noinspection ConstantConditions
        Product product = new Product(title, description, quantity, unit, productCheckedState, productId);
        if (product.isEmpty()) {
            if (isViewAvailableAndActive())
                addEditProductView.showEmptyProductError();
        } else {
            productsRepository.saveProduct(product);
            if (isViewAvailableAndActive())
                addEditProductView.showProductListUI();
        }
    }

    private boolean isViewAvailableAndActive() {
        return nonNull(addEditProductView) && addEditProductView.isActive();
    }
}
