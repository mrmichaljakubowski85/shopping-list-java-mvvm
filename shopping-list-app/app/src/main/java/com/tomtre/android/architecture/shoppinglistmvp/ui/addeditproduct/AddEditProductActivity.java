package com.tomtre.android.architecture.shoppinglistmvp.ui.addeditproduct;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.tomtre.android.architecture.shoppinglistmvp.R;
import com.tomtre.android.architecture.shoppinglistmvp.util.AndroidUtils;

import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.isNull;

public class AddEditProductActivity extends AppCompatActivity {

    public static final String KEY_EDIT_PRODUCT_ID = "KEY_EDIT_PRODUCT_ID";

    private ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);
        setUpActionBar();
        String productId = getProductId();
        setToolbarTitle(productId);
        initAddEditFragment(productId);
    }

    private void setUpActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Nullable
    private String getProductId() {
        if (getIntent().hasExtra(KEY_EDIT_PRODUCT_ID))
            return getIntent().getStringExtra(KEY_EDIT_PRODUCT_ID);
        else
            return null;
    }

    private void setToolbarTitle(@Nullable String productId) {
        if (isNull(productId))
            actionBar.setTitle(R.string.add_product);
        else
            actionBar.setTitle(R.string.edit_product);
    }

    private void initAddEditFragment(@Nullable String productId) {
        AddEditProductFragment productsFragment = (AddEditProductFragment) getSupportFragmentManager().findFragmentById(R.id.l_content_frame);
        if (isNull(productsFragment)) {
            productsFragment = AddEditProductFragment.newInstance(productId);
            AndroidUtils.addFragmentToActivity(getSupportFragmentManager(), productsFragment, R.id.l_content_frame);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
