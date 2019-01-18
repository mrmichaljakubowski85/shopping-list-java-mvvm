package com.tomtre.android.architecture.shoppinglistmvp.data;

import java.text.Collator;
import java.util.Comparator;

public class ProductComparator implements Comparator<Product> {

    private final Collator collator;

    public ProductComparator(Collator collator) {
        this.collator = collator;
    }

    @Override
    public int compare(Product o1, Product o2) {
        return collator.compare(o1.getTitle(), o2.getTitle());
    }
}
