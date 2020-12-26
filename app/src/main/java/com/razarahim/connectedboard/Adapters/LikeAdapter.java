package com.razarahim.connectedboard.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razarahim.connectedboard.Models.PostModel;
import com.razarahim.connectedboard.R;
import com.razarahim.connectedboard.Utils.CommonUtils;
import com.razarahim.connectedboard.Utils.SharedPrefs;
import com.razarahim.connectedboard.comments;
import com.razarahim.connectedboard.fullpic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {
    Context context;
    ArrayList<PostModel> itemList;

    PostsAdapterCallbacks callbacks;
    boolean admin;
    ArrayList<PostModel> arrayList;
    ArrayList<String> token_id;


      Boolean likechecker = false;
      String currentUserId;


    public LikeAdapter(Context context, ArrayList<PostModel> itemList, ArrayList<String> key, boolean admin, PostsAdapterCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.callbacks = callbacks;
        this.admin = admin;
        this.token_id =key;
        this.arrayList = new ArrayList<>(itemList);

    }

    public void setItemList(ArrayList<PostModel> itemList,ArrayList<String> data) {
        this.itemList = itemList;
        this.token_id = data;
        notifyDataSetChanged();
    }

    public void updateList(ArrayList<PostModel> itemList,ArrayList<String> data) {
        this.itemList = itemList;
        arrayList.clear();
        this.token_id = data;
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
        LikeAdapter.ViewHolder viewHolder = new LikeAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final PostModel model = itemList.get(position);

        final DatabaseReference likesrefernce =FirebaseDatabase.getInstance().getReference("likes");


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





        holder.commntBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                       Intent intent = new Intent(context,comments.class);
                       intent.putExtra("postKey",token_id.get(position));
                       context.startActivity(intent);
            }
        });
        currentUserId = SharedPrefs.getStudentModel().getRollNumber();
        holder.setLikesbuttonStatus(token_id.get(position),currentUserId);

        holder.setcommentcount(token_id.get(position),currentUserId);

        holder.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                likechecker = true;

                likesrefernce.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (likechecker.equals(true)){
                            if (dataSnapshot.child(token_id.get(position)).hasChild(currentUserId)){
                                likesrefernce.child(token_id.get(position)).child(currentUserId).removeValue();
                                likechecker = false;
                            }else {
                                likesrefernce.child(token_id.get(position)).child(currentUserId).setValue(true);
                                likechecker = false;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });



    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, download, share, delete, playVideo, viewFile;
        TextView description, time,likeCountTextView,commentcount;
        ImageView favBtn,commntBtn;
        DatabaseReference likesrefernce,commentref;


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
            commentcount = itemView.findViewById(R.id.commentcont);
            commntBtn = itemView.findViewById(R.id.comments);


        }

        public void setLikesbuttonStatus(final String postkey, final String CurrenUserId){
            favBtn = itemView.findViewById(R.id.favBtn);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            likesrefernce = FirebaseDatabase.getInstance().getReference("likes");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            final String userId = user.getUid();
            final String likes = " Likes";

            likesrefernce.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(postkey).hasChild(CurrenUserId)){
                       int likescount = (int)dataSnapshot.child(postkey).getChildrenCount();
//
                        favBtn.setImageResource(R.drawable.ic_heart);
                        likeCountTextView.setText(Integer.toString(likescount)+likes);
                    }else {
                      int  likescount = (int)dataSnapshot.child(postkey).getChildrenCount();
                        favBtn.setImageResource(R.drawable.ic_favorite_shadow_24dp);
                        likeCountTextView.setText(Integer.toString(likescount)+likes);
                    }

//                    Collections.sort(itemList, new Comparator<PostModel>() {
//                        @Override
//                        public int compare(PostModel listData, PostModel t1) {
//                            Long ob1 = listData.getTime();
//                            Long ob2 = t1.getTime();
//                            return ob2.compareTo(ob1);
//
//                        }
//                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


        public void setcommentcount(final String postkey, final String CurrenUserId){
            commentref = FirebaseDatabase.getInstance().getReference("Comments");

            final String commentC = "";

            commentref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(postkey).hasChild(CurrenUserId)){
                        int commentcnt = (int)dataSnapshot.child(postkey).getChildrenCount();

                        commentcount.setText(Integer.toString(commentcnt)+commentC);
                    }else {
                        int  commentcnt = (int)dataSnapshot.child(postkey).getChildrenCount();

                        commentcount.setText(Integer.toString(commentcnt)+"");
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }




    public interface PostsAdapterCallbacks {
        public void onDownload(PostModel model);

        public void onShare(PostModel model);

        public void onDelete(PostModel model);

        public void onPlayVideo(PostModel model);

        public void onOpenFile(PostModel model);

       public   void onComments(PostModel model);
    }

    }


