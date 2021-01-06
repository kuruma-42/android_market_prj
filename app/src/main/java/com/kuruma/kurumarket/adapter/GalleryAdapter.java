package com.kuruma.kurumarket.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kuruma.kurumarket.R;

import java.util.ArrayList;

public class GalleryAdapter  extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private ArrayList<String> mDataset;
    private Activity activity;


    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public GalleryAdapter(Activity activity, ArrayList<String> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
    }

    @NonNull
    @Override
    public GalleryAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        return new GalleryViewHolder(cardView);
    }


    @Override
    public void onBindViewHolder(@NonNull final GalleryViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("profilePath", mDataset.get(holder.getAdapterPosition()));
                activity.setResult(Activity.RESULT_OK, resultIntent);
                activity.finish();
            }
        });

        ImageView iv_gallery = cardView.findViewById(R.id.iv_gallery);
        Glide.with(activity).load(mDataset.get(position)).centerCrop().override(500).into(iv_gallery);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

//public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
//    private String[] mDataset;
//
//    public static class MyViewHolder extends RecyclerView.ViewHolder {
//        // each data item is just a string in this case
//        public CardView cardView;
//        public MyViewHolder(CardView v) {
//            super(v);
//            cardView = v;
//        }
//    }
//
//    public GalleryAdapter(String[] myDataset) {
//        mDataset = myDataset;
//    }
//
//
//    @Override
//    public GalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
//        // create a new view
//        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
//            //...
//        MyViewHolder vh = new MyViewHolder(v);
//        return vh;
//    }
//
//
//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//        TextView textView = holder.cardView.findViewById(R.id.tv_gallery);
//        textView.setText(mDataset[position]);
//
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return mDataset.length;
//    }
//}
