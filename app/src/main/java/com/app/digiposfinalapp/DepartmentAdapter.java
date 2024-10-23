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

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHolder> {


    private List<DepartmentModel> dataList;

    // Constructor
    public DepartmentAdapter(List<DepartmentModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.department_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DepartmentModel item = dataList.get(position);
        holder.ageTextView.setText(item.getAge());
        holder.departmentTextView.setText(item.getDepartment());



        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the clicked item's details
                String department =item.getDepartment();
                String age = item.getAge();
                int sale = item.getNum();
                int weight = item.getNoshop();
                int points = item.getPoints();
                int id=item.getDepartmentId();

                MyBottomSheetFragment bottomSheetFragment = new MyBottomSheetFragment();
                bottomSheetFragment.setDetails(view.getContext(), department, age, sale, weight, points,id);
                bottomSheetFragment.show(((MainActivity) view.getContext()).getSupportFragmentManager(), bottomSheetFragment.getTag());

            }

        });




        holder.carproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), DepartmentDetails.class);

                intent.putExtra("DEPARTMENT", item.getDepartment());
                intent.putExtra("AGE", item.getAge());
                intent.putExtra("SALE", item.getNoshop());

                // Convert weight and points to strings before passing them
                String weightStr = String.valueOf(item.getNum());

                String pointsStr = String.valueOf(item.getPoints());
                String id1 = String.valueOf(item.getDepartmentId());

                intent.putExtra("WEIGHT", weightStr);
                intent.putExtra("POINTS", pointsStr);
                intent.putExtra("ID", id1);

                view.getContext().startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ageTextView;
        TextView departmentTextView;

        LinearLayout edit;
        CardView carproduct;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ageTextView = itemView.findViewById(R.id.itemText34105);
            departmentTextView = itemView.findViewById(R.id.itemText3410);
            edit=itemView.findViewById(R.id.edit);
            carproduct=itemView.findViewById(R.id.carproduct);
        }
    }
}
