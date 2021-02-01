package com.kuruma.kurumarket.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kuruma.kurumarket.FireBaseHelper;
import com.kuruma.kurumarket.PostInfo;
import com.kuruma.kurumarket.R;
import com.kuruma.kurumarket.adapter.WelcomeAdapter;
import com.kuruma.kurumarket.listener.OnPostListener;

import java.util.ArrayList;
import java.util.Date;

import static com.kuruma.kurumarket.Util.isStorageUri;
import static com.kuruma.kurumarket.Util.showToast;
import static com.kuruma.kurumarket.Util.storageUriToName;

public class WelcomeActivity extends BasicActivity {

    private static final String TAG = "WelcomeActivity";
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private WelcomeAdapter welcomeAdapter;
    private ArrayList<PostInfo> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setToolbarTitle(getResources().getString(R.string.app_name));

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //When Login doesn't work
        if (firebaseUser == null) {
            myStartActivity(SignupActivity.class);
        } else {

            firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference docRef = firebaseFirestore.collection("users").document(firebaseUser.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                myStartActivity(MemberInitActivity.class);
                                Log.d(TAG, "No such document");
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }

        postList = new ArrayList<>();
        welcomeAdapter = new WelcomeAdapter(WelcomeActivity.this, postList);
        welcomeAdapter.setOnPostListener(onPostListener);


        RecyclerView recyclerView = findViewById(R.id.rv_welcome);
        findViewById(R.id.btn_board_add).setOnClickListener(onClickListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(WelcomeActivity.this));
        recyclerView.setAdapter(welcomeAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        postUpdate();
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_board_add:
                    myStartActivity(WritePostActivity.class);
                    break;

            }
        }
    };

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete() {
            postUpdate();
            Log.e("로그","삭제 성공");
        }

        @Override
        public void onModify() {
            Log.e("로그","수정 성공");
        }
    };

    private void postUpdate() {
        if (firebaseUser != null) {
            CollectionReference collectionReference = firebaseFirestore.collection("posts");
            collectionReference
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                postList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    postList.add(new PostInfo(
                                            document.getData().get("title").toString(),
                                            (ArrayList<String>) document.getData().get("contents"),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getId()
                                    ));
                                }
                                welcomeAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }


    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }



}


