package com.tomtre.android.architecture.shoppinglistmvp.ui.addeditproduct;

import android.content.Intent;
import android.support.annotation.Nullable;
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
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.tomtre.android.architecture.shoppinglistmvp.util.Matchers.withToolbarTitle;
import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.createUncheckedProduct;
import static com.tomtre.android.architecture.shoppinglistmvp.util.UiTestUtils.getCurrentActivity;
import static com.tomtre.android.architecture.shoppinglistmvp.util.UiTestUtils.rotateOrientation;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddEditProductScreenTest {

    private static final Product UNCHECKED_PRODUCT = createUncheckedProduct();
    @Rule
    public ActivityTestRule<AddEditProductActivity> addEditProductActivityActivityTestRule =
            new ActivityTestRule<>(AddEditProductActivity.class, false, false);


    @Before
    public void setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    public void cleanUp() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    @Test
    public void shouldNotSaveWhenInputInvalidProductTitle() {
        launchActivityAsNewProduct();

        //add invalid product title
        onView(withId(R.id.et_add_edit_product_title)).perform(clearText());
        //try to save the product
        onView(withId(R.id.fab_done_product)).perform(click());

        //verify the screen is still displayed
        onView(withId(R.id.et_add_edit_product_title)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldPersistProductTitleWhenOrientationChange() {
        launchActivityAsEditProduct(UNCHECKED_PRODUCT);

        //change the product title (but don't save)
        onView(withId(R.id.et_add_edit_product_title)).perform(replaceText("New title"), closeSoftKeyboard());

        //rotate the screen
        rotateOrientation(getCurrentActivity());

        //verify the product title is restored
        onView(withId(R.id.et_add_edit_product_title)).check(matches(withText("New title")));
    }

    @Test
    public void shouldPersistToolbarTitleWhenOrientationChangeWithNewTask() {
        launchActivityAsNewProduct();

        //verify the toolbar shows correct title
        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(R.string.add_product)));

        //rotate the screen
        rotateOrientation(addEditProductActivityActivityTestRule.getActivity());

        //verify the toolbar shows correct title
        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(R.string.add_product)));
    }

    @Test
    public void shouldPersistToolbarTitleWhenOrientationChangeWithEditTask() {
        launchActivityAsEditProduct(UNCHECKED_PRODUCT);

        //verify the toolbar shows correct title
        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(R.string.edit_product)));

        //rotate the screen
        rotateOrientation(addEditProductActivityActivityTestRule.getActivity());

        //verify the toolbar shows correct title
        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(R.string.edit_product)));
    }

    @Test
    public void shouldShowNoProductWhenLaunchWithInvalidProductId() {
        launchActivity("");

        //verify "no product" message is shown
        onView(withText(R.string.no_product)).check(matches(isDisplayed()));
    }

    private void launchActivityAsNewProduct() {
        launchActivity(null);
    }

    private void launchActivityAsEditProduct(Product product) {
        stubProduct(product);
        launchActivity(product.getId());
    }

    private void stubProduct(Product product) {
        ProductsRepository productsRepository = Injection.provideProductsRepository(InstrumentationRegistry.getTargetContext());
        productsRepository.removeAllProducts();
        productsRepository.saveProduct(product);
    }

    private void launchActivity(@Nullable String productId) {
        Intent startIntent = new Intent();
        startIntent.putExtra(AddEditProductActivity.KEY_EDIT_PRODUCT_ID, productId);
        addEditProductActivityActivityTestRule.launchActivity(startIntent);
    }

}
