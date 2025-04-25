package com.app.digiposfinalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PromotionSearchAdapter extends RecyclerView.Adapter<PromotionSearchAdapter.PromotionSearchViewHolder> {
    private List<PromotionSearch> promotionSearchList;
    private Context context;

    public PromotionSearchAdapter(List<PromotionSearch> promotionSearchList, Context context) {
        this.promotionSearchList = promotionSearchList;
        this.context = context;
    }

    @NonNull
    @Override
    public PromotionSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_promotion_search, parent, false);
        return new PromotionSearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionSearchViewHolder holder, int position) {
        PromotionSearch promotion = promotionSearchList.get(position);

        holder.txtPromoName.setText(promotion.getPromoName());
        holder.txtDescription.setText(String.valueOf("Promo ID :"+promotion.getPromoID()));
        holder.txtDates.setText(promotion.getStartDate() + " - " + promotion.getEndDate());
        holder.txtpromotype.setText(promotion.getType());
        holder.txtitemcount.setText("Qtys :"+promotion.getRuleValue());

    }

    @Override
    public int getItemCount() {
        return promotionSearchList.size();
    }

    public class PromotionSearchViewHolder extends RecyclerView.ViewHolder {
        public TextView txtPromoName, txtDescription, txtDates,txtpromotype,txtitemcount;

        public PromotionSearchViewHolder(View view) {
            super(view);
            txtPromoName = view.findViewById(R.id.txtPromoName);
            txtDescription = view.findViewById(R.id.txtid);
            txtDates = view.findViewById(R.id.txtDates);
            txtitemcount =view.findViewById(R.id.txtitemcount);
            txtpromotype=view.findViewById(R.id.txtpromotype);
        }
    }
}