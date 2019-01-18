package com.tomtre.android.architecture.shoppinglistmvp.ui.productdetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tomtre.android.architecture.shoppinglistmvp.R;
import com.tomtre.android.architecture.shoppinglistmvp.data.Injection;
import com.tomtre.android.architecture.shoppinglistmvp.ui.addeditproduct.AddEditProductActivity;
import com.tomtre.android.architecture.shoppinglistmvp.util.RequestCodes;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ProductDetailFragment extends Fragment implements ProductDetailContract.View {

    public static final String KEY_PRODUCT_ID = "KEY_PRODUCT_ID";

    @BindView(R.id.cb_product_detail_checked)
    CheckBox cbProductDetailChecked;

    @BindView(R.id.tv_product_detail_title)
    TextView tvProductDetailTitle;

    @BindView(R.id.tv_product_detail_description)
    TextView tvProductDetailDescription;

    @BindView(R.id.tv_product_detail_quantity)
    TextView tvProductDetailQuantity;

    @BindView(R.id.tv_product_detail_unit)
    TextView tvProductDetailUnit;

    @BindView(R.id.progress_bar_loading)
    ContentLoadingProgressBar progressBarLoading;

    @BindView(R.id.l_container_product)
    LinearLayout lContainerProduct;

    @BindView(R.id.tv_no_product_message)
    TextView tvNoProductMessage;

    Unbinder unbinder;
    private ProductDetailContract.Presenter presenter;


    public static ProductDetailFragment newInstance(String productId) {
        ProductDetailFragment productDetailFragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(KEY_PRODUCT_ID, productId);
        productDetailFragment.setArguments(args);
        return productDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection ConstantConditions
        String productId = getArguments().getString(KEY_PRODUCT_ID);

        //noinspection ConstantConditions
        presenter = new ProductDetailPresenter(
                productId,
                Injection.provideProductsRepository(getContext())
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.takeView(this);

        //noinspection ConstantConditions
        FloatingActionButton fabEditProduct = getActivity().findViewById(R.id.fab_edit_product);
        fabEditProduct.setOnClickListener(v -> presenter.editProduct());

        cbProductDetailChecked.setOnClickListener(v -> {
            CheckBox checkBox = (CheckBox) v;
            if (checkBox.isChecked())
                presenter.checkProduct();
            else
                presenter.uncheckProduct();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.dropView();
        unbinder.unbind();
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active)
            progressBarLoading.show();
        else
            progressBarLoading.hide();
    }

    @Override
    public void showMissingProduct() {
        lContainerProduct.setVisibility(View.GONE);
        tvNoProductMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void showTitle(String title) {
        tvProductDetailTitle.setText(title);
    }

    @Override
    public void showDescription(String description) {
        tvProductDetailDescription.setVisibility(View.VISIBLE);
        tvProductDetailDescription.setText(description);
    }

    @Override
    public void hideDescription() {
        tvProductDetailDescription.setVisibility(View.GONE);
    }

    @Override
    public void showQuantity(String quantity) {
        tvProductDetailQuantity.setVisibility(View.VISIBLE);
        tvProductDetailQuantity.setText(quantity);
    }

    @Override
    public void hideQuantity() {
        tvProductDetailQuantity.setVisibility(View.GONE);
    }

    @Override
    public void showUnit(String unit) {
        tvProductDetailUnit.setVisibility(View.VISIBLE);
        tvProductDetailUnit.setText(unit);
    }

    @Override
    public void hideUnit() {
        tvProductDetailUnit.setVisibility(View.GONE);
    }

    @Override
    public void showCheckedStatus(boolean checked) {
        cbProductDetailChecked.setChecked(checked);
    }

    @Override
    public void showProductMarkedAsChecked() {
        Snackbar.make(tvProductDetailTitle, R.string.product_marked_as_checked, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showProductMarkedAsUnchecked() {
        Snackbar.make(tvProductDetailTitle, R.string.product_marked_as_unchecked, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showEditProductUI(String productId) {
        Intent intent = new Intent(getContext(), AddEditProductActivity.class);
        intent.putExtra(AddEditProductActivity.KEY_EDIT_PRODUCT_ID, productId);
        startActivityForResult(intent, RequestCodes.EDIT_PRODUCT);
    }

    @Override
    public void showProductRemoved() {
        //noinspection ConstantConditions
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void showProductEdited() {
        //noinspection ConstantConditions
        getActivity().finish();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_remove_product:
                presenter.removeProduct();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_product_detail, menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.activityResult(requestCode, resultCode);
    }
}
