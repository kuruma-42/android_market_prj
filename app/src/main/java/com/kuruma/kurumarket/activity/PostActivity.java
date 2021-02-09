package com.kuruma.kurumarket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.kuruma.kurumarket.FireBaseHelper;
import com.kuruma.kurumarket.PostInfo;
import com.kuruma.kurumarket.R;
import com.kuruma.kurumarket.listener.OnPostListener;
import com.kuruma.kurumarket.view.ReadContentsView;

public class PostActivity extends BasicActivity{
    private PostInfo postInfo;
    private FireBaseHelper fireBaseHelper;
    private ReadContentsView readContentsView;
    private LinearLayout contentsLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");

        contentsLayout = findViewById(R.id.contentsLayout);
        readContentsView = findViewById(R.id.readContentsView);

        fireBaseHelper = new FireBaseHelper(this);
        fireBaseHelper.setOnPosterListener(onPostListener);
        uiUpdate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    Log.e("로그", "로그 수정");
                    postInfo = (PostInfo)data.getSerializableExtra("postInfo");
                    contentsLayout.removeAllViews();
                    uiUpdate();
                }
                break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dropdown_menu_delete:
                fireBaseHelper.storageDelete(postInfo);
                myStartActivity(WelcomeActivity.class, postInfo);
                return true;

            case R.id.dropdown_menu_modify:
                myStartActivity(WritePostActivity.class, postInfo);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(PostInfo postInfo) {
            Log.e("로그", "삭제 성공");
        }

        @Override
        public void onModify() {
            Log.e("로그", "수정 성공");
        }
    };

    private void uiUpdate(){
        setToolbarTitle(postInfo.getTitle());
        readContentsView.setPostInfo(postInfo);
    }

    private void myStartActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("postInfo", postInfo);
        startActivityForResult(intent, 0);
    }


}
