package com.tomtre.android.architecture.shoppinglistmvp.util;

import com.tomtre.android.architecture.shoppinglistmvp.data.Product;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProductsTestUtils {

    public static Product createCheckedProduct(int number) {
        return new Product(
                concat("Title", number),
                concat("Description", number),
                concat("Quantity", number),
                concat("Unit", number),
                true
        );
    }

    public static Product createUncheckedProduct(int number) {
        return new Product(
                concat("Title", number),
                concat("Description", number),
                concat("Quantity", number),
                concat("Unit", number),
                false
        );
    }

    private static String concat(String str, int number) {
        return str + " " + number;
    }


    public static Product createUncheckedProduct() {
        return createUncheckedProduct(1);
    }

    public static Product createCheckedProduct() {
        return createCheckedProduct(1);
    }

    public static void assertProduct(Product product, String id, String description, String quantity, String unit, boolean checked) {
        assertThat(product.getId(), equalTo(id));
        assertThat(product.getDescription(), equalTo(description));
        assertThat(product.getQuantity(), equalTo(quantity));
        assertThat(product.getUnit(), equalTo(unit));
        assertThat(product.isChecked(), equalTo(checked));
    }
}
