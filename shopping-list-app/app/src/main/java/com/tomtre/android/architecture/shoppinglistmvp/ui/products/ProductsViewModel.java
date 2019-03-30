package com.tomtre.android.architecture.shoppinglistmvp.ui.products;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.tomtre.android.architecture.shoppinglistmvp.R;
import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.ProductComparator;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.util.RequestCodes;
import com.tomtre.android.architecture.shoppinglistmvp.util.SingleLiveEvent;
import com.tomtre.android.architecture.shoppinglistmvp.util.SnackbarMessage;

import java.text.Collator;
import java.util.List;

import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

public class ProductsViewModel extends AndroidViewModel {

    private final ProductsRepository productsRepository;
    private final LiveData<List<Product>> observableProducts;
    private final MutableLiveData<String> currentFilteringLabel = new MutableLiveData<>();
    private final MutableLiveData<String> noProductsLabel = new MutableLiveData<>();
    private final SnackbarMessage snackbarMessage = new SnackbarMessage();
    private final MutableLiveData<ProductsFilterType> filterChangeEvent = new MutableLiveData<>();
    private final SingleLiveEvent<String> openProductEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<Void> newProductEvent = new SingleLiveEvent<>();
    private final MutableLiveData<Boolean> noProductViewVisible = new MediatorLiveData<>();
    private ProductsFilterType productsFilterType = ProductsFilterType.ALL_PRODUCTS;

    private ProductsViewModel(@NonNull Application application, ProductsRepository productsRepository) {
        super(application);
        this.productsRepository = productsRepository;

        Timber.d("init");

          observableProducts = Transformations.switchMap(filterChangeEvent, input -> {
                    Timber.d("switchMap");
                    final MediatorLiveData<List<Product>> result = new MediatorLiveData<>();
                    result.addSource(productsRepository.getProducts(), products -> {
                        List<Product> filteredProducts = filterProducts(products);
                        if (filteredProducts.isEmpty())
                            noProductViewVisible.setValue(true);
                        else
                            noProductViewVisible.setValue(false);
                        result.setValue(filteredProducts);

                    });
                    return result;
                }
        );
    }

    void setFilterType(ProductsFilterType productsFilterType) {
        Timber.d("setFilterType");
        this.productsFilterType = productsFilterType;
        filterChangeEvent.setValue(productsFilterType);

        switch (productsFilterType) {
            case UNCHECKED_PRODUCTS:
                currentFilteringLabel.setValue(getString(R.string.label_unchecked_products));
                noProductsLabel.setValue(getString(R.string.no_unchecked_products));
                break;
            case CHECKED_PRODUCTS:
                currentFilteringLabel.setValue(getString(R.string.label_checked_products));
                noProductsLabel.setValue(getString(R.string.no_checked_products));
                break;
            case SORTED_BY_PRODUCTS_TITLE:
                currentFilteringLabel.setValue(getString(R.string.label_sorted_by_title_products));
                noProductsLabel.setValue(getString(R.string.no_products));
                break;
            default:
                currentFilteringLabel.setValue(getString(R.string.label_all_products));
                noProductsLabel.setValue(getString(R.string.no_products));
                break;
        }
    }

    void refresh() {
        productsRepository.refreshProducts();
    }

    void handleActivityResult(int requestCode, int resultCode) {
        if (RequestCodes.ADD_PRODUCT == requestCode && Activity.RESULT_OK == resultCode) {
            showSnackbarMessage(R.string.saved_product);
        } else if (RequestCodes.REMOVE_PRODUCT == requestCode && Activity.RESULT_OK == resultCode) {
            showSnackbarMessage(R.string.removed_product);
        }
    }

    void openProductDetails(Product product) {
        checkNotNull(product);
        openProductEvent.setValue(product.getId());
    }

    public void removeCheckedProducts() {
        productsRepository.removeCheckedProducts();
        showSnackbarMessage(R.string.removed_checked_products);
    }

    public void checkProduct(Product product) {
        checkNotNull(product);
        productsRepository.checkProduct(product);
        showSnackbarMessage(R.string.product_marked_as_checked);
    }

    public void uncheckProduct(Product product) {
        checkNotNull(product);
        productsRepository.uncheckProduct(product);
        showSnackbarMessage(R.string.product_marked_as_unchecked);
    }

    void addNewProduct() {
        newProductEvent.call();
    }

    ProductsFilterType getFilterType() {
        return productsFilterType;
    }

    LiveData<List<Product>> getObservableProducts() {
        return observableProducts;
    }

    LiveData<String> getCurrentFilteringLabel() {
        return currentFilteringLabel;
    }

    LiveData<String> getNoProductsLabel() {
        return noProductsLabel;
    }

    SnackbarMessage getSnackbarMessage() {
        return snackbarMessage;
    }

    SingleLiveEvent<String> getOpenProductEvent() {
        return openProductEvent;
    }

    SingleLiveEvent<Void> getNewProductEvent() {
        return newProductEvent;
    }

    LiveData<Boolean> getNoProductViewVisible() {
        return noProductViewVisible;
    }

    private void showSnackbarMessage(int resId) {
        snackbarMessage.setValue(resId);
    }

    private String getString(int resId) {
        return getApplication().getString(resId);
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
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final Application application;
        private final ProductsRepository productsRepository;

        Factory(Application application, ProductsRepository productsRepository) {
            this.application = application;
            this.productsRepository = productsRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ProductsViewModel(application, productsRepository);
        }
    }
}
