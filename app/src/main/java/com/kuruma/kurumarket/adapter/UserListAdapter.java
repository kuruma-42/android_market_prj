package com.kuruma.kurumarket.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.kuruma.kurumarket.FireBaseHelper;
import com.kuruma.kurumarket.PostInfo;
import com.kuruma.kurumarket.R;
import com.kuruma.kurumarket.UserInfo;
import com.kuruma.kurumarket.activity.PostActivity;
import com.kuruma.kurumarket.activity.WritePostActivity;
import com.kuruma.kurumarket.listener.OnPostListener;
import com.kuruma.kurumarket.view.ReadContentsView;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.WelcomeViewHolder> {
    private ArrayList<UserInfo> mDataset;
    private Activity activity;



    static class WelcomeViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        WelcomeViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }

    public UserListAdapter(Activity activity, ArrayList<UserInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public UserListAdapter.WelcomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        final WelcomeViewHolder welcomeViewHolder = new WelcomeViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(activity, PostActivity.class);
//                intent.putExtra("postInfo", mDataset.get(welcomeViewHolder.getAdapterPosition()));
//                activity.startActivity(intent);
            }
        });


        return
                welcomeViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final WelcomeViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView photoImageView = cardView.findViewById(R.id.photoImageView);
        TextView nameTextView = cardView.findViewById(R.id.nameTextView);
        TextView addressTextView = cardView.findViewById(R.id.addressTextView);

        UserInfo userInfo = mDataset.get(position);
        if(mDataset.get(position).getPhotoUrl() != null){
            Glide.with(activity).load(mDataset.get(position).getPhotoUrl()).centerCrop().override(500).into(photoImageView);
        }
        nameTextView.setText(userInfo.getName());
        addressTextView.setText(userInfo.getAddress());


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
