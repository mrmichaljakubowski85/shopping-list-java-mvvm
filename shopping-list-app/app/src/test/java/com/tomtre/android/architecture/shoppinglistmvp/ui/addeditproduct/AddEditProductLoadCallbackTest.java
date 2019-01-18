package com.tomtre.android.architecture.shoppinglistmvp.ui.addeditproduct;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createUncheckedProduct;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class AddEditProductLoadCallbackTest {

    private final static Product PRODUCT = createUncheckedProduct();

    @Mock
    private AddEditProductPresenter addEditProductPresenter;

    private AddEditProductLoadCallback addEditProductLoadCallback;

    @Before
    public void setUp() {
        addEditProductLoadCallback = new AddEditProductLoadCallback(addEditProductPresenter);
    }

    @Test
    public void shouldShowProductWhenProductLoaded() {
        //when
        addEditProductLoadCallback.onProductLoaded(PRODUCT);

        //then
        then(addEditProductPresenter).should().showProduct(PRODUCT);
    }

    @Test
    public void shouldSetProductCheckedStateWhenProductLoaded() {
        //when
        addEditProductLoadCallback.onProductLoaded(PRODUCT);

        //then
        then(addEditProductPresenter).should().setProductCheckedState(PRODUCT.isChecked());
    }

    @Test
    public void shouldSetDataIsMissingWhenProductLoaded() {
        //when
        addEditProductLoadCallback.onProductLoaded(PRODUCT);

        //then
        then(addEditProductPresenter).should().setDataIsMissing();
    }

    @Test
    public void shouldShowMissingProductInViewWhenDataNotAvailable() {
        //when
        addEditProductLoadCallback.onDataNotAvailable();

        //then
        then(addEditProductPresenter).should().showMissingProductInView();
    }


}