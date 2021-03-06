package com.razarahim.connectedboard.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.razarahim.connectedboard.FavDB;
import com.razarahim.connectedboard.Models.PostModel;
import com.razarahim.connectedboard.R;
import com.razarahim.connectedboard.Utils.CommonUtils;
import com.bumptech.glide.Glide;
import com.razarahim.connectedboard.comments;
import com.razarahim.connectedboard.fullpic;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    Context context;
    ArrayList<PostModel> itemList;

    PostsAdapterCallbacks callbacks;
    boolean admin;
    ArrayList<PostModel> arrayList;



    public PostsAdapter(Context context, ArrayList<PostModel> itemList, boolean admin, PostsAdapterCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.callbacks = callbacks;
        this.admin = admin;
        this.arrayList = new ArrayList<>(itemList);

    }

        public void setItemList(ArrayList<PostModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public void updateList(ArrayList<PostModel> itemList) {
        this.itemList = itemList;
        arrayList.clear();
        arrayList.addAll(itemList);
        notifyDataSetChanged();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        itemList.clear();
        if (charText.length() == 0) {
            itemList.addAll(arrayList);
        } else {
            for (PostModel item : arrayList) {
                if (item.getDescription().toLowerCase().contains(charText.toLowerCase())) {

                    itemList.add(item);
                }

            }


        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item_layout, parent, false);
        PostsAdapter.ViewHolder viewHolder = new PostsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final PostModel model = itemList.get(position);

        if (admin) {
            holder.download.setVisibility(View.GONE);
            holder.share.setVisibility(View.GONE);
            holder.delete.setVisibility(View.VISIBLE);

        } else {
            holder.download.setVisibility(View.VISIBLE);
            holder.share.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.GONE);
        }

        if (model.getType().equalsIgnoreCase("image")) {
            holder.playVideo.setVisibility(View.GONE);
            holder.viewFile.setVisibility(View.GONE);
            Glide.with(context).load(model.getUrl()).into(holder.image);

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, fullpic.class);
                    intent.putExtra("imag",model.getUrl());
                    context.startActivity(intent);
                }
            });


        } else if (model.getType().equalsIgnoreCase("video")) {
            holder.playVideo.setVisibility(View.VISIBLE);
            holder.viewFile.setVisibility(View.GONE);
            Glide.with(context).load(model.getVideoImgUrl()).into(holder.image);
        } else if (model.getType().equalsIgnoreCase("pdf")) {
            holder.playVideo.setVisibility(View.GONE);
            holder.viewFile.setVisibility(View.VISIBLE);
            Glide.with(context).load(model.getVideoImgUrl()).into(holder.image);
        }
        String sourceString = "<b>Description: </b> " + model.getDescription();
        holder.description.setText(Html.fromHtml(sourceString));

        holder.time.setText(CommonUtils.getFormattedDate(model.getTime()));

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onShare(model);
            }
        });
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onDownload(model);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onDelete(model);
            }
        });

        holder.playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onPlayVideo(model);
            }
        });
        holder.viewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onOpenFile(model);
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, download, share, delete, playVideo, viewFile;
        TextView description, time,likeCountTextView;
        ImageView favBtn,commntBtn;



        @SuppressLint("WrongViewCast")
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            download = itemView.findViewById(R.id.download);
            share = itemView.findViewById(R.id.share);
            description = itemView.findViewById(R.id.description);
            time = itemView.findViewById(R.id.time);
            delete = itemView.findViewById(R.id.delete);
            viewFile = itemView.findViewById(R.id.viewFile);
            playVideo = itemView.findViewById(R.id.playVideo);

            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);

            commntBtn = itemView.findViewById(R.id.comments);




        }
//
    }




    public interface PostsAdapterCallbacks {
        public void onDownload(PostModel model);

        public void onShare(PostModel model);

        public void onDelete(PostModel model);

        public void onPlayVideo(PostModel model);

        public void onOpenFile(PostModel model);

       public   void onLike(PostModel model);
    }


    }


