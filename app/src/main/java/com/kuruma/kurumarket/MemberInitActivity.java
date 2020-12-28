package com.kuruma.kurumarket;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class MemberInitActivity extends AppCompatActivity {

    private static final String TAG = "MemberInitActivity";
    private EditText et_name;
    private EditText et_phone;
    private EditText et_birthdate;
    private EditText et_address;
    private Button btn_confirm_init;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);


        et_name = (EditText)findViewById(R.id.et_name);
        et_phone = (EditText)findViewById(R.id.et_phone);
        et_birthdate = (EditText)findViewById(R.id.et_birthdate);
        et_address = (EditText)findViewById(R.id.et_address);

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

    private void profileUpdate(){

        String name = et_name.getText().toString();
        String phone = et_phone.getText().toString();
        String birthdate = et_birthdate.getText().toString();
        String address = et_address.getText().toString();

        if(name.length() > 0 && phone.length() > 9 && birthdate.length() > 5 && address.length() > 0)
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            MemberInfo memberInfo = new MemberInfo(name, phone, birthdate, address);

            if(user != null){
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
                                startToast("회원정보 등록에 실패하였습니다. ");
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            }

        }else{
            startToast("회원정보를 입력해주세요.");
        }

    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(MemberInitActivity.this, c);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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