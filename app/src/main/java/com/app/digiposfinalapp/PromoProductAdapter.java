package com.app.digiposfinalapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PromoProductAdapter extends RecyclerView.Adapter<PromoProductAdapter.ProductViewHolder> {

    private final Context context;
    private List<PromoProductEdit> productList;
    private final NumberFormat currencyFormat;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onRemoveClick(int position);
        void onBuyClick(int position);
        void onGetClick(int position);
    }

    public PromoProductAdapter(Context context, List<PromoProductEdit> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.UK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_promo_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        PromoProductEdit product = productList.get(position);


        holder.tvDescription.setText(product.getDescription());
        holder.tvBarcode.setText("Barcode: " + product.getBarcode());
        holder.tvPrice.setText("Price: " + product.getPrice());
        holder.tvCostPrice.setText("Cost: " + product.getCostPrice());
        holder.tvItemCode.setText("Itemcode: "+product.getItemCode());



//        holder.tvDescription.setText(product.getDescription());
//        holder.tvItemCode.setText(product.getItemCode());
//        holder.tvBarcode.setText(product.getBarcode());
//        holder.tvPrice.setText(currencyFormat.format(product.getPrice()));
//        holder.tvCostPrice.setText(currencyFormat.format(product.getCostPrice()));

        // Set click listeners for buttons
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveClick(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public void updateProducts(List<PromoProductEdit> newProducts) {
        productList = newProducts;
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvDescription, tvItemCode, tvBarcode, tvPrice, tvCostPrice;
        Button btnRemove;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvItemCode = itemView.findViewById(R.id.tvitemcode);
            tvBarcode = itemView.findViewById(R.id.tvBarcode);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCostPrice = itemView.findViewById(R.id.tvCostPrice);
            btnRemove = itemView.findViewById(R.id.btnRemove);

        }
    }

    public List<PromoProductEdit> getProducts() {
        return productList; // Return your list of products
    }
}