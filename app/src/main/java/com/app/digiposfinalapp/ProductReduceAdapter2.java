package com.app.digiposfinalapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductReduceAdapter2 extends RecyclerView.Adapter<ProductReduceAdapter2.ViewHolder> {
    private List<ProductDataModel> dataList;
    private String ipAddress;
    private String portNumber;
    private String databaseName;
    private String username;
    private String password;

    public ProductReduceAdapter2(List<ProductDataModel> dataList, String ipAddress, String portNumber,
                                 String databaseName, String username, String password) {
        this.dataList = dataList;
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    public void filterList(List<ProductDataModel> filteredList) {
        this.dataList = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productPrice;
        TextView id;
        CardView cardProduct;
        LinearLayout edit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.itemText3410);
            productName = itemView.findViewById(R.id.itemText34105);
            productPrice = itemView.findViewById(R.id.itemText341056);
            cardProduct = itemView.findViewById(R.id.carproduct);
            edit = itemView.findViewById(R.id.edit);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductDataModel product = dataList.get(position);
        holder.id.setText(String.valueOf(product.getId()));
        holder.productName.setText(product.getBarcode());
        holder.productPrice.setText(String.valueOf(product.getPrice()));





    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
