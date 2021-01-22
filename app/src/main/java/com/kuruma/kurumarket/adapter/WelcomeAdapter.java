package com.kuruma.kurumarket.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuruma.kurumarket.PostInfo;
import com.kuruma.kurumarket.R;
import com.kuruma.kurumarket.listener.OnPostListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class WelcomeAdapter extends RecyclerView.Adapter<WelcomeAdapter.WelcomeViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private OnPostListener onPostListener;


    static class WelcomeViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        WelcomeViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }

    public WelcomeAdapter(Activity activity, ArrayList<PostInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
    }

    //리스너 생성
    public void setOnPostListener(OnPostListener onPostListener){
        this.onPostListener = onPostListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public WelcomeAdapter.WelcomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final WelcomeViewHolder welcomeViewHolder = new WelcomeViewHolder(cardView);
        Log.e("로그 :", "로그:" + viewType);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        tv_board_title.setText(mDataset.get(position).getTitle());

        TextView tv_createdAt = cardView.findViewById(R.id.tv_createdAt);
        tv_createdAt.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));


        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentsList = mDataset.get(position).getContents();


        if(contentsLayout.getTag() == null || !contentsLayout.getTag().equals(contentsList)){
            contentsLayout.setTag(contentsList);
            contentsLayout.removeAllViews();
            final int MORE_INDEX = 2;

            for (int i = 0; i < contentsList.size(); i++) {
                if(i == MORE_INDEX ){
                    //create textview
                    TextView textView = new TextView(activity);
                    textView.setLayoutParams(layoutParams);
                    textView.setText("더 보기");
                    contentsLayout.addView(textView);
                    break;
                }
                String contents = contentsList.get(i);
                //check URL patterns in contents
                //User가 게시글에 URL 패턴을 칠 수 도 있으니 && 조건을 추가한다.
                if (Patterns.WEB_URL.matcher(contents).matches() && contents.contains("https://firebasestorage.googleapis.com/v0/b/kurumarket-f4a15.appspot.com/o/posts"))
                {
                    //create imageView
                    ImageView imageView = new ImageView(activity);
                    imageView.setLayoutParams(layoutParams);
                    //이미지 크기 맞추기
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    contentsLayout.addView(imageView);
                    Glide.with(activity).load(contents).override(1000).thumbnail(0.1f).into(imageView);
                } else {
                    //create textview
                    TextView textView = new TextView(activity);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(contents);
                    textView.setTextColor(Color.rgb(0,0,0));
                    contentsLayout.addView(textView);
                }
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
                        onPostListener.onModify(position);
                        return true;
                    case R.id.dropdown_menu_delete:
                        //삭제로직
                        onPostListener.onDelete(position);
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


    private void startToast(String msg){ Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();}

}
