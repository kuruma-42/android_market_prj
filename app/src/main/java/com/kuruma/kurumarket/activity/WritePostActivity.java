package com.kuruma.kurumarket.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.kuruma.kurumarket.view.ContentsItemView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import static com.kuruma.kurumarket.Util.GALLERY_IMAGE;
import static com.kuruma.kurumarket.Util.GALLERY_VIDEO;
import static com.kuruma.kurumarket.Util.INTENT_MEDIA;
import static com.kuruma.kurumarket.Util.INTENT_PATH;
import static com.kuruma.kurumarket.Util.isImageFile;
import static com.kuruma.kurumarket.Util.isStorageUri;
import static com.kuruma.kurumarket.Util.isVideoFile;
import static com.kuruma.kurumarket.Util.showToast;
import static com.kuruma.kurumarket.Util.storageUriToName;

public class WritePostActivity extends BasicActivity {

    private static final String TAG = "WritePostActivity";
    private FirebaseUser user;
    private LinearLayout parent;
    private StorageReference storageRef;
    private ArrayList<String> pathList = new ArrayList<>();
    private RelativeLayout buttonsBackgroundLayout;
    private int pathCount, successCount;
    private EditText selectedEditText;
    private ImageView selectedImageView;
    private RelativeLayout loaderLayout;
    private EditText et_board_contents;
    private EditText et_board_title;
    private PostInfo postInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);
        setToolbarTitle("게시글 작성");

        parent = findViewById(R.id.contentsLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
        loaderLayout = findViewById(R.id.loaderLayout);
        et_board_contents = findViewById(R.id.et_board_contents);
        et_board_title = findViewById(R.id.et_board_title);

        findViewById(R.id.btn_board_upload).setOnClickListener(onClickListener);
        findViewById(R.id.btn_board_image).setOnClickListener(onClickListener);
        findViewById(R.id.btn_board_video).setOnClickListener(onClickListener);
        findViewById(R.id.btn_image_modify).setOnClickListener(onClickListener);
        findViewById(R.id.btn_video_modify).setOnClickListener(onClickListener);
        findViewById(R.id.btn_contents_delete).setOnClickListener(onClickListener);

        buttonsBackgroundLayout.setOnClickListener(onClickListener);
        et_board_contents.setOnFocusChangeListener(onFocusChangeListener);
        et_board_title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    selectedEditText = null;
                }
            }
        });
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
        postInit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.add(path);

                    ContentsItemView contentsItemView = new ContentsItemView(this);

                    if (selectedEditText == null) {
                        parent.addView(contentsItemView);
                    } else {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            if (parent.getChildAt(i) == selectedEditText.getParent()) {
                                //현재 셀렉트 에딧텍스트 다음에 리니어레이아웃을 생성
                                parent.addView(contentsItemView, i + 1);
                                break;
                            }
                        }
                    }

                    contentsItemView.setImage(path);
                    contentsItemView.setOnclickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) v;
                        }
                    });
                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                }
                break;
            }

            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.set(parent.indexOfChild((View) selectedImageView.getParent()) - 1, path);
                    Glide.with(this).load(path).override(1000).into(selectedImageView);

                }
                break;

        }
    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                selectedEditText = (EditText) v;
            }
        }
    };


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_board_upload:
                    storageUpload();
                    break;
                case R.id.btn_board_image:
                    myStartActivity(GalleryActivity.class, GALLERY_IMAGE, 0);
                    break;
                case R.id.btn_board_video:
                    myStartActivity(GalleryActivity.class, GALLERY_VIDEO, 0);
                    break;
                case R.id.buttonsBackgroundLayout:
                    if (buttonsBackgroundLayout.getVisibility() == View.VISIBLE) {
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.btn_image_modify:
                    myStartActivity(GalleryActivity.class, GALLERY_IMAGE, 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.btn_video_modify:
                    myStartActivity(GalleryActivity.class, GALLERY_VIDEO, 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.btn_contents_delete:
                    final View selectedView = (View) selectedImageView.getParent();
                    String path = pathList.get(parent.indexOfChild(selectedView)-1);
                    if(isStorageUri(path)){
                        StorageReference desertRef = storageRef.child("posts"+storageUriToName(path));
                        Log.e("로그 :", "이름 :" + storageUriToName(pathList.get(parent.indexOfChild(selectedView) - 1)));

                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                                showToast(WritePostActivity.this,"파일을 삭제하였습니다.");
                                pathList.remove(parent.indexOfChild(selectedView) - 1);
                                parent.removeView(selectedView);
                                buttonsBackgroundLayout.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                                showToast(WritePostActivity.this,"파일을 삭제하는데 실패하였습니다.");
                            }
                        });
                    }else{
                        pathList.remove(parent.indexOfChild(selectedView) - 1);
                        parent.removeView(selectedView);
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };


    private void storageUpload() {

        final String title = ((EditText) findViewById(R.id.et_board_title)).getText().toString();
        if (title.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            final ArrayList<String> contentList = new ArrayList<>();
            final ArrayList<String> formatList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference dr;


            final DocumentReference documentReference = postInfo == null ? firebaseFirestore.collection("posts").document() : firebaseFirestore.collection("posts").document(postInfo.getId());
            final Date date = postInfo == null ? new Date() : postInfo.getCreatedAt();

            for (int i = 0; i < parent.getChildCount(); i++) {
                LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
                for (int ii = 0; ii < linearLayout.getChildCount(); ii++) {
                    View view = linearLayout.getChildAt(ii);
                    if (view instanceof EditText) {
                        String text = ((EditText) view).getText().toString();
                        if (text.length() > 0) {
                            contentList.add(text);
                            formatList.add("text");
                        }
                    } else if(!isStorageUri(pathList.get(pathCount))) {
                        String path = pathList.get(pathCount);
                        successCount++;
                        contentList.add(path);

                        if (isImageFile(path)) {
                            formatList.add("image");
                        } else if (isVideoFile(path)) {
                            formatList.add("video");
                        } else {
                            formatList.add("text");
                        }

                        String[] pathArray = path.split("\\.");
                        final StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + "." + pathArray[pathArray.length - 1]);
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentList.size() - 1)).build();
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
                                            successCount--;
                                            contentList.set(index, uri.toString());
                                            if (successCount == 0) ;
                                            {
                                                PostInfo postInfo = new PostInfo(title, contentList, formatList, user.getUid(), date);
                                                storeUpload(documentReference, postInfo);
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (FileNotFoundException e) {
                            Log.e("로그", "에러" + e.toString());
                        }
                        pathCount++;

                    } else if (isStorageUri(pathList.get(pathCount))) {
                        String path = pathList.get(pathCount);
                        contentList.add(path);

                        String[] okFileExtensions =  new String[] {"jpg", "png", "gif","jpeg"};
                        for(int j = 0; j < okFileExtensions.length; j++){
                            if(path.contains(okFileExtensions[j])){
                                formatList.add("image");
                                break;
                            }
                            if(j == okFileExtensions.length - 1){
                                formatList.add("video");
                            }
                        }
                        pathCount++;
                    }

                }

            }
            if (successCount == 0) {
                storeUpload(documentReference, new PostInfo(title, contentList, formatList, user.getUid(), date));
            }

        } else {
            showToast(WritePostActivity.this,"제목을 입력해주세요.");
        }

    }

    private void storeUpload(DocumentReference documentReference, final PostInfo postInfo) {

        documentReference.set(postInfo.getPostInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        loaderLayout.setVisibility(View.GONE);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("postInfo", postInfo);
                        setResult(Activity.RESULT_OK, resultIntent);
                        Log.e("로그", "로그 수정!");
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

    private void postInit() {
        if (postInfo != null) {
            et_board_title.setText(postInfo.getTitle());
            ArrayList<String> contentsList = postInfo.getContents();

            for (int i = 0; i < contentsList.size(); i++) {
                String contents = contentsList.get(i);
                if (isStorageUri(contents)) {
                    pathList.add(contents);

                    ContentsItemView contentsItemView = new ContentsItemView(this);

                    parent.addView(contentsItemView);

                    contentsItemView.setImage(contents);
                    contentsItemView.setOnclickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) v;
                        }
                    });
                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);

                    if (i < contentsList.size() - 1) {
                        String nextContents = contentsList.get(i + 1);

//                        if (!Patterns.WEB_URL.matcher(nextContents).matches() || !nextContents.contains("https://firebasestorage.googleapis.com/v0/b/kurumarket-f4a15.appspot.com/o/posts")) {
//                            contentsItemView.setText(nextContents);
//                        }
//                         조건이 틀림 || 가 && 으로 바뀜
                        if (!isStorageUri(nextContents)) {
                            contentsItemView.setText(nextContents);
                        }
                    }

                } else if (i == 0) {
                    et_board_contents.setText(contents);
                }
            }
        }
    }



    private void myStartActivity(Class c, int media, int requestCode) {
        Intent intent = new Intent(this, c);
        intent.putExtra(INTENT_MEDIA, media);
        startActivityForResult(intent, requestCode);
    }


}
