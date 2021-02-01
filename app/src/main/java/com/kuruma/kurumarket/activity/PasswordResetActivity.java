package com.kuruma.kurumarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kuruma.kurumarket.R;

import static com.kuruma.kurumarket.Util.showToast;

public class PasswordResetActivity extends BasicActivity {

    private Button btn_send_email;

    private static final String TAG = "SignupActivity";
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        setToolbarTitle("비밀번호 재설정");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btn_send_email = (Button)findViewById(R.id.btn_send_email);

        btn_send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    private void sendEmail(){

        String email = ((EditText)findViewById(R.id.et_email_reset)).getText().toString();

        if(email.length() > 0){
            final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
            loaderLayout.setVisibility(View.VISIBLE);

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loaderLayout.setVisibility(View.GONE);
                                showToast(PasswordResetActivity.this,"이메일을 보냈습니다.");
                                Log.d(TAG, "Email sent.");
                            }
                        }
                    });
        }else {
            showToast(PasswordResetActivity.this,"이메일을 입력해 주세요.");
        }



    }


    private void startWelcomePagerActivity(){
        //Redirect to ViewPager Activity
        Intent intent = new Intent(PasswordResetActivity.this, WelcomeActivity.class);

        startActivity(intent);
        finish();

          //Option
//        //Data 초기화
//        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}