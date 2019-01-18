package com.tomtre.android.architecture.shoppinglistmvp.data;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.isNull;


@SuppressWarnings("Guava")
public class ProductsCache {

    private static final Object LOCK = new Object();
    private static ProductsCache INSTANCE;
    private final Map<String, Product> products = new LinkedHashMap<>();

    private ProductsCache() {
    }

    public static ProductsCache getInstance() {
        if (isNull(INSTANCE)) {
            synchronized (LOCK) {
                if (isNull(INSTANCE)) {
                    INSTANCE = new ProductsCache();
                }
            }
        }
        return INSTANCE;
    }

    public Collection<Product> getProducts() {
        return products.values();
    }

    public Optional<Product> getProduct(String productId) {
        return Optional.fromNullable(products.get(productId));
    }

    public void clear() {
        products.clear();
    }

    public void removeAllIf(Predicate<Product> predicate) {
        Iterator<Map.Entry<String, Product>> iterator = products.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Product> entry = iterator.next();
            if (predicate.apply(entry.getValue()))
                iterator.remove();
        }
    }

    public void remove(String productId) {
        products.remove(productId);
    }

    public void save(Product product) {
        checkNotNull(product);
        products.put(product.getId(), product);
    }
}
