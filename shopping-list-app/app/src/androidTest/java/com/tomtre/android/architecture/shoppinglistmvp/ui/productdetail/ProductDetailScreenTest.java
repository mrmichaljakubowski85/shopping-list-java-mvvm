package com.tomtre.android.architecture.shoppinglistmvp.ui.productdetail;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.tomtre.android.architecture.shoppinglistmvp.R;
import com.tomtre.android.architecture.shoppinglistmvp.data.Injection;
import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.util.EspressoIdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createCheckedProduct;
import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createUncheckedProduct;
import static com.tomtre.android.architecture.shoppinglistmvp.util.UiTestUtils.rotateOrientation;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProductDetailScreenTest {

    private static Product UNCHECKED_PRODUCT = createUncheckedProduct();
    private static Product CHECKED_PRODUCT = createCheckedProduct();

    @Rule
    public ActivityTestRule<ProductDetailActivity> productDetailActivityActivityTestRule =
            new ActivityTestRule<>(ProductDetailActivity.class, true, false);


    @Before
    public void setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    public void cleanUp() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    @Test
    public void shouldShowUncheckedProductOnScreen() {
        launchActivityWithUncheckedProduct();

        //verify the product information are shown
        onView(withId(R.id.tv_product_detail_title)).check(matches(withText(UNCHECKED_PRODUCT.getTitle())));
        onView(withId(R.id.tv_product_detail_description)).check(matches(withText(UNCHECKED_PRODUCT.getDescription())));
        onView(withId(R.id.tv_product_detail_quantity)).check(matches(withText(UNCHECKED_PRODUCT.getQuantity())));
        onView(withId(R.id.tv_product_detail_unit)).check(matches(withText(UNCHECKED_PRODUCT.getUnit())));
        onView(withId(R.id.tv_product_detail_description)).check(matches(withText(UNCHECKED_PRODUCT.getDescription())));
        onView(withId(R.id.cb_product_detail_checked)).check(matches(isNotChecked()));
    }

    @Test
    public void shouldShowCheckedProductOnScreen() {
        launchActivityWithCheckedProduct();

        //verify the product information are shown
        onView(withId(R.id.tv_product_detail_title)).check(matches(withText(UNCHECKED_PRODUCT.getTitle())));
        onView(withId(R.id.tv_product_detail_description)).check(matches(withText(UNCHECKED_PRODUCT.getDescription())));
        onView(withId(R.id.tv_product_detail_quantity)).check(matches(withText(UNCHECKED_PRODUCT.getQuantity())));
        onView(withId(R.id.tv_product_detail_unit)).check(matches(withText(UNCHECKED_PRODUCT.getUnit())));
        onView(withId(R.id.tv_product_detail_description)).check(matches(withText(UNCHECKED_PRODUCT.getDescription())));
        onView(withId(R.id.cb_product_detail_checked)).check(matches(isChecked()));
    }

    @Test
    public void shouldShowProductOnScreenWhenOrientationChange() {
        launchActivityWithUncheckedProduct();

        //rotate the screen
        rotateOrientation(productDetailActivityActivityTestRule.getActivity());

        //verify the product information are shown
        onView(withId(R.id.tv_product_detail_title)).check(matches(withText(UNCHECKED_PRODUCT.getTitle())));
        onView(withId(R.id.tv_product_detail_description)).check(matches(withText(UNCHECKED_PRODUCT.getDescription())));
        onView(withId(R.id.tv_product_detail_quantity)).check(matches(withText(UNCHECKED_PRODUCT.getQuantity())));
        onView(withId(R.id.tv_product_detail_unit)).check(matches(withText(UNCHECKED_PRODUCT.getUnit())));
        onView(withId(R.id.tv_product_detail_description)).check(matches(withText(UNCHECKED_PRODUCT.getDescription())));
        onView(withId(R.id.cb_product_detail_checked)).check(matches(isNotChecked()));
    }

    @Test
    public void shouldPersistUncheckedValueWhenOrientationChange() {
        launchActivityWithCheckedProduct();

        //uncheck the product
        onView(withId(R.id.cb_product_detail_checked)).perform(click());
        //verify the product is unchecked
        onView(withId(R.id.cb_product_detail_checked)).check(matches(isNotChecked()));

        rotateOrientation(productDetailActivityActivityTestRule.getActivity());
        //verify the product is still unchecked
        onView(withId(R.id.cb_product_detail_checked)).check(matches(isNotChecked()));
    }

    @Test
    public void shouldPersistCheckedValueWhenOrientationChange() {
        launchActivityWithUncheckedProduct();

        //check the product
        onView(withId(R.id.cb_product_detail_checked)).perform(click());
        //verify the product is checked
        onView(withId(R.id.cb_product_detail_checked)).check(matches(isChecked()));

        rotateOrientation(productDetailActivityActivityTestRule.getActivity());
        //verify the product is still checked
        onView(withId(R.id.cb_product_detail_checked)).check(matches(isChecked()));
    }

    @Test
    public void shouldShowNoProductWhenLaunchWithInvalidProductId() {
        Intent startIntent = new Intent();
        startIntent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, "");
        productDetailActivityActivityTestRule.launchActivity(startIntent);

        //verify "no product" message is shown
        onView(withText(R.string.no_product)).check(matches(isDisplayed()));
    }

    private void launchActivityWithUncheckedProduct() {
        stubProduct(UNCHECKED_PRODUCT);
        launchActivityProduct(UNCHECKED_PRODUCT.getId());
    }

    private void launchActivityWithCheckedProduct() {
        stubProduct(CHECKED_PRODUCT);
        launchActivityProduct(CHECKED_PRODUCT.getId());
    }

    private void stubProduct(Product product) {
        ProductsRepository productsRepository = Injection.provideProductsRepository(InstrumentationRegistry.getTargetContext());
        productsRepository.removeAllProducts();
        productsRepository.saveProduct(product);
    }

    private void launchActivityProduct(String productId) {
        Intent startIntent = new Intent();
        startIntent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, productId);
        productDetailActivityActivityTestRule.launchActivity(startIntent);
    }
}
