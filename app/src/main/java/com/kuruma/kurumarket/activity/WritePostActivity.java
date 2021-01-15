package com.kuruma.kurumarket.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kuruma.kurumarket.R;
import com.kuruma.kurumarket.PostInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

public class WritePostActivity extends BasicActivity{

    private static final String TAG = "WritePostActivity";
    private FirebaseUser user;
    private LinearLayout parent;
    private ArrayList<String> pathList = new ArrayList<>();
    private RelativeLayout buttonsBackgroundLayout;
    private int pathCount, successCount;
    private EditText selectedEditText;
    private ImageView selectedImageView;
    private RelativeLayout loaderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_write_post);

    parent = findViewById(R.id.contentsLayout);
    buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
    loaderLayout = findViewById(R.id.loaderLayout);

    buttonsBackgroundLayout.setOnClickListener(onClickListener);
    findViewById(R.id.btn_board_upload).setOnClickListener(onClickListener);
    findViewById(R.id.btn_board_image).setOnClickListener(onClickListener);
    findViewById(R.id.btn_board_video).setOnClickListener(onClickListener);
    findViewById(R.id.btn_image_modify).setOnClickListener(onClickListener);
    findViewById(R.id.btn_video_modify).setOnClickListener(onClickListener);
    findViewById(R.id.btn_contents_delete).setOnClickListener(onClickListener);
    findViewById(R.id.et_board_contents).setOnFocusChangeListener(onFocusChangeListener);
    findViewById(R.id.et_board_title).setOnFocusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                selectedEditText = null;
            }
        }
    });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode, data);
        switch (requestCode){

            case 0 :{
                if(resultCode == Activity.RESULT_OK){
                    String profilePath= data.getStringExtra("profilePath");
                    pathList.add(profilePath);

                    //ViewGroup생성 및 레이아웃 세부 설정
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

                    LinearLayout linearLayout = new LinearLayout(WritePostActivity.this);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    if(selectedEditText == null){
                        parent.addView(linearLayout);
                    } else{
                        for(int i = 0; i < parent.getChildCount(); i ++){
                        if(parent.getChildAt(i) == selectedEditText.getParent()){
                            //현재 셀렉트 에딧텍스트 다음에 리니어레이아웃을 생성
                            parent.addView(linearLayout, i + 1);
                            break;
                        }
                    }

                    }



                    ImageView imageView = new ImageView(WritePostActivity.this);
                    //Layout Param 이미지 뷰에
                    imageView.setLayoutParams(layoutParams);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) v;
                        }
                    });
                    Glide.with(this).load(profilePath).override(1000).into(imageView);
                    //parent에 imageview를 넣어줘야 함.
                    linearLayout.addView(imageView);

                    //EditText 생성
                    EditText editText = new EditText(WritePostActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    editText.setHint("내용");
                    //Focus 위치를 기반으로 리니어 레이아웃을 생성한다.
                    editText.setOnFocusChangeListener(onFocusChangeListener);
                    linearLayout.addView(editText);
                }
                break;
            }

            case 1:
                if(resultCode == Activity.RESULT_OK){
                    String profilePath= data.getStringExtra("profilePath");
                    Glide.with(this).load(profilePath).override(1000).into(selectedImageView);
                }
                break;

        }
    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                selectedEditText = (EditText) v;
            }
        }
    };


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_board_upload:
                    storageUpload();
                    break;
                case R.id.btn_board_image:
                    myStartActivity(GalleryActivity.class, "image", 0);
                    break;
                case R.id.btn_board_video:
                    myStartActivity(GalleryActivity.class, "video", 0);
                    break;
                case R.id.buttonsBackgroundLayout:
                    if(buttonsBackgroundLayout.getVisibility() == View.VISIBLE){
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.btn_image_modify:
                    myStartActivity(GalleryActivity.class, "image", 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.btn_video_modify:
                    myStartActivity(GalleryActivity.class, "video", 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.btn_contents_delete:
                    parent.removeView((View) selectedImageView.getParent());
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;


            }
        }
    };


    private void storageUpload(){

        final String title = ((EditText)findViewById(R.id.et_board_title)).getText().toString();
        if(title.length() > 0)
        {
            loaderLayout.setVisibility(View.VISIBLE);
            ArrayList<String> contentList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            final DocumentReference documentReference = firebaseFirestore.collection("posts").document();

            for(int i = 0; i < parent.getChildCount(); i++){
                LinearLayout linearLayout = (LinearLayout)parent.getChildAt(i);
                for(int ii = 0; ii < linearLayout.getChildCount(); ii++){
                    View view = linearLayout.getChildAt(ii);
                    if(view instanceof EditText){
                        String text = ((EditText)view).getText().toString();
                        if(text.length() > 0 ){
                            contentList.add(text);
                        }
                    } else {
                        contentList.add(pathList.get(pathCount));
                        String[] pathArray = pathList.get(pathCount).split("₩₩.");
                        final StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/"+ pathCount +""+pathArray[pathArray.length - 1]);
                        try{
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index",""+(contentList.size()-1)).build();
                            UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            contentList.set(index,uri.toString());
                                            successCount++;
                                            if(pathList.size() == successCount);{
                                                PostInfo postInfo = new PostInfo(title, contentList, user.getUid(), new Date());
                                                storeUpload(documentReference, postInfo);
                                                for(int k = 0; k < contentList.size(); k++){
                                                    Log.e("로그","콘텐츠 : " + contentList);
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        }catch(FileNotFoundException e){
                            Log.e("로그","에러" + e.toString());
                        }
                        pathCount++;
                    }
                }

            }
            if(pathList.size() == 0){
                PostInfo postInfo = new PostInfo(title, contentList, user.getUid(), new Date());
                storeUpload(documentReference, postInfo);
            }

        }else{
            startToast("제목을 입력해주세요.");
        }

    }

    private void storeUpload(DocumentReference documentReference, PostInfo postInfo){

    documentReference.set(postInfo)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    loaderLayout.setVisibility(View.GONE);
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);
                    loaderLayout.setVisibility(View.GONE);
                }
            });
}

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private void myStartActivity(Class c, String media, int requestCode){
        Intent intent = new Intent(this, c);
        intent.putExtra("media", media);
        startActivityForResult(intent,requestCode);
    }



}
