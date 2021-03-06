package com.kuruma.kurumarket.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.kuruma.kurumarket.FireBaseHelper;
import com.kuruma.kurumarket.PostInfo;
import com.kuruma.kurumarket.R;
import com.kuruma.kurumarket.activity.PostActivity;
import com.kuruma.kurumarket.activity.WritePostActivity;
import com.kuruma.kurumarket.listener.OnPostListener;
import com.kuruma.kurumarket.view.ReadContentsView;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.WelcomeViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private ArrayList<ArrayList<SimpleExoPlayer>> playerArrayListArrayList = new ArrayList<>();
    private FireBaseHelper fireBaseHelper;
    private final int MORE_INDEX = 2;



    static class WelcomeViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        WelcomeViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }

    public HomeAdapter(Activity activity, ArrayList<PostInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
        fireBaseHelper = new FireBaseHelper(activity);
    }

    public void setOnPostListener(OnPostListener onPostListener){
        fireBaseHelper.setOnPosterListener(onPostListener);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public HomeAdapter.WelcomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final WelcomeViewHolder welcomeViewHolder = new WelcomeViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PostActivity.class);
                intent.putExtra("postInfo", mDataset.get(welcomeViewHolder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });

        cardView.findViewById(R.id.cv_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, welcomeViewHolder.getAdapterPosition());
            }
        });

        return
                welcomeViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final WelcomeViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView tv_board_title = cardView.findViewById(R.id.tv_board_title);

        PostInfo postInfo = mDataset.get(position);
        tv_board_title.setText(postInfo.getTitle());

        ReadContentsView readContentsView = cardView.findViewById(R.id.readContentsView);

        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);


        if(contentsLayout.getTag() == null || !contentsLayout.getTag().equals(postInfo)){
            contentsLayout.setTag(postInfo);
            contentsLayout.removeAllViews();

            readContentsView.setMoreIndex(MORE_INDEX);
            readContentsView.setPostInfo(postInfo);

            ArrayList<SimpleExoPlayer> playerArrayList = readContentsView.getPlayerArrayList();

            if(playerArrayList != null){
                playerArrayListArrayList.add(playerArrayList);
            }

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void showPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dropdown_menu_modify:
                        //수정로직
                        myStartActivity(WritePostActivity.class, mDataset.get(position));
                        return true;
                    case R.id.dropdown_menu_delete:
                        //삭제로직
                        fireBaseHelper.storageDelete(mDataset.get(position));
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post, popup.getMenu());
        popup.show();
    }


    private void myStartActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(activity, c);
        intent.putExtra("postInfo", postInfo);
        activity.startActivity(intent);
    }

    public void playerStop(){
        for(int i = 0; i<playerArrayListArrayList.size(); i++){
            ArrayList<SimpleExoPlayer> playerArrayList = playerArrayListArrayList.get(i);
            for(int ii = 0; ii < playerArrayList.size(); ii++){
                SimpleExoPlayer player = playerArrayList.get(ii);
                if(player.getPlayWhenReady()){
                    player.setPlayWhenReady(false);
                }
            }
        }
    }
}
