package com.tomtre.android.architecture.shoppinglistmvp.ui.addeditproduct;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tomtre.android.architecture.shoppinglistmvp.R;
import com.tomtre.android.architecture.shoppinglistmvp.data.Injection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.nonNull;

public class AddEditProductFragment extends Fragment implements AddEditProductContract.View {

    public static final String KEY_EDIT_PRODUCT_ID = "KEY_EDIT_PRODUCT_ID";
    private static final String KEY_LOAD_DATA_FROM_REPOSITORY = "KEY_LOAD_DATA_FROM_REPOSITORY";

    @BindView(R.id.et_add_edit_product_title)
    EditText etAddEditProductTitle;

    @BindView(R.id.et_add_edit_product_description)
    EditText etAddEditProductDescription;

    @BindView(R.id.et_add_edit_product_quantity)
    EditText etAddEditProductQuantity;

    @BindView(R.id.et_add_edit_product_unit)
    EditText etAddEditProductUnit;

    @BindView(R.id.tv_no_product_message)
    TextView tvNoProductMessage;

    @BindView(R.id.l_container_product)
    LinearLayout lContainerProduct;

    Unbinder unbinder;
    private AddEditProductContract.Presenter presenter;

    public static AddEditProductFragment newInstance(@Nullable String productId) {
        AddEditProductFragment addEditProductFragment = new AddEditProductFragment();
        if (nonNull(productId)) {
            Bundle args = new Bundle();
            args.putString(KEY_EDIT_PRODUCT_ID, productId);
            addEditProductFragment.setArguments(args);
        }
        return addEditProductFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //decides if we need to refresh data (configuration change, background/foreground etc)
        boolean loadDataFromRepository = shouldLoadDataFromRepository(savedInstanceState);

        String productId = findProductId();

        presenter = new AddEditProductPresenter(
                productId,
                Injection.provideProductsRepository(getContext()),
                loadDataFromRepository
        );
    }

    @Nullable
    private String findProductId() {
        if (nonNull(getArguments())) {
            return getArguments().getString(KEY_EDIT_PRODUCT_ID);
        }
        return null;
    }

    private boolean shouldLoadDataFromRepository(@Nullable Bundle savedInstanceState) {
        if (nonNull(savedInstanceState)) {
            return savedInstanceState.getBoolean(KEY_LOAD_DATA_FROM_REPOSITORY);
        } else {
            return true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_product, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.takeView(this);

        FloatingActionButton fabDoneProduct = getActivity().findViewById(R.id.fab_done_product);
        fabDoneProduct.setOnClickListener(
                v -> presenter.saveProduct(
                        etAddEditProductTitle.getText().toString(),
                        etAddEditProductDescription.getText().toString(),
                        etAddEditProductQuantity.getText().toString(),
                        etAddEditProductUnit.getText().toString())
        );

    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_LOAD_DATA_FROM_REPOSITORY, presenter.isDataMissing());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.dropView();
        unbinder.unbind();
    }

    @Override
    public void setTitle(String title) {
        etAddEditProductTitle.setText(title);
    }

    @Override
    public void setDescription(String description) {
        etAddEditProductDescription.setText(description);
    }

    @Override
    public void setQuantity(String quantity) {
        etAddEditProductQuantity.setText(quantity);
    }

    @Override
    public void setUnit(String unit) {
        etAddEditProductUnit.setText(unit);
    }

    @Override
    public void showProductListUI() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void showEmptyProductError() {
        Snackbar.make(etAddEditProductTitle, R.string.empty_product_message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showMissingProduct() {
        lContainerProduct.setVisibility(View.GONE);
        tvNoProductMessage.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean isActive() {
        return isAdded();
    }


}
