package com.tomtre.android.architecture.shoppinglistmvp.util;

import com.google.common.collect.Lists;
import com.tomtre.android.architecture.shoppinglistmvp.data.Product;

import java.util.List;
import java.util.UUID;

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

    //generates the same UUID string for the given seed string, needed because Product equals method checks UUID value
    private static String generateUUID(String seedString) {
        return UUID.nameUUIDFromBytes(seedString.getBytes()).toString();
    }

    private static final Product PRODUCT_1 = new Product("Title a", "Description 1", "Quantity 1", null, true, generateUUID("Title a"));
    private static final Product PRODUCT_2 = new Product("Title b", "Description 2", "Quantity 2", "Unit 2", false, generateUUID("Title b"));
    private static final Product PRODUCT_3 = new Product("Title c", "Description 3", "Quantity 3", "Unit 3", false, generateUUID("Title c"));
    private static final Product PRODUCT_4 = new Product("Title d", null, null, null, false, generateUUID("Title d"));

    public static List<Product> prepareUnsortedProducts() {
        return Lists.newArrayList(PRODUCT_3, PRODUCT_2, PRODUCT_4, PRODUCT_1);
    }

    public static List<Product> prepareSortedByTitleProducts() {
        return Lists.newArrayList(ProductsTestUtils.PRODUCT_1, ProductsTestUtils.PRODUCT_2, ProductsTestUtils.PRODUCT_3, ProductsTestUtils.PRODUCT_4);
    }


}
