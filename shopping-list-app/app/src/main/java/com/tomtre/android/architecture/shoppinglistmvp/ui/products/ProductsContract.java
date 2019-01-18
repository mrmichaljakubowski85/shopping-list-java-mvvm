package com.tomtre.android.architecture.shoppinglistmvp.ui.products;

import com.tomtre.android.architecture.shoppinglistmvp.base.BasePresenter;
import com.tomtre.android.architecture.shoppinglistmvp.data.Product;

import java.util.List;

public interface ProductsContract {

    interface View {

        boolean isActive();

        void setLoadingIndicator(boolean active);

        void showNoProducts();

        void showNoCheckedProducts();

        void showNoUncheckedProducts();

        void showSuccessfullySavedMessage();

        void showLoadingProductsError();

        void showProducts(List<Product> products);

        void showAllFilterLabel();

        void showCheckedFilterLabel();

        void showUncheckedFilterLabel();

        void showSortedByTitleFilterLabel();

        void showProductDetailsUI(String productId);

        void showProductMarkedAsChecked();

        void showProductMarkedAsUnchecked();

        void showAddProductUI();

        void showRemovedCheckedProducts();

        void showRemovedProduct();
    }

    interface Presenter extends BasePresenter<View> {

        void activityResult(int requestCode, int resultCode);

        void loadProducts(boolean forceUpdate);

        void openProductDetails(Product product);

        void checkProduct(Product product);

        void uncheckProduct(Product product);

        void addNewProduct();

        void removeCheckedProducts();

        void setFilterType(ProductsFilterType productsFilterType);

        ProductsFilterType getFilterType();
    }

}
