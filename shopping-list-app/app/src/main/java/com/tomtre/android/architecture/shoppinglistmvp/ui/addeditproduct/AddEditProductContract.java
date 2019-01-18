package com.tomtre.android.architecture.shoppinglistmvp.ui.addeditproduct;

import com.tomtre.android.architecture.shoppinglistmvp.base.BasePresenter;

public interface AddEditProductContract {

    interface View  {

        void setTitle(String title);

        void setDescription(String description);

        void setQuantity(String quantity);

        void setUnit(String unit);

        void showProductListUI();

        void showEmptyProductError();

        void showMissingProduct();

        boolean isActive();
    }

    interface Presenter extends BasePresenter<View> {

        void saveProduct(String title, String description, String quantity, String unit);

        boolean isDataMissing();
    }
}
