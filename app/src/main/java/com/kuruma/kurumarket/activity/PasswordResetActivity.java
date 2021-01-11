package com.kuruma.kurumarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kuruma.kurumarket.R;

public class PasswordResetActivity extends BasicActivity {

    private Button btn_send_email;

    private static final String TAG = "SignupActivity";
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

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

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startToast("이메일을 보냈습니다.");
                                Log.d(TAG, "Email sent.");
                            }
                        }
                    });
        }else {
            startToast("이메일을 입력해 주세요");
        }



    }

    private void startToast(String msg){ Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}

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