package com.tomtre.android.architecture.shoppinglistmvp.ui.productdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.tomtre.android.architecture.shoppinglistmvp.R;
import com.tomtre.android.architecture.shoppinglistmvp.util.AndroidUtils;

import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.isNull;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String KEY_PRODUCT_ID = "KEY_PRODUCT_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        setUpActionBar();
        String productId = getIntent().getStringExtra(KEY_PRODUCT_ID);
        initProductDetailFragment(productId);
    }

    private void setUpActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void initProductDetailFragment(String productId) {
        ProductDetailFragment productDetailFragment = (ProductDetailFragment) getSupportFragmentManager().findFragmentById(R.id.l_content_frame);
        if (isNull(productDetailFragment)) {
            productDetailFragment = ProductDetailFragment.newInstance(productId);
            AndroidUtils.addFragmentToActivity(getSupportFragmentManager(), productDetailFragment, R.id.l_content_frame);
        }
    }

}
