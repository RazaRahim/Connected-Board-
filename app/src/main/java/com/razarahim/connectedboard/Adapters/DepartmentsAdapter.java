package com.razarahim.connectedboard.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.razarahim.connectedboard.Models.PostModel;
import com.razarahim.connectedboard.R;
import com.razarahim.connectedboard.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DepartmentsAdapter extends RecyclerView.Adapter<DepartmentsAdapter.ViewHolder> {
    Context context;
    ArrayList<String> itemList;

    DepartmentsAdapterCallbacks callbacks;

    public DepartmentsAdapter(Context context, ArrayList<String> itemList, DepartmentsAdapterCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.callbacks = callbacks;
    }

    public void setItemList(ArrayList<String> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.department_item_layout, parent, false);
        DepartmentsAdapter.ViewHolder viewHolder = new DepartmentsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String model = itemList.get(position);


        holder.department.setText((position + 1) + ") " + model);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onDelete(model);
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView delete;
        TextView department;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            department = itemView.findViewById(R.id.department);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    public interface DepartmentsAdapterCallbacks {

        public void onDelete(String model);

    }
}
