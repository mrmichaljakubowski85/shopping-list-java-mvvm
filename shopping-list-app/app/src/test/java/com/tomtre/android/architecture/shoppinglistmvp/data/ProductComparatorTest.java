package com.tomtre.android.architecture.shoppinglistmvp.data;

import com.google.common.collect.Lists;

import org.junit.Test;

import java.text.Collator;
import java.util.List;
import java.util.Locale;

import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.prepareSortedByTitleProducts;
import static com.tomtre.android.architecture.shoppinglistmvp.util.ProductsTestUtils.prepareUnsortedProducts;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ProductComparatorTest {

    private static Product PRODUCT_1_PL = new Product("a", "Description 1", "Quantity 1", "Unit 1", true);
    private static Product PRODUCT_2_PL = new Product("ą", "Description 2", "Quantity 2", "Unit 2", true);
    private static Product PRODUCT_3_PL = new Product("ę", "Description 3", "Quantity 3", "Unit 3", false);

    private ProductComparator productComparator;

    @Test
    public void shouldSortListWithEnglishLocalCollator() {
        //given
        productComparator = new ProductComparator(Collator.getInstance(Locale.ENGLISH));
        List<Product> products = prepareUnsortedProducts();
        List<Product> sortedList = prepareSortedByTitleProducts();

        //when
        products.sort(productComparator);

        //then
        assertThat(products, equalTo(sortedList));
    }

    @Test
    public void shouldSortListWithPolishLocalCollator() {
        //given
        productComparator = new ProductComparator(Collator.getInstance(new Locale("pl")));

        List<Product> products = Lists.newArrayList(
                PRODUCT_3_PL,
                PRODUCT_2_PL,
                PRODUCT_1_PL
        );
        List<Product> sortedList = Lists.newArrayList(
                PRODUCT_1_PL,
                PRODUCT_2_PL,
                PRODUCT_3_PL
        );

        //when
        products.sort(productComparator);

        //then
        assertThat(products, equalTo(sortedList));
    }

}