package com.razarahim.connectedboard.Adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.razarahim.connectedboard.Models.PostModel;
import com.razarahim.connectedboard.Models.commentModel;
import com.razarahim.connectedboard.R;

import java.util.ArrayList;
import java.util.Locale;

public class commentAdapter extends FirebaseRecyclerAdapter <commentModel,commentAdapter.myViewHolder> {

    public commentAdapter(@NonNull FirebaseRecyclerOptions<commentModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull commentModel model) {

        holder.name.setText(model.getUsername());
        holder.mesg.setText(model.getUsermsg());
        holder.id.setText("Roll Number : ".concat(model.getUid()));
        holder.time.setText(model.getTime());
        holder.date.setText(model.getDate());
        Glide.with(holder.imageView.getContext()).load(model.getUserImage()).into(holder.imageView);


    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentrow,parent,false);
        return new myViewHolder(view);

    }

    class myViewHolder extends RecyclerView.ViewHolder{

    ImageView imageView;
    TextView name,id,mesg,date,time;

    public myViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imagec);

        name = itemView.findViewById(R.id.userName);
        id = itemView.findViewById(R.id.id);
        mesg = itemView.findViewById(R.id.userComments);
        date=itemView.findViewById(R.id.date);
        time = itemView.findViewById(R.id.time);

    }
}

}
