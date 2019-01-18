package com.tomtre.android.architecture.shoppinglistmvp.ui.products;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tomtre.android.architecture.shoppinglistmvp.R;
import com.tomtre.android.architecture.shoppinglistmvp.data.Injection;
import com.tomtre.android.architecture.shoppinglistmvp.data.Product;
import com.tomtre.android.architecture.shoppinglistmvp.ui.addeditproduct.AddEditProductActivity;
import com.tomtre.android.architecture.shoppinglistmvp.ui.productdetail.ProductDetailActivity;
import com.tomtre.android.architecture.shoppinglistmvp.util.RequestCodes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.nonNull;

public class ProductsFragment extends Fragment implements ProductsContract.View {

    private static final String KEY_CURRENT_FILTER_TYPE = "KEY_CURRENT_FILTER_TYPE";

    @BindView(R.id.tv_no_products_message)
    TextView tvNoProductsMessage;

    @BindView(R.id.tv_filtering_label)
    TextView tvFilteringLabel;

    @BindView(R.id.l_container_product_list)
    LinearLayout lContainerProductList;

    @BindView(R.id.l_container_no_products)
    LinearLayout lContainerNoProducts;

    @BindView(R.id.l_swipe_refresh_layout)
    SwipeRefreshLayout lSwipeRefreshLayout;

    Unbinder unbinder;
    private ProductsContract.Presenter presenter;
    private ProductsAdapter productsAdapter;
    private ProductsAdapter.ProductItemListener productItemListener;

    public static ProductsFragment newInstance() {
        return new ProductsFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ProductsPresenter(Injection.provideProductsRepository(getContext()));
        setFilterTypeToPresenter(savedInstanceState);

        setUpProductListener();
        productsAdapter = new ProductsAdapter(new ArrayList<>(0), productItemListener);
    }

    private void setUpProductListener() {
        productItemListener = new ProductsAdapter.ProductItemListener() {
            @Override
            public void onProductClick(Product product) {
                presenter.openProductDetails(product);
            }

            @Override
            public void onCheckedProduct(Product product) {
                presenter.checkProduct(product);
            }

            @Override
            public void onUnCheckedProduct(Product product) {
                presenter.uncheckProduct(product);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.takeView(this);

        RecyclerView productListRecyclerView = view.findViewById(R.id.rv_product_list);
        productListRecyclerView.setAdapter(productsAdapter);
        productListRecyclerView.setHasFixedSize(true);
        productListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton addProductFAB = getActivity().findViewById(R.id.fab_add_product);
        addProductFAB.setOnClickListener(v -> presenter.addNewProduct());

        lSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getContext(), R.color.colorPrimary),
                ContextCompat.getColor(getContext(), R.color.colorAccent),
                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        lSwipeRefreshLayout.setOnRefreshListener(() -> presenter.loadProducts(true));
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(KEY_CURRENT_FILTER_TYPE, presenter.getFilterType());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.dropView();
        unbinder.unbind();
    }

    private void setFilterTypeToPresenter(Bundle savedInstanceState) {
        if (nonNull(savedInstanceState)) {
            ProductsFilterType productsFilterType = (ProductsFilterType) savedInstanceState.getSerializable(KEY_CURRENT_FILTER_TYPE);
            presenter.setFilterType(productsFilterType);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_products, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                showFilteringMenu();
                return true;
            case R.id.menu_remove_checked:
                presenter.removeCheckedProducts();
                return true;
            case R.id.menu_refresh:
                presenter.loadProducts(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFilteringMenu() {
        PopupMenu filteringMenu = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        filteringMenu.getMenuInflater().inflate(R.menu.menu_filter_products, filteringMenu.getMenu());

        filteringMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_filter_checked_products:
                    presenter.setFilterType(ProductsFilterType.CHECKED_PRODUCTS);
                    break;
                case R.id.menu_filter_unchecked_products:
                    presenter.setFilterType(ProductsFilterType.UNCHECKED_PRODUCTS);
                    break;
                case R.id.menu_filter_sort_by_title_products:
                    presenter.setFilterType(ProductsFilterType.SORTED_BY_PRODUCTS_TITLE);
                    break;
                default:
                    presenter.setFilterType(ProductsFilterType.ALL_PRODUCTS);
                    break;
            }
            presenter.loadProducts(false);
            return true;
        });
        filteringMenu.show();
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (nonNull(lSwipeRefreshLayout))
            lSwipeRefreshLayout.setRefreshing(active);
    }

    @Override
    public void showProducts(List<Product> products) {
        lContainerProductList.setVisibility(View.VISIBLE);
        lContainerNoProducts.setVisibility(View.GONE);

        productsAdapter.replaceData(products);
    }

    @Override
    public void showNoProducts() {
        showNoProductView(getString(R.string.no_products));
    }

    @Override
    public void showNoCheckedProducts() {
        showNoProductView(getString(R.string.no_checked_products));
    }

    @Override
    public void showNoUncheckedProducts() {
        showNoProductView(getString(R.string.no_unchecked_products));
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.saved_product));
    }

    private void showNoProductView(String message) {
        lContainerProductList.setVisibility(View.GONE);
        lContainerNoProducts.setVisibility(View.VISIBLE);

        tvNoProductsMessage.setText(message);
    }

    @Override
    public void showLoadingProductsError() {
        showMessage(getString(R.string.loading_products_error));
    }

    @Override
    public void showAllFilterLabel() {
        tvFilteringLabel.setText(getString(R.string.label_all_products));
    }

    @Override
    public void showCheckedFilterLabel() {
        tvFilteringLabel.setText(getString(R.string.label_checked_products));
    }

    @Override
    public void showUncheckedFilterLabel() {
        tvFilteringLabel.setText(getString(R.string.label_unchecked_products));
    }

    @Override
    public void showSortedByTitleFilterLabel() {
        tvFilteringLabel.setText(getString(R.string.label_sorted_by_title_products));
    }

    @Override
    public void showProductDetailsUI(String productId) {
        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, productId);
        startActivityForResult(intent, RequestCodes.REMOVE_PRODUCT);
    }

    @Override
    public void showAddProductUI() {
        Intent intent = new Intent(getContext(), AddEditProductActivity.class);
        startActivityForResult(intent, RequestCodes.ADD_PRODUCT);
    }

    @Override
    public void showRemovedCheckedProducts() {
        showMessage(getString(R.string.removed_checked_products));
    }

    @Override
    public void showRemovedProduct() {
        showMessage(getString(R.string.removed_product));
    }

    @Override
    public void showProductMarkedAsChecked() {
        showMessage(getString(R.string.product_marked_as_checked));
    }

    @Override
    public void showProductMarkedAsUnchecked() {
        showMessage(getString(R.string.product_marked_as_unchecked));
    }


    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.activityResult(requestCode, resultCode);
    }
}
