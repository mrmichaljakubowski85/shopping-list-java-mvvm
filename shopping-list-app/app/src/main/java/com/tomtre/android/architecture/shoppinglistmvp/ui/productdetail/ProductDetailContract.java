package com.tomtre.android.architecture.shoppinglistmvp.ui.productdetail;

import com.tomtre.android.architecture.shoppinglistmvp.base.BasePresenter;

public interface ProductDetailContract {

    interface View {

        void setLoadingIndicator(boolean active);

        void showMissingProduct();

        void showTitle(String title);

        void showDescription(String description);

        void hideDescription();

        void showQuantity(String quantity);

        void hideQuantity();

        void showUnit(String unit);

        void hideUnit();

        void showCheckedStatus(boolean checked);

        void showProductMarkedAsChecked();

        void showProductMarkedAsUnchecked();

        void showEditProductUI(String productId);

        void showProductRemoved();

        void showProductEdited();

        boolean isActive();
    }

    interface Presenter extends BasePresenter<View> {

        void editProduct();

        void removeProduct();

        void checkProduct();

        void uncheckProduct();

        void activityResult(int requestCode, int resultCode);
    }
}
