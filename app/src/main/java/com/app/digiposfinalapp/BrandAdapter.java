package com.app.digiposfinalapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.MyViewHolder> {

    private List<BrandModel> brandList;

    public BrandAdapter(List<BrandModel> brandList) {
        this.brandList = brandList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.brand_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        BrandModel brand = brandList.get(position);

        holder.id.setText(String.valueOf(brand.getId()));
        holder.brand.setText(brand.getBrandName());
        holder.done.setText(String.valueOf(brand.getDone()));

        

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the clicked item's details from the brand object
                String brandName = brand.getBrandName();
                String plu = String.valueOf(brand.getDone());
                String id = String.valueOf(brand.getId());

                // Open bottom sheet fragment for editing
                MyBottomSheetFragmentbrand bottomSheetFragment = new MyBottomSheetFragmentbrand();
                bottomSheetFragment.setDetails(view.getContext(), brandName, plu, id);
                bottomSheetFragment.show(((BrandActivity) view.getContext()).getSupportFragmentManager(), bottomSheetFragment.getTag());

            }
        });

        holder.carproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start the new activity
                Intent intent = new Intent(view.getContext(), BrandDetailActivity.class);
                intent.putExtra("brand_id", String.valueOf(brand.getId()));
                intent.putExtra("brand_name", brand.getBrandName());
                intent.putExtra("brand_done", String.valueOf(brand.getDone()));
                // Start the new activity
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView brand, done,id;

        LinearLayout edit;
        CardView carproduct;

        public MyViewHolder(View view) {
            super(view);

            id=view.findViewById(R.id.itemText3410);
            brand = view.findViewById(R.id.itemText34105);
            done = view.findViewById(R.id.itemText341056);
            carproduct=view.findViewById(R.id.carproduct);
            edit=view.findViewById(R.id.edit);

        }

    }

}
