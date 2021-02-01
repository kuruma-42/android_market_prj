package com.kuruma.kurumarket.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.kuruma.kurumarket.PostInfo;
import com.kuruma.kurumarket.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.kuruma.kurumarket.Util.isStorageUri;

public class ReadContentsView extends LinearLayout {
    private Context context;
    private int moreIndex = -1;

    public ReadContentsView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ReadContentsView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        initView();
    }

    private void initView() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Merge를 사용했을 때는 아래의 코드를 쓴다.
        layoutInflater.inflate(R.layout.view_post, this, true);
//        addView(layoutInflater.inflate(R.layout.view_post, this, false));

    }

    public void setMoreIndex(int moreIndex){
        this.moreIndex = moreIndex;
    }

    public void setPostInfo(PostInfo postInfo){
//        TextView tv_board_title =findViewById(R.id.tv_board_title);
//        tv_board_title.setText(postInfo.getTitle());

        TextView tv_createdAt = findViewById(R.id.tv_createdAt);
        tv_createdAt.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(postInfo.getCreatedAt()));

        LinearLayout contentsLayout = findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentsList = postInfo.getContents();

        for (int i = 0; i < contentsList.size(); i++) {
            if(i == moreIndex ){
                //create textview
                TextView textView = new TextView(context);
                textView.setLayoutParams(layoutParams);
                textView.setText("더 보기");
                contentsLayout.addView(textView);
                break;
            }
            String contents = contentsList.get(i);
            //check URL patterns in contents
            //User가 게시글에 URL 패턴을 칠 수 도 있으니 && 조건을 추가한다.
            if (isStorageUri(contents))
            {
                //create imageView
                ImageView imageView = new ImageView(context);
                imageView.setLayoutParams(layoutParams);
                //이미지 크기 맞추기
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                contentsLayout.addView(imageView);
                Glide.with(this).load(contents).override(1000).thumbnail(0.1f).into(imageView);
            } else {
                //create textview
                TextView textView = new TextView(context);
                textView.setLayoutParams(layoutParams);
                textView.setText(contents);
                textView.setTextColor(Color.rgb(0,0,0));
                contentsLayout.addView(textView);
            }
        }
    }
}

