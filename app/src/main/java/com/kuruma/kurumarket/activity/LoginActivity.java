package com.kuruma.kurumarket.activity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kuruma.kurumarket.R;

import static com.kuruma.kurumarket.Util.showToast;

public class LoginActivity extends BasicActivity {

    private EditText et_loginid;
    private EditText et_loginpw;
    private Button btn_login;
    private Button btn_reset_password;

    private static final String TAG = "SignupActivity";
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setToolbarTitle("로그인");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        et_loginid = (EditText)findViewById(R.id.et_loginid);
        et_loginpw = (EditText)findViewById(R.id.et_loginpw);
        btn_login = (Button)findViewById(R.id.btn_login);
        btn_reset_password  = (Button)findViewById(R.id.btn_reset_password);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_loginid.getText().toString().length() > 0 && et_loginpw.getText().toString().length() > 0){
                    startLogin();
                }else{
                    showToast(LoginActivity.this,"이메일 혹은 비밀번호를 입력하세요.");
                    return;
                }
            }
        });

        btn_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myStartActivity(PasswordResetActivity.class);
            }
        });
    }

    private void startLogin(){

        String email = ((EditText)findViewById(R.id.et_loginid)).getText().toString();
        String password = ((EditText)findViewById(R.id.et_loginpw)).getText().toString();

        if(email.length() > 0 && password.length() > 0){
            final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
            loaderLayout.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loaderLayout.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                showToast(LoginActivity.this,"로그인에 성공하였습니다.");
                                myStartActivity(WelcomeActivity.class);



                            } else {
                                loaderLayout.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                showToast(LoginActivity.this,"로그인에 실패하였습니다.");
                                // ...
                            }

                            // ...
                        }
                    });
        }

    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(LoginActivity.this, c);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}