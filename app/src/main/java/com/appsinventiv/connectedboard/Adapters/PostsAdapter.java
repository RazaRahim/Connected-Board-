package com.appsinventiv.connectedboard.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.connectedboard.Models.PostModel;
import com.appsinventiv.connectedboard.R;
import com.appsinventiv.connectedboard.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
        TextView description, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            download = itemView.findViewById(R.id.download);
            share = itemView.findViewById(R.id.share);
            description = itemView.findViewById(R.id.description);
            time = itemView.findViewById(R.id.time);
            delete = itemView.findViewById(R.id.delete);
            viewFile = itemView.findViewById(R.id.viewFile);
            playVideo = itemView.findViewById(R.id.playVideo);
        }
    }

    public interface PostsAdapterCallbacks {
        public void onDownload(PostModel model);

        public void onShare(PostModel model);

        public void onDelete(PostModel model);

        public void onPlayVideo(PostModel model);

        public void onOpenFile(PostModel model);
    }
}
