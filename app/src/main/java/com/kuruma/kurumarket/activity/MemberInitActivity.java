package com.kuruma.kurumarket.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kuruma.kurumarket.MemberInfo;
import com.kuruma.kurumarket.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MemberInitActivity extends AppCompatActivity {

    private static final String TAG = "MemberInitActivity";
    private EditText et_name;
    private EditText et_phone;
    private EditText et_birthdate;
    private EditText et_address;
    private Button btn_confirm_init;
    private Button btn_gallery;
    private Button btn_takepicture;
    private ImageView iv_profile;
    private String profilePath;
    private FirebaseUser user;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);


        et_name = (EditText)findViewById(R.id.et_name);
        et_phone = (EditText)findViewById(R.id.et_phone);
        et_birthdate = (EditText)findViewById(R.id.et_birthdate);
        et_address = (EditText)findViewById(R.id.et_address);


        btn_gallery = findViewById(R.id.btn_gallery);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_gallery:
                        if (ContextCompat.checkSelfPermission(MemberInitActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MemberInitActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    1);
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MemberInitActivity.this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            } else {
                                startToast("권한을 허용해 주세요");
                            }
                        }else{
                            myStartActivity(GalleryActivity.class);
                        }
                        break;
                }


            }
        });




        btn_takepicture = findViewById(R.id.btn_takepicture);
        btn_takepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_takepicture:
                        myStartActivity(CameraActivity.class);
                        break;
                }
            }
        });

        iv_profile = findViewById(R.id.iv_profile);
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_confirm_init:
                        profileUpdate();
                        break;
                    case R.id.iv_profile:
                        ConstraintLayout constraintLayout = findViewById(R.id.cl_gallerytakepicture);
                        if(constraintLayout.getVisibility() == View.VISIBLE){
                            constraintLayout.setVisibility(View.GONE);
                        }else{
                            constraintLayout.setVisibility(View.VISIBLE);
                        }
                        break;


                }
            }
        });







        btn_confirm_init = (Button)findViewById(R.id.btn_confirm_init);


        btn_confirm_init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_confirm_init:
                        profileUpdate();
                        break;
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myStartActivity(GalleryActivity.class);
                } else {
                    startToast("권한을 허용해 주세요");
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode, data);
        switch (requestCode){

            case 0 :{
                if(resultCode == Activity.RESULT_OK){
                    profilePath= data.getStringExtra("profilePath");
                    Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                    iv_profile.setImageBitmap(bmp);
                }
                break;

            }

        }
    }

    private void profileUpdate(){

        final String name = et_name.getText().toString();
        final String phone = et_phone.getText().toString();
        final String birthdate = et_birthdate.getText().toString();
        final String address = et_address.getText().toString();

        if(name.length() > 0 && phone.length() > 9 && birthdate.length() > 5 && address.length() > 0)
        {
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            final StorageReference mountainImagesRef = storageRef.child("users/" +user.getUid() + "/profileimage.jpg");

            if(profilePath == null){
                MemberInfo memberInfo = new MemberInfo(name, phone, birthdate, address);
                uploader(memberInfo);
            } else{
                try{
                    InputStream stream = new FileInputStream(new File(profilePath));
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                MemberInfo memberInfo = new MemberInfo(name, phone, birthdate, address, downloadUri.toString());
                                uploader(memberInfo);

                            } else {
                                Log.e("로그", "실패");
                                startToast("회원정보를 보내는데 실패하였습니다.");
                            }
                        }
                    });
                }catch(FileNotFoundException e){
                    Log.e("로그","에러" + e.toString());
                }
            }
        }else{
            startToast("회원정보를 입력해주세요.");
        }

    }

    private void uploader(MemberInfo memberInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startToast("회원정보 등록을 성공하였습니다.");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startToast("회원정보 등록에 실패하였습니다.");
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    private void myStartActivity(Class c){
        Intent intent = new Intent(MemberInitActivity.this, c);
        startActivityForResult(intent, 0);
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

}