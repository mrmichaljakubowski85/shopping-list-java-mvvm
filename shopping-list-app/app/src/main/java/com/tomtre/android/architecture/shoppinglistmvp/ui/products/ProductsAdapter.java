package com.tomtre.android.architecture.shoppinglistmvp.ui.products;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.tomtre.android.architecture.shoppinglistmvp.R;
import com.tomtre.android.architecture.shoppinglistmvp.data.Product;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.tomtre.android.architecture.shoppinglistmvp.util.AndroidUtils.hasPosition;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private final ProductItemListener productItemListener;
    private List<Product> products;

    public ProductsAdapter(List<Product> products, ProductItemListener productItemListener) {
        setData(products);
        this.productItemListener = productItemListener;
    }

    private void setData(List<Product> products) {
        checkNotNull(products);
        this.products = products;
    }

    public void replaceData(List<Product> products) {
        setData(products);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(itemView, productItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        bindData(holder, product);
        setBackground(holder, product);
    }

    private void bindData(ViewHolder holder, Product product) {
        holder.tvTitle.setText(product.getTitle());

        String quantity = product.getQuantity();
        if (Strings.isNullOrEmpty(quantity)) {
            holder.tvQuantity.setVisibility(View.GONE);
        } else {
            holder.tvQuantity.setVisibility(View.VISIBLE);
            holder.tvQuantity.setText(quantity);
        }

        String unit = product.getUnit();
        if (Strings.isNullOrEmpty(unit)) {
            holder.tvUnit.setVisibility(View.GONE);
        } else {
            holder.tvUnit.setVisibility(View.VISIBLE);
            holder.tvUnit.setText(unit);
        }

        holder.cbChecked.setChecked(product.isChecked());
    }

    private void setBackground(ViewHolder holder, Product product) {
        Context context = holder.itemView.getContext();
        if (product.isChecked())
            holder.itemView.setBackgroundDrawable(getDrawable(context, R.drawable.background_item_product_checked));
        else
            holder.itemView.setBackgroundDrawable(getDrawable(context, R.drawable.background_item_product_unchecked));
    }

    private Drawable getDrawable(Context context, @DrawableRes int resId) {
        return context.getResources().getDrawable(resId);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    public interface ProductItemListener {

        void onProductClick(Product product);

        void onCheckedProduct(Product product);

        void onUnCheckedProduct(Product product);

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        TextView tvTitle;

        @BindView(R.id.tv_quantity)
        TextView tvQuantity;

        @BindView(R.id.tv_unit)
        TextView tvUnit;

        @BindView(R.id.cb_checked)
        CheckBox cbChecked;

        ViewHolder(View itemView, final ProductItemListener productItemListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                if (hasPosition(adapterPosition)) {
                    Product product = products.get(adapterPosition);
                    productItemListener.onProductClick(product);
                }
            });

            cbChecked.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                if (hasPosition(adapterPosition)) {
                    Product product = products.get(adapterPosition);
                    if (!product.isChecked())
                        productItemListener.onCheckedProduct(product);
                    else
                        productItemListener.onUnCheckedProduct(product);
                }
            });
        }
    }
}
