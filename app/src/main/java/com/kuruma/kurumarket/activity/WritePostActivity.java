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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kuruma.kurumarket.MemberInfo;
import com.kuruma.kurumarket.R;
import com.kuruma.kurumarket.WriteInfo;

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
    private int pathCount, successCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_write_post);

    parent = findViewById(R.id.contentsLayout);
    findViewById(R.id.btn_board_upload).setOnClickListener(onClickListener);
    findViewById(R.id.btn_board_image).setOnClickListener(onClickListener);
    findViewById(R.id.btn_board_video).setOnClickListener(onClickListener);
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

                    ImageView imageView = new ImageView(WritePostActivity.this);
                    //Layout Param 이미지 뷰에
                    imageView.setLayoutParams(layoutParams);
                    Glide.with(this).load(profilePath).override(1000).into(imageView);
                    //parent에 imageview를 넣어줘야 함.
                    parent.addView(imageView);

                    //EditText 생성
                    EditText editText = new EditText(WritePostActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    parent.addView(editText);
                }
                break;

            }

        }
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_board_upload:
                    storageUpload();
                    break;
                case R.id.btn_board_image:
                    myStartActivity(GalleryActivity.class, "image");
                    break;
                case R.id.btn_board_video:
                    myStartActivity(GalleryActivity.class, "video");
                    break;


            }
        }
    };


    private void storageUpload(){

        final String title = ((EditText)findViewById(R.id.et_board_title)).getText().toString();
        if(title.length() > 0)
        {
            ArrayList<String> contentList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            for(int i = 0; i < parent.getChildCount(); i++){
                View view = parent.getChildAt(i);
                if(view instanceof EditText){
                    String text = ((EditText)view).getText().toString();
                    if(text.length() > 0 ){
                        contentList.add(text);
                    }
                } else {
                    contentList.add(pathList.get(pathCount));

                    final StorageReference mountainImagesRef = storageRef.child("users/" +user.getUid() + "/"+ pathCount +".jpg");
                    try{
                        InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                        StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index",""+( contentList.size()-1)).build();
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
                                            WriteInfo writeInfo = new WriteInfo(title, contentList, user.getUid(), new Date());
                                            storeUpload(writeInfo);
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

        }else{
            startToast("회원정보를 입력해주세요.");
        }

    }

    private void storeUpload(WriteInfo writeInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").add(writeInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private void myStartActivity(Class c, String media){
        Intent intent = new Intent(this, c);
        intent.putExtra("media", media);
        startActivityForResult(intent,0);
    }



}
