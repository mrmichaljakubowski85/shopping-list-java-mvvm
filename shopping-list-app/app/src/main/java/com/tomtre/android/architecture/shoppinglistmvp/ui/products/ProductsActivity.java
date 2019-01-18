package com.tomtre.android.architecture.shoppinglistmvp.ui.products;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.tomtre.android.architecture.shoppinglistmvp.R;
import com.tomtre.android.architecture.shoppinglistmvp.util.AndroidUtils;

import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.isNull;

public class ProductsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        setUpActionBar();
        initProductsFragment();
    }

    private void setUpActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initProductsFragment() {
        ProductsFragment productsFragment = (ProductsFragment) getSupportFragmentManager().findFragmentById(R.id.l_content_frame);
        if (isNull(productsFragment)) {
            productsFragment = ProductsFragment.newInstance();
            AndroidUtils.addFragmentToActivity(getSupportFragmentManager(), productsFragment, R.id.l_content_frame);
        }

    }


}
