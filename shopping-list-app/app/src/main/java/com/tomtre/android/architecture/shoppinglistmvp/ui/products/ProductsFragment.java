package com.tomtre.android.architecture.shoppinglistmvp.ui.products;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
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
import com.tomtre.android.architecture.shoppinglistmvp.util.SnackbarMessage;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static com.tomtre.android.architecture.shoppinglistmvp.util.CommonUtils.nonNull;

public class ProductsFragment extends Fragment {

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
    private ProductsAdapter productsAdapter;
    private ProductsAdapter.ProductItemListener productItemListener;
    private ProductsViewModel productsViewModel;

    public static ProductsFragment newInstance() {
        return new ProductsFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ProductsViewModel.Factory factory = new ProductsViewModel.Factory(
                getActivity().getApplication(),
                Injection.provideProductsRepository(getContext()));

        productsViewModel = ViewModelProviders.of(this, factory).get(ProductsViewModel.class);

        setFilterTypeToViewModel(savedInstanceState);

        setUpProductListener();
        productsAdapter = new ProductsAdapter(new ArrayList<>(0), productItemListener);

    }

    private void setUpProductListener() {
        productItemListener = new ProductsAdapter.ProductItemListener() {
            @Override
            public void onProductClick(Product product) {
                productsViewModel.openProductDetails(product);
            }

            @Override
            public void onCheckedProduct(Product product) {
                productsViewModel.checkProduct(product);
            }

            @Override
            public void onUnCheckedProduct(Product product) {
                productsViewModel.uncheckProduct(product);
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


        RecyclerView productListRecyclerView = view.findViewById(R.id.rv_product_list);
        productListRecyclerView.setAdapter(productsAdapter);
        productListRecyclerView.setHasFixedSize(true);
        productListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton addProductFAB = getActivity().findViewById(R.id.fab_add_product);
        addProductFAB.setOnClickListener(v -> productsViewModel.addNewProduct());

        lSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getContext(), R.color.colorPrimary),
                ContextCompat.getColor(getContext(), R.color.colorAccent),
                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        lSwipeRefreshLayout.setOnRefreshListener(() -> productsViewModel.refresh());

        setUpObservers();
    }

    private void setUpObservers() {
        LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();

        productsViewModel.getSnackbarMessage().observe(
                viewLifecycleOwner,
                (SnackbarMessage.SnackbarObserver) messageResId -> {
                    Timber.d("getSnackbarMessage");
                    showMessage(messageResId);
                });

        productsViewModel.getNewProductEvent().observe(viewLifecycleOwner, aVoid -> {
            Timber.d("getNewProductEvent");

            Intent intent = new Intent(getContext(), AddEditProductActivity.class);
            startActivityForResult(intent, RequestCodes.ADD_PRODUCT);
        });

        productsViewModel.getObservableProducts().observe(viewLifecycleOwner, products -> {
            Timber.d("getObservableProducts");
            productsAdapter.replaceData(products);
        });

        productsViewModel.getOpenProductEvent().observe(viewLifecycleOwner, productId -> {
            Timber.d("getOpenProductEvent: productId: %s", productId);

            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, productId);
            startActivityForResult(intent, RequestCodes.REMOVE_PRODUCT);
        });

        productsViewModel.getNoProductsLabel().observe(viewLifecycleOwner, labelText -> {
            Timber.d("getNoProductsLabel: labelText: %s", labelText);
            tvNoProductsMessage.setText(labelText);
        });

        productsViewModel.getCurrentFilteringLabel().observe(viewLifecycleOwner, labelText -> {
            Timber.d("getCurrentFilteringLabel: labelText: %s", labelText);
            tvFilteringLabel.setText(labelText);
        });

        productsViewModel.getNoProductViewVisible().observe(viewLifecycleOwner, visible -> {
            Timber.d("getNoProductViewVisible: visible: %s", visible);
            if (visible) {
                lContainerProductList.setVisibility(View.GONE);
                lContainerNoProducts.setVisibility(View.VISIBLE);
            } else {
                lContainerProductList.setVisibility(View.VISIBLE);
                lContainerNoProducts.setVisibility(View.GONE);

            }
        });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(KEY_CURRENT_FILTER_TYPE, productsViewModel.getFilterType());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setFilterTypeToViewModel(Bundle savedInstanceState) {
        if (nonNull(savedInstanceState)) {
            ProductsFilterType productsFilterType = (ProductsFilterType) savedInstanceState.getSerializable(KEY_CURRENT_FILTER_TYPE);
            productsViewModel.setFilterType(productsFilterType);
        } else {
            productsViewModel.setFilterType(ProductsFilterType.ALL_PRODUCTS);
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
                productsViewModel.removeCheckedProducts();
                return true;
            case R.id.menu_refresh:
                productsViewModel.refresh();
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
                    productsViewModel.setFilterType(ProductsFilterType.CHECKED_PRODUCTS);
                    break;
                case R.id.menu_filter_unchecked_products:
                    productsViewModel.setFilterType(ProductsFilterType.UNCHECKED_PRODUCTS);
                    break;
                case R.id.menu_filter_sort_by_title_products:
                    productsViewModel.setFilterType(ProductsFilterType.SORTED_BY_PRODUCTS_TITLE);
                    break;
                default:
                    productsViewModel.setFilterType(ProductsFilterType.ALL_PRODUCTS);
                    break;
            }
            return true;
        });
        filteringMenu.show();
    }

//    @Override
//    public void setLoadingIndicator(boolean active) {
//        if (nonNull(lSwipeRefreshLayout))
//            lSwipeRefreshLayout.setRefreshing(active);
//    }

    private void showMessage(int messageResId) {
        Snackbar.make(getView(), messageResId, Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        productsViewModel.handleActivityResult(requestCode, resultCode);
    }
}
