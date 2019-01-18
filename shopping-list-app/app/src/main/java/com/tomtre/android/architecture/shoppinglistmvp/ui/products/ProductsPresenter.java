package com.tomtre.android.architecture.shoppinglistmvp.ui.products;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.ProductComparator;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.util.EspressoIdlingResource;
import com.tomtre.android.architecture.shoppinglistmvp.util.RequestCodes;

import java.text.Collator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.nonNull;

public class ProductsPresenter implements ProductsContract.Presenter {

    private final ProductsRepository productsRepository;
    private ProductsContract.View productsView;
    private ProductsFilterType productsFilterType = ProductsFilterType.ALL_PRODUCTS;

    ProductsPresenter(@NonNull ProductsRepository productsRepository) {
        this.productsRepository = checkNotNull(productsRepository);
    }

    @Override
    public void takeView(ProductsContract.View view) {
        productsView = view;
    }

    @Override
    public void start() {
        loadProducts(false);
    }

    @Override
    public void dropView() {
        productsView = null;
    }

    @Override
    public void loadProducts(boolean forceUpdate) {
        loadProducts(forceUpdate, true);
    }

    void processProducts(List<Product> products) {
        if (!isViewAvailableAndActive())
            return;
        List<Product> filteredProductsToShow = filterProducts(products);
        if (filteredProductsToShow.isEmpty()) {
            processEmptyProducts();
        } else {
            showFilterLabel();
            productsView.showProducts(filteredProductsToShow);
        }
    }

    void showLoadingProductsErrorInView() {
        if (isViewAvailableAndActive())
            productsView.showLoadingProductsError();
    }

    void setInactiveLoadingIndicatorInView() {
        if (isViewAvailableAndActive())
            productsView.setLoadingIndicator(false);
    }

    @Override
    public void openProductDetails(Product product) {
        checkNotNull(product);
        if (isViewAvailableAndActive())
            productsView.showProductDetailsUI(product.getId());
    }

    @Override
    public void checkProduct(Product product) {
        checkNotNull(product);
        productsRepository.checkProduct(product);
        if (isViewAvailableAndActive())
            productsView.showProductMarkedAsChecked();
        loadProducts(false, false);
    }

    @Override
    public void uncheckProduct(Product product) {
        checkNotNull(product);
        productsRepository.uncheckProduct(product);
        if (isViewAvailableAndActive())
            productsView.showProductMarkedAsUnchecked();
        loadProducts(false, false);
    }

    @Override
    public void addNewProduct() {
        if (isViewAvailableAndActive())
            productsView.showAddProductUI();
    }

    @Override
    public void removeCheckedProducts() {
        productsRepository.removeCheckedProducts();
        if (isViewAvailableAndActive())
            productsView.showRemovedCheckedProducts();
        loadProducts(false, false);
    }

    @Override
    public ProductsFilterType getFilterType() {
        return productsFilterType;
    }

    @Override
    public void setFilterType(ProductsFilterType productsFilterType) {
        this.productsFilterType = productsFilterType;
    }

    @Override
    public void activityResult(int requestCode, int resultCode) {
        if (!isViewAvailableAndActive())
            return;
        if (RequestCodes.ADD_PRODUCT == requestCode && Activity.RESULT_OK == resultCode) {
            productsView.showSuccessfullySavedMessage();
        } else if (RequestCodes.REMOVE_PRODUCT == requestCode && Activity.RESULT_OK == resultCode) {
            productsView.showRemovedProduct();
        }
    }

    private void loadProducts(boolean forceUpdate, boolean showLoadingIndicator) {
        if (showLoadingIndicator) {
            if (isViewAvailableAndActive())
                productsView.setLoadingIndicator(true);
        }
        if (forceUpdate)
            productsRepository.forceToLoadFromRemoteNextCall();
        EspressoIdlingResource.increment();
        productsRepository.getProducts(new ProductsListLoadCallback(this, showLoadingIndicator));
    }

    @SuppressWarnings("Guava")
    private List<Product> filterProducts(List<Product> products) {
        switch (productsFilterType) {
            case UNCHECKED_PRODUCTS:
                return FluentIterable.from(products)
                        .filter(product -> !product.isChecked())
                        .toList();
            case CHECKED_PRODUCTS:
                return FluentIterable.from(products)
                        .filter(Product::isChecked)
                        .toList();
            case SORTED_BY_PRODUCTS_TITLE:
                return FluentIterable.from(products)
                        .toSortedList(new ProductComparator(Collator.getInstance()));
            default:
                return ImmutableList.copyOf(products);
        }
    }

    private void processEmptyProducts() {
        switch (productsFilterType) {
            case UNCHECKED_PRODUCTS:
                productsView.showNoUncheckedProducts();
                break;
            case CHECKED_PRODUCTS:
                productsView.showNoCheckedProducts();
                break;
            default:
                productsView.showNoProducts();
                break;
        }
    }

    private void showFilterLabel() {
        switch (productsFilterType) {
            case UNCHECKED_PRODUCTS:
                productsView.showUncheckedFilterLabel();
                break;
            case CHECKED_PRODUCTS:
                productsView.showCheckedFilterLabel();
                break;
            case SORTED_BY_PRODUCTS_TITLE:
                productsView.showSortedByTitleFilterLabel();
                break;
            default:
                productsView.showAllFilterLabel();
                break;
        }
    }

    private boolean isViewAvailableAndActive() {
        return nonNull(productsView) && productsView.isActive();
    }
}
