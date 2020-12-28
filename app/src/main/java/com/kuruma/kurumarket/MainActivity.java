package com.kuruma.kurumarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView tv_title; //텍스트 뷰 변수를 선언
    Button btn_redirectlogin;
    Button btn_redirectsignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_title = (TextView)findViewById(R.id.tv_title); // View connect by using id
        btn_redirectlogin = findViewById(R.id.btn_redirectlogin); // Button connect by using id
        btn_redirectsignup = findViewById(R.id.btn_redirectsignup);

        tv_title.setOnClickListener(new View.OnClickListener() { // 해당 뷰를 클릭할 때 변화
           @Override
            public void onClick(View v) {
                tv_title.setText("쿠루마켓");
            }
        });

        btn_redirectlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myStartActivity(LoginActivity.class);
            }
        });

        btn_redirectsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myStartActivity(SignupActivity.class);
            }
        });


    }
    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



}