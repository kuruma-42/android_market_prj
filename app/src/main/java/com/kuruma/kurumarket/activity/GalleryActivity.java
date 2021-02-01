package com.kuruma.kurumarket.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuruma.kurumarket.R;
import com.kuruma.kurumarket.adapter.GalleryAdapter;

import java.util.ArrayList;

import static com.kuruma.kurumarket.Util.GALLERY_IMAGE;
import static com.kuruma.kurumarket.Util.GALLERY_VIDEO;
import static com.kuruma.kurumarket.Util.INTENT_MEDIA;
import static com.kuruma.kurumarket.Util.showToast;


public class GalleryActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setToolbarTitle("갤러리");


        if (ContextCompat.checkSelfPermission(GalleryActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GalleryActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            if (ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                showToast(GalleryActivity.this,getResources().getString(R.string.please_grant_permission));
            }
        }else{
            recyclerInit();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recyclerInit();
                } else {
                    finish();
                    showToast(GalleryActivity.this,getResources().getString(R.string.please_grant_permission));
                }
            }
        }
    }

    private void recyclerInit(){

        final int numberOfColumns = 3;

        RecyclerView recyclerView = findViewById(R.id.rv_gallery);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        RecyclerView.Adapter mAdapter = new GalleryAdapter(this, getImagesPath(this));
        recyclerView.setAdapter(mAdapter);
    }

    public ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data;
        String PathOfImage = null;
        String[] projection;

        //Intent putExtra, getExtra 활용 (인텐트로 다른 페이지를 호출하면서 구분할 수 있는 표식을 남긴다.
        Intent intent = getIntent();
        //초기값을 넣어주면 인텐트 값을 안 보냈을 때도 Null Exception이 없음.
        final int media = intent.getIntExtra(INTENT_MEDIA, GALLERY_IMAGE);
        if (media == GALLERY_VIDEO) {
            //Return Video List
            uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME};

        } else {
            //Return Image List
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        }

        cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }

}