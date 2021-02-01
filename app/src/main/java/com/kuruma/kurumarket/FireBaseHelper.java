package com.kuruma.kurumarket;


import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kuruma.kurumarket.activity.WelcomeActivity;
import com.kuruma.kurumarket.listener.OnPostListener;

import java.util.ArrayList;

import static com.kuruma.kurumarket.Util.isStorageUri;
import static com.kuruma.kurumarket.Util.showToast;
import static com.kuruma.kurumarket.Util.storageUriToName;

public class FireBaseHelper {
    private Activity activity;
    private int successCount;
    private OnPostListener onPostListener;

    public FireBaseHelper(Activity activity){
        this.activity = activity;
    }

    public void setOnPosterListener(OnPostListener onPosterListener){
        this.onPostListener = onPosterListener;
    }

    public void storageDelete(PostInfo postInfo){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        final String id = postInfo.getId();
        ArrayList<String> contentsList = postInfo.getContents();


        for (int i = 0; i < contentsList.size(); i++) {
            String contents = contentsList.get(i);
            if (isStorageUri(contents)) {
                successCount++;
                StorageReference desertRef = storageRef.child("posts" + storageUriToName(contents));

                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        showToast(activity, "게시글을 삭제하였습니다.");
                        successCount--;
                        storeDelete(id);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        showToast(activity, "ERROR");
                    }
                });
            }
        }
        storeDelete(id);
    }

    private void storeDelete(String id) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        if (successCount == 0) {
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(activity, "포스트 게시글을 삭제하였습니다.");
//                            postUpdate();
                            onPostListener.onDelete();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(activity, "포스트 게시글을 삭제하지 못하였습니다.");
                        }
                    });
        }
    }
}
