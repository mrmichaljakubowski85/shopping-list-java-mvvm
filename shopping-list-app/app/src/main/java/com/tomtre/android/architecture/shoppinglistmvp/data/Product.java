package com.tomtre.android.architecture.shoppinglistmvp.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.leftSubString;

@Entity(tableName = "Products")
public class Product {

    @PrimaryKey
    @NonNull
    private final String id;

    @NonNull
    private final String title;

    @Nullable
    private final String description;

    @Nullable
    private final String quantity;

    @Nullable
    private final String unit;

    private final boolean checked;

    public Product(@NonNull String title, @Nullable String description, @Nullable String quantity, @Nullable String unit, boolean checked, @NonNull String id) {
        this.title = title;
        this.description = description;
        this.quantity = quantity;
        this.unit = unit;
        this.checked = checked;
        this.id = id;
    }

    @Ignore
    public Product(@NonNull String title, @Nullable String description, @Nullable String quantity, @Nullable String unit, boolean checked) {
        this(title, description, quantity, unit, checked, UUID.randomUUID().toString());
    }

    @Ignore
    public Product(Product other, boolean checked) {
        this.id = other.id;
        this.title = other.title;
        this.description = other.description;
        this.quantity = other.quantity;
        this.unit = other.unit;
        this.checked = checked;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public boolean isChecked() {
        return checked;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(title);
    }

    @Nullable
    public String getQuantity() {
        return quantity;
    }

    @Nullable
    public String getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        //noinspection ConstantConditions
        return checked == product.checked &&
                Objects.equal(id, product.id) &&
                Objects.equal(title, product.title) &&
                Objects.equal(description, product.description) &&
                Objects.equal(quantity, product.quantity) &&
                Objects.equal(unit, product.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, title, description, quantity, unit, checked);
    }

    @Override
    public String toString() {
        //noinspection ConstantConditions
        return MoreObjects.toStringHelper(this)
                .add("id", leftSubString(id, 8))
                .add("title", title)
                .add("description", description)
                .add("quantity", quantity)
                .add("unit", unit)
                .add("checked", checked)
                .toString();
    }
}
