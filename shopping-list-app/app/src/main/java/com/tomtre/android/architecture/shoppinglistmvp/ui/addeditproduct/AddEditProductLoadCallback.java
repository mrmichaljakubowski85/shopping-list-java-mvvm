package com.tomtre.android.architecture.shoppinglistmvp.ui.addeditproduct;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.util.EspressoIdlingResource;

public class AddEditProductLoadCallback {

    private final AddEditProductPresenter addEditProductPresenter;

    AddEditProductLoadCallback(AddEditProductPresenter addEditProductPresenter) {
        this.addEditProductPresenter = addEditProductPresenter;
    }


    public void onProductLoaded(Product product) {
        EspressoIdlingResource.decrementWithIdleCheck();
        addEditProductPresenter.showProduct(product);
        addEditProductPresenter.setProductCheckedState(product.isChecked());
        addEditProductPresenter.setDataIsMissing();
    }


    public void onDataNotAvailable() {
        EspressoIdlingResource.decrementWithIdleCheck();
        addEditProductPresenter.showMissingProductInView();
    }
}
