package com.app.digiposfinalapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder> {

    private List<PromotionModel> promotionList;
    private FragmentManager fragmentManager; // Add FragmentManager

    // Constructor to pass FragmentManager
    public PromotionAdapter(List<PromotionModel> promotionList, FragmentManager fragmentManager) {
        this.promotionList = promotionList;
        this.fragmentManager = fragmentManager; // Initialize FragmentManager
    }

    @NonNull
    @Override
    public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.promotion_item_new, parent, false);
        return new PromotionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionViewHolder holder, int position) {
        PromotionModel promotion = promotionList.get(position);
        holder.bind(promotion);

        holder.proname.setText(":" + "  " + promotion.getDescription());
        holder.rewardtype.setText(":" + "  " + promotion.getRuleNo());
        holder.protype.setText(":" + "  " + promotion.getType());
        holder.startdate.setText(":" + "  " + promotion.getStart());
        holder.enddate.setText(":" + "  " + promotion.getEnd());
        holder.status.setText(":" + "  " + promotion.getDone());

        // Using passed fragmentManager for fragment transaction
        holder.cardproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromotionDetailsFragment productManagementFragment = new PromotionDetailsFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return promotionList.size();
    }

    public class PromotionViewHolder extends RecyclerView.ViewHolder {

        LinearLayout edit;
        TextView proname, rewardtype, protype, startdate, enddate, status;
        CardView cardproduct;

        public PromotionViewHolder(@NonNull View itemView) {
            super(itemView);

            edit = itemView.findViewById(R.id.edit);
            proname = itemView.findViewById(R.id.PROMOTION_NAME);
            rewardtype = itemView.findViewById(R.id.REWARD_TYPE);
            protype = itemView.findViewById(R.id.PROMOTION_TYPE);
            startdate = itemView.findViewById(R.id.START_DATE);
            enddate = itemView.findViewById(R.id.END_DATE);
            status = itemView.findViewById(R.id.STATUS);
            cardproduct = itemView.findViewById(R.id.cardproduct);
        }

        public void bind(PromotionModel promotion) {
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Get the clicked item's details from the promotion object
                    int id = promotion.getId();
                    int promoId = promotion.getPromoId();
                    String description = promotion.getDescription();
                    String receipt = promotion.getReceipt();
                    String ruleNo = promotion.getRuleNo();
                    double ruleValue = promotion.getRuleValue();
                    String type = promotion.getType();
                    double typeValue = promotion.getTypeValue();
                    String start = promotion.getStart();
                    String endDate = promotion.getEnd();
                    int itemCount = promotion.getItemCount();
                    String plu = promotion.getPlu();
                    int done = promotion.getDone();

                    // Open bottom sheet fragment for editing
                    MyBottomSheetFragmentpromotion bottomSheetFragment = new MyBottomSheetFragmentpromotion();
                    bottomSheetFragment.setDetails(view.getContext(), id, promoId, description, receipt, ruleNo, ruleValue, type, typeValue,
                            start, endDate, itemCount, plu, done);
                    bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag()); // Use passed fragmentManager
                }
            });
        }
    }
}
