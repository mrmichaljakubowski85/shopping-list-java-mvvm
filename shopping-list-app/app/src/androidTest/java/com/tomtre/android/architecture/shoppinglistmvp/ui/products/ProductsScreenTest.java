package com.tomtre.android.architecture.shoppinglistmvp.ui.products;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tomtre.android.architecture.shoppinglistmvp.R;
import com.tomtre.android.architecture.shoppinglistmvp.data.Injection;
import com.tomtre.android.architecture.shoppinglistmvp.data.source.repository.ProductsRepository;
import com.tomtre.android.architecture.shoppinglistmvp.util.EspressoIdlingResource;
import com.tomtre.android.architecture.shoppinglistmvp.util.UiTestUtils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.tomtre.android.architecture.shoppinglistmvp.util.Assertions.isNotDisplayed;
import static com.tomtre.android.architecture.shoppinglistmvp.util.Matchers.hasItemAtPosition;
import static com.tomtre.android.architecture.shoppinglistmvp.util.Matchers.withItemText;
import static com.tomtre.android.architecture.shoppinglistmvp.util.UiTestUtils.getCurrentActivity;
import static com.tomtre.android.architecture.shoppinglistmvp.util.UiTestUtils.rotateOrientation;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProductsScreenTest {

    private final static String TITLE = "Title 1";
    private final static String DESCRIPTION = "Description 1";
    private final static String QUANTITY = "1";
    private final static String UNIT = "Unit1";

    private final static String TITLE_2 = "Title 2";
    private final static String DESCRIPTION_2 = "Description 2";
    private final static String QUANTITY_2 = "2";
    private final static String UNIT_2 = "Unit2";


    @Rule
    public ActivityTestRule<ProductsActivity> productsActivityTestRule =
            new ActivityTestRule<ProductsActivity>(ProductsActivity.class) {

                //deletes all products before each test to avoid
                //the need to scroll through the list fo find a product
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    //can't be used in @Before because of a race condition
                    ProductsRepository productsRepository = Injection.provideProductsRepository(InstrumentationRegistry.getTargetContext());
                    productsRepository.removeAllProducts();
                }
            };

    @Before
    public void setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    public void cleanUp() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    @Test
    public void shouldDisplayProduct() {
        addUncheckedProduct();

        verifyIfProductIsDisplayed(TITLE);
    }

    @Test
    public void shouldShowProductAsCheckedWhenClickProductCheckBox() {
        addUncheckedProduct();

        //mark product as checked
        clickCheckBoxForProduct(TITLE);

        //verify product is show as checked
        viewAllProducts();
        verifyIfProductIsDisplayed(TITLE);

        viewCheckedProducts();
        verifyIfProductIsDisplayed(TITLE);

        viewUncheckedProducts();
        verifyIfProductIsNotDisplayed(TITLE);
    }

    @Test
    public void shouldShowProductAsUncheckedWhenClickProductCheckBox() {
        addUncheckedProduct();

        //mark product as checked
        clickCheckBoxForProduct(TITLE);
        //mark product as unchecked
        clickCheckBoxForProduct(TITLE);

        //verify product is show as unchecked
        viewAllProducts();
        verifyIfProductIsDisplayed(TITLE);

        viewCheckedProducts();
        verifyIfProductIsNotDisplayed(TITLE);

        viewUncheckedProducts();
        verifyIfProductIsDisplayed(TITLE);
    }

    @Test
    public void shouldOpenAddProductScreenWhenClickAddProductButton() {
        //click on the add product button
        onView(withId(R.id.fab_add_product)).perform(click());

        //verify add product screen is displayed
        onView(withId(R.id.et_add_edit_product_title)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldOpenProductDetailScreenWhenClickProduct() {
        addUncheckedProduct();

        //click on the product on the list
        onView(withText(TITLE)).perform(click());

        //verify product detail screen is displayed
        onView(withId(R.id.tv_product_detail_title)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldEditProduct() {
        //add product
        addUncheckedProduct();

        //click on the product on the list
        onView(withText(TITLE)).perform(click());

        //click on the edit button (FAB)
        onView(withId(R.id.fab_edit_product)).perform(click());

        //edit product title, description, quantity and unit
        onView(withId(R.id.et_add_edit_product_title)).perform(replaceText(TITLE_2), closeSoftKeyboard());
        onView(withId(R.id.et_add_edit_product_description)).perform(replaceText(DESCRIPTION_2), closeSoftKeyboard());
        onView(withId(R.id.et_add_edit_product_quantity)).perform(replaceText(QUANTITY_2), closeSoftKeyboard());
        onView(withId(R.id.et_add_edit_product_unit)).perform(replaceText(UNIT_2), closeSoftKeyboard());
        //save the product
        onView(withId(R.id.fab_done_product)).perform(click());


        //verify edited product is displayed
        verifyIfProductIsDisplayed(TITLE_2);
        //verify previous product is not displayed
        verifyIfProductIsNotDisplayed(TITLE);

    }

    @Test
    public void shouldShowAllProducts() {
        addUncheckedProduct();
        addUncheckedProduct2();

        //verify all products are shown
        viewAllProducts();
        verifyIfProductIsDisplayed(TITLE);
        verifyIfProductIsDisplayed(TITLE_2);
    }

    @Test
    public void shouldShowCheckedProducts() {
        addCheckedProduct();
        addCheckedProduct2();

        //verify all products are shown
        viewCheckedProducts();
        verifyIfProductIsDisplayed(TITLE);
        verifyIfProductIsDisplayed(TITLE_2);
    }

    @Test
    public void shouldShowUncheckedProducts() {
        addUncheckedProduct();
        addUncheckedProduct2();

        //verify all products are shown
        viewUncheckedProducts();
        verifyIfProductIsDisplayed(TITLE);
        verifyIfProductIsDisplayed(TITLE_2);
    }

    @Test
    public void shouldShowSortedProducts() {
        addUncheckedProduct2();
        addUncheckedProduct();

        //verify all products are shown sorted
        viewSortedByTitleProducts();
        onView(hasItemAtPosition(hasDescendant(withText(TITLE)), 0)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText(TITLE_2)), 1)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldRemoveCheckedProducts() {
        addCheckedProduct();
        addCheckedProduct2();

        //click remove checked products in menu
        openActionBarOverflowOrOptionsMenu(getTargetContext());
        onView(withText(R.string.menu_remove_checked)).perform(click());

        //verify checked products are not shown
        verifyIfProductIsNotDisplayed(TITLE);
        verifyIfProductIsNotDisplayed(TITLE_2);
    }

    @Test
    public void shouldRemoveProductWhenClickRemoveProductOnProductDetailScreen() {
        addUncheckedProduct();

        //open product detail screen
        onView(withText(TITLE)).perform(click());

        //click remove product in menu
        onView(withId(R.id.menu_remove_product)).perform(click());

        //verify product was deleted
        verifyIfProductIsNotDisplayed(TITLE);

    }


    @Test
    public void shouldShowCheckedProductOnListWhenMarkProductAsCheckedOnDetailScreen() {
        addUncheckedProduct();

        //open product detail screen
        onView(withText(TITLE)).perform(click());

        //click on the checkbox in product detail screen
        onView(withId(R.id.cb_product_detail_checked)).perform(click());

        //click on the navigation up button to go back to the list
        clickNavigateUpButton();

        //verify the product is marked as checked
        onView(hasCheckBoxWithProductTitle(TITLE)).check(matches(isChecked()));
    }

    @Test
    public void shouldShowUncheckedProductOnListWhenMarkProductAsUncheckedOnDetailScreen() {
        addCheckedProduct();

        //open product detail screen
        onView(withText(TITLE)).perform(click());

        //click on the checkbox in product detail screen
        onView(withId(R.id.cb_product_detail_checked)).perform(click());

        //click on the navigation up button to go back to the list
        clickNavigateUpButton();

        //verify the product is marked as unchecked
        onView(hasCheckBoxWithProductTitle(TITLE)).check(matches(not(isChecked())));
    }

    @Test
    public void shouldShowUncheckedProductOnListWhenMarkProductAsCheckedAndUncheckedOnDetailScreen() {
        addUncheckedProduct();

        //open product detail screen
        onView(withText(TITLE)).perform(click());

        //click to check product
        onView(withId(R.id.cb_product_detail_checked)).perform(click());
        //click to uncheck product
        onView(withId(R.id.cb_product_detail_checked)).perform(click());

        //click on the navigation up button to go back to the list
        clickNavigateUpButton();

        //verify the product is marked as unchecked
        onView(hasCheckBoxWithProductTitle(TITLE)).check(matches(not(isChecked())));
    }

    @Test
    public void shouldShowCheckedProductOnListWhenMarkProductAsUncheckedAndCheckedOnDetailScreen() {
        addCheckedProduct();

        //open product detail screen
        onView(withText(TITLE)).perform(click());

        //click to check product
        onView(withId(R.id.cb_product_detail_checked)).perform(click());
        //click to uncheck product
        onView(withId(R.id.cb_product_detail_checked)).perform(click());

        //click on the navigation up button to go back to the list
        clickNavigateUpButton();

        //verify the product is marked as checked
        onView(hasCheckBoxWithProductTitle(TITLE)).check(matches(isChecked()));
    }

    @Test
    public void shouldPersistFilterAsCheckedWhenOrientationChange() {
        addUncheckedProduct();

        //switch to checked products
        viewCheckedProducts();

        //verify the product is not shown
        verifyIfProductIsNotDisplayed(TITLE);

        //rote the screen
        rotateOrientation(productsActivityTestRule.getActivity());

        //then nothing changes
        verifyIfProductIsNotDisplayed(TITLE);
    }

    @Test
    public void shouldPersistFilterAsUncheckedWhenOrientationChange() {
        addCheckedProduct();

        //switch to unchecked products
        viewUncheckedProducts();

        //verify the product is not shown
        verifyIfProductIsNotDisplayed(TITLE);

        //rotate the screen
        rotateOrientation(productsActivityTestRule.getActivity());

        //then nothing changes
        verifyIfProductIsNotDisplayed(TITLE);
    }

    @Test
    public void shouldPersistFilterAsSortedByTitleWhenOrientationChange() {
        addUncheckedProduct2();
        addUncheckedProduct();

        //switch to sorted by title products
        viewSortedByTitleProducts();

        //then all products are shown sorted
        onView(hasItemAtPosition(hasDescendant(withText(TITLE)), 0)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText(TITLE_2)), 1)).check(matches(isDisplayed()));

        //rotate the screen
        rotateOrientation(productsActivityTestRule.getActivity());

        //then nothing changes
        onView(hasItemAtPosition(hasDescendant(withText(TITLE)), 0)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText(TITLE_2)), 1)).check(matches(isDisplayed()));
    }

    @Test
    // blinking cursor after rotation breaks this in API 19
    @SdkSuppress(minSdkVersion = 21)
    public void shouldNotCreateDuplicateProductsWhenOrientationChangeDuringProductEdit() {
        addUncheckedProduct();

        //open the product in detail view
        onView(withText(TITLE)).perform(click());

        // click on the edit product button (FAB)
        onView(withId(R.id.fab_edit_product)).perform(click());

        // rotate the screen
        UiTestUtils.rotateOrientation(getCurrentActivity());

        // change the product title, description, quantity, unit
        onView(withId(R.id.et_add_edit_product_title)).perform(replaceText(TITLE_2), closeSoftKeyboard());
        onView(withId(R.id.et_add_edit_product_description)).perform(replaceText(DESCRIPTION_2), closeSoftKeyboard());
        onView(withId(R.id.et_add_edit_product_quantity)).perform(replaceText(QUANTITY_2), closeSoftKeyboard());
        onView(withId(R.id.et_add_edit_product_unit)).perform(replaceText(UNIT_2), closeSoftKeyboard());

        //save the product
        onView(withId(R.id.fab_done_product)).perform(click());

        //verify the product is displayed on the screen
        verifyIfProductIsDisplayed(TITLE_2);

        //verify the previous product is not displayed
        verifyIfProductIsNotDisplayed(TITLE);
    }


    /* helper methods */
    private void verifyIfProductIsDisplayed(String title) {
        onView(withItemText(title)).check(matches(isDisplayed()));
    }

    private void verifyIfProductIsNotDisplayed(String title) {
        onView(withItemText(title)).check(isNotDisplayed());
    }

    private void addUncheckedProduct() {
        addUncheckedProduct(TITLE, DESCRIPTION, QUANTITY, UNIT);
    }

    private void addCheckedProduct() {
        addUncheckedProduct(TITLE, DESCRIPTION, QUANTITY, UNIT);
        clickCheckBoxForProduct(TITLE);
    }

    private void addCheckedProduct2() {
        addUncheckedProduct(TITLE_2, DESCRIPTION_2, QUANTITY_2, UNIT_2);
        clickCheckBoxForProduct(TITLE_2);
    }

    private void addUncheckedProduct2() {
        addUncheckedProduct(TITLE_2, DESCRIPTION_2, QUANTITY_2, UNIT_2);
    }

    private void addUncheckedProduct(String title, String description, String quantity, String unit) {
        onView(withId(R.id.fab_add_product)).perform(click());

        onView(withId(R.id.et_add_edit_product_title)).perform(typeText(title), closeSoftKeyboard());
        onView(withId(R.id.et_add_edit_product_description)).perform(typeText(description), closeSoftKeyboard());
        onView(withId(R.id.et_add_edit_product_quantity)).perform(typeText(quantity), closeSoftKeyboard());
        onView(withId(R.id.et_add_edit_product_unit)).perform(typeText(unit), closeSoftKeyboard());

        onView(withId(R.id.fab_done_product)).perform(click());

    }

    private void viewCheckedProducts() {
        onView(withId(R.id.menu_filter)).perform(click());
        onView(withText(R.string.menu_checked)).perform(click());
    }

    private void viewUncheckedProducts() {
        onView(withId(R.id.menu_filter)).perform(click());
        onView(withText(R.string.menu_unchecked)).perform(click());
    }

    private void viewSortedByTitleProducts() {
        onView(withId(R.id.menu_filter)).perform(click());
        onView(withText(R.string.menu_sort_by_title)).perform(click());
    }

    private void viewAllProducts() {
        onView(withId(R.id.menu_filter)).perform(click());
        onView(withText(R.string.menu_all)).perform(click());
    }

    private void clickCheckBoxForProduct(String title) {
        onView(hasCheckBoxWithProductTitle(title)).perform(click());
    }

    private Matcher<View> hasCheckBoxWithProductTitle(String title) {
        return new BoundedMatcher<View, CheckBox>(CheckBox.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has Checkbox with product title " + title);
            }

            @Override
            protected boolean matchesSafely(CheckBox item) {
                ViewGroup parent = (ViewGroup) item.getParent();
                TextView tvTitle = parent.findViewById(R.id.tv_title);
                return tvTitle.getText().equals(title);
            }
        };
    }


    private void clickNavigateUpButton() {
        onView(withContentDescription(getToolbarNavigationContentDescription())).perform(click());
    }

    private String getToolbarNavigationContentDescription() {
        return UiTestUtils.getToolbarNavigationContentDescription(
                getCurrentActivity(), R.id.toolbar);
    }

}
