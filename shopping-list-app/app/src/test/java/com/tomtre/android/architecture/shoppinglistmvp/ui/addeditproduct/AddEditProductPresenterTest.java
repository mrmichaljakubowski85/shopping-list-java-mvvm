package com.tomtre.android.architecture.shoppinglistmvp.ui.addeditproduct;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepositoryImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createCheckedProduct;
import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createUncheckedProduct;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class AddEditProductPresenterTest {

    private static final Product PRODUCT = createUncheckedProduct();

    @Mock
    private ProductsRepositoryImpl productsRepositoryImpl;

    @Mock
    private AddEditProductContract.View addEditProductView;

    @Captor
    private ArgumentCaptor<Product> productArgumentCaptor;

    private AddEditProductPresenter addEditProductPresenter;

    @Before
    public void setup() {
        given(addEditProductView.isActive()).willReturn(true);
    }

    @Test
    public void shouldLoadProductWhenPresenterStartsAndLoadDataFromRepositorySetOnTrue() {
        //given
        setUpPresenter(PRODUCT.getId(), true);

        //when
        addEditProductPresenter.start();

        //then
        assertThat(addEditProductPresenter.isDataMissing(), is(true));
        then(productsRepositoryImpl).should().getProduct(eq(PRODUCT.getId()), any(ProductsRepository.LoadProductCallback.class));
    }

    @Test
    public void shouldNotGetProductFromRepositoryWhenPresenterStartsAndLoadDataFromRepositorySetOnFalse() {
        //given
        setUpPresenter(null, false);

        //when
        addEditProductPresenter.start();

        //then
        assertThat(addEditProductPresenter.isDataMissing(), is(false));
        then(productsRepositoryImpl).should(never()).getProduct(anyString(), any(ProductsRepository.LoadProductCallback.class));
    }

    @Test
    public void shouldNotGetProductFromRepositoryWhenPresenterStartsAndProductIdIsNull() {
        //given
        setUpPresenter(null, true);

        //when
        addEditProductPresenter.start();

        //then
        then(productsRepositoryImpl).should(never()).getProduct(anyString(), any(ProductsRepository.LoadProductCallback.class));
    }

    @Test
    public void shouldShowProductInViewWhenShowProduct() {
        //given
        setUpPresenter(PRODUCT.getId(), true);

        //when
        addEditProductPresenter.showProduct(PRODUCT);

        //then
        verifyProductIsShownInView(PRODUCT);
    }

    @Test
    public void shouldSaveProductInRepositoryWhenSaveProduct() {
        //given
        setUpPresenter(null, true);

        //when
        addEditProductPresenter.saveProduct(PRODUCT.getTitle(), PRODUCT.getDescription(), PRODUCT.getQuantity(), PRODUCT.getUnit());

        //then
        then(productsRepositoryImpl).should().saveProduct(productArgumentCaptor.capture());
        Product actualProduct = productArgumentCaptor.getValue();

        assertThat(actualProduct.getTitle(), is(PRODUCT.getTitle()));
        assertThat(actualProduct.getDescription(), is(PRODUCT.getDescription()));
        assertThat(actualProduct.getQuantity(), is(PRODUCT.getQuantity()));
        assertThat(actualProduct.getUnit(), is(PRODUCT.getUnit()));
        assertThat(actualProduct.isChecked(), is(PRODUCT.isChecked()));
    }

    @Test
    public void shouldShowProductListUiWhenSaveNewProduct() {
        //given
        setUpPresenter(null, true);

        //when
        addEditProductPresenter.saveProduct(PRODUCT.getTitle(), PRODUCT.getDescription(), PRODUCT.getQuantity(), PRODUCT.getUnit());

        //then
        then(addEditProductView).should().showProductListUI();
    }

    @Test
    public void shouldShowEmptyProductErrorWhenSaveNewEmptyProduct() {
        //given
        setUpPresenter(null, true);

        //when
        addEditProductPresenter.saveProduct("", PRODUCT.getDescription(), PRODUCT.getQuantity(), PRODUCT.getUnit());

        //then
        then(addEditProductView).should().showEmptyProductError();
    }

    @Test
    public void shouldShowProductListUiWhenUpdateExistingProduct() {
        //given
        setUpPresenter(PRODUCT.getId(), true);

        //when
        addEditProductPresenter.saveProduct(PRODUCT.getTitle(), PRODUCT.getDescription(), PRODUCT.getQuantity(), PRODUCT.getUnit());

        //then
        then(addEditProductView).should().showProductListUI();
    }

    @Test
    public void shouldSaveProductToRepositoryWhenUpdateExistingUncheckedProduct() {
        //given
        Product uncheckedProduct = createUncheckedProduct();
        setUpPresenter(uncheckedProduct.getId(), true);
        addEditProductPresenter.setProductCheckedState(uncheckedProduct.isChecked());

        //when
        addEditProductPresenter.saveProduct(uncheckedProduct.getTitle(), uncheckedProduct.getDescription(), uncheckedProduct.getQuantity(), uncheckedProduct.getUnit());

        //then
        then(productsRepositoryImpl).should().saveProduct(productArgumentCaptor.capture());
        Product actualProduct = productArgumentCaptor.getValue();

        assertThat(actualProduct, is(uncheckedProduct));
    }

    @Test
    public void shouldSaveProductToRepositoryWhenUpdateExistingCheckedProduct() {
        //given
        Product checkedProduct = createCheckedProduct();
        setUpPresenter(checkedProduct.getId(), true);
        addEditProductPresenter.setProductCheckedState(checkedProduct.isChecked());

        //when
        addEditProductPresenter.saveProduct(checkedProduct.getTitle(), checkedProduct.getDescription(), checkedProduct.getQuantity(), checkedProduct.getUnit());

        //then
        then(productsRepositoryImpl).should().saveProduct(productArgumentCaptor.capture());
        Product actualProduct = productArgumentCaptor.getValue();

        assertThat(actualProduct, is(checkedProduct));
    }

    @Test
    public void shouldShowEmptyProductErrorWhenUpdateExistingProductAsEmpty() {
        //given
        setUpPresenter(PRODUCT.getId(), true);
        //when
        addEditProductPresenter.saveProduct("", PRODUCT.getDescription(), PRODUCT.getQuantity(), PRODUCT.getUnit());

        //then
        then(addEditProductView).should().showEmptyProductError();
    }

    @Test
    public void shouldDelegateShowEmptyProductErrorToViewWhenShowEmptyProductError() {
        //given
        setUpPresenter(PRODUCT.getId(), true);
        //when
        addEditProductPresenter.showEmptyProductErrorInView();

        //then
        then(addEditProductView).should().showEmptyProductError();
    }

    @Test
    public void shouldDelegateShowMissingProductToViewWhenShowMissingProduct() {
        //given
        setUpPresenter(PRODUCT.getId(), true);

        //when
        addEditProductPresenter.showMissingProductInView();

        //then
        then(addEditProductView).should().showMissingProduct();
    }

    private void setUpPresenter(String productId, boolean loadDataFromRepository) {
        addEditProductPresenter = new AddEditProductPresenter(productId, productsRepositoryImpl, loadDataFromRepository);
        addEditProductPresenter.takeView(addEditProductView);
    }

    private void verifyProductIsShownInView(Product product) {
        then(addEditProductView).should().setTitle(product.getTitle());
        then(addEditProductView).should().setDescription(product.getDescription());
        then(addEditProductView).should().setQuantity(product.getQuantity());
        then(addEditProductView).should().setUnit(product.getUnit());
    }

}