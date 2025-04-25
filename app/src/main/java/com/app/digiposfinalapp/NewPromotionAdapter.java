package com.app.digiposfinalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

public class NewPromotionAdapter extends RecyclerView.Adapter<NewPromotionAdapter.NewPromotionViewHolder> {
    private List<NewPromotion> promotionList;
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public NewPromotionAdapter(List<NewPromotion> promotionList, Context context) {
        this.promotionList = promotionList;
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("PromotionPrefs", Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
        this.gson = new Gson();
    }

    @NonNull
    @Override
    public NewPromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_new_promotion, parent, false);
        return new NewPromotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewPromotionViewHolder holder, int position) {
        NewPromotion promotion = promotionList.get(position);
        Log.d("AdapterDebug", "Binding position " + position + ": " + promotion.getDescription());

        holder.tvDescription.setText(promotion.getDescription());
        holder.tvBarcode.setText("Barcode: " + promotion.getBarcode());
        holder.tvPrice.setText("Price: " + promotion.getPrice());
        holder.tvCostPrice.setText("Cost: " + promotion.getCostPrice());
        holder.tvitemcode.setText("Itemcode: "+promotion.getItemCode());

        // Add click listener to remove button
        holder.btnRemove.setOnClickListener(v -> {
            removeItem(position);
        });
    }

    @Override
    public int getItemCount() {
        Log.d("AdapterDebug", "Item count: " + promotionList.size());
        return promotionList.size();
    }

    // Method to remove item
    public void removeItem(int position) {
        if (position >= 0 && position < promotionList.size()) {
            // Remove from list
            promotionList.remove(position);
            // Update SharedPreferences
            saveListToPreferences();

            // Notify adapter
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, promotionList.size());

            Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to save updated list to SharedPreferences
    private void saveListToPreferences() {
        String json = gson.toJson(promotionList);
        editor.putString("promotion_list", json);
        editor.apply();
    }

    public static class NewPromotionViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvBarcode, tvPrice, tvCostPrice, tvitemcode;
        TextView btnRemove; // Assuming you have a remove button in your layout

        public NewPromotionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvBarcode = itemView.findViewById(R.id.tvBarcode);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCostPrice = itemView.findViewById(R.id.tvCostPrice);
            tvitemcode = itemView.findViewById(R.id.tvitemcode);
            btnRemove = itemView.findViewById(R.id.btnRemove); // Make sure this ID matches your layout
        }
    }
}