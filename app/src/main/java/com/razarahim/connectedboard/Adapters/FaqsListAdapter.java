package com.razarahim.connectedboard.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.razarahim.connectedboard.Models.FaqsModel;
import com.razarahim.connectedboard.R;
import com.razarahim.connectedboard.Utils.CommonUtils;
import com.razarahim.connectedboard.ui.ViewFaq;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FaqsListAdapter extends RecyclerView.Adapter<FaqsListAdapter.ViewHolder> {
    Context context;
    ArrayList<FaqsModel> itemList;


    public FaqsListAdapter(Context context, ArrayList<FaqsModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemList(ArrayList<FaqsModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.faq_item_layout, parent, false);
        FaqsListAdapter.ViewHolder viewHolder = new FaqsListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final FaqsModel model = itemList.get(position);


        holder.title.setText((position + 1) + ") Title: " + model.getTitle() + "\n" + "    By: " + model.getName());
        holder.time.setText(CommonUtils.getFormattedDate(model.getTime()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ViewFaq.class);
                i.putExtra("id", model.getId());
                context.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            time = itemView.findViewById(R.id.time);
        }
    }


}
