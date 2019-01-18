package com.tomtre.android.architecture.shoppinglistmvp.ui.addeditproduct;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.util.EspressoIdlingResource;

public class AddEditProductLoadCallback implements ProductsRepository.LoadProductCallback {

    private final AddEditProductPresenter addEditProductPresenter;

    AddEditProductLoadCallback(AddEditProductPresenter addEditProductPresenter) {
        this.addEditProductPresenter = addEditProductPresenter;
    }

    @Override
    public void onProductLoaded(Product product) {
        EspressoIdlingResource.decrementWithIdleCheck();
        addEditProductPresenter.showProduct(product);
        addEditProductPresenter.setProductCheckedState(product.isChecked());
        addEditProductPresenter.setDataIsMissing();
    }

    @Override
    public void onDataNotAvailable() {
        EspressoIdlingResource.decrementWithIdleCheck();
        addEditProductPresenter.showMissingProductInView();
    }
}
