package com.kuruma.kurumarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    private ArrayList<MemberInfo> arrayList;
    private DatabaseReference databaseReference; //파이어베이스 데이터베이스
    EditText et_email;
    EditText et_loginpassword;
    EditText et_loginpasswordcheck;
    Button btn_signup;

    private static final String TAG = "SignupActivity";
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();



        et_email = (EditText)findViewById(R.id.et_loginid);
        et_loginpassword = (EditText)findViewById(R.id.et_loginpw);
        et_loginpasswordcheck = (EditText)findViewById(R.id.et_loginpasswordcheck);
        btn_signup = (Button)findViewById(R.id.btn_login);
        databaseReference = FirebaseDatabase.getInstance().getReference("MemberInfo"); // 파이어베이스 데이터베이스 연동
        arrayList = new ArrayList<>(); // 판매목록을 담아주려는 빈 배열 리스트 생성

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check Password
                if(et_loginpassword.getText().toString().equals(et_loginpasswordcheck.getText().toString()) == false ){
                    Toast.makeText(SignupActivity.this, "비밀번호 불일치", Toast.LENGTH_SHORT).show();
                    return;
                }


                //Empty Space Check
                if (et_email.getText().length() == 0 || et_loginpassword.getText().length() == 0 || et_loginpasswordcheck.getText().length() == 0){
                    Toast.makeText(SignupActivity.this, "비어있는 입력필드가 존재합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //회원가입 절차 진행
                signUp();


                //Duplicate Check at DataBase

                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // 현재 엑티비티를 파괴시킴.


                //Set Sources
//                final MemberInfo memberInfo = new MemberInfo();
//                memberInfo.setTv_email(et_email.getText().toString());
//                memberInfo.setTv_loginpassword(et_loginpassword.getText().toString());

                //Push Data to DataBase
//                databaseReference.push().setValue(memberInfo);

                //redirect

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    //firebase
    private void signUp() {

        String email = ((EditText)findViewById(R.id.et_loginid)).getText().toString();
        String password = ((EditText)findViewById(R.id.et_loginpw)).getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //성공했을 때 로직
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //실패했을 때 로직
                        }

                        // ...
                    }
                });
    }



}