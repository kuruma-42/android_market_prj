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
    Button btn_buy; //버튼 뷰 변수를 선언
    EditText et_name; //에딧텍스트 뷰 변수를 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_title = (TextView)findViewById(R.id.tv_title); // View connect by using id
        btn_buy = findViewById(R.id.btn_buy); // Button connect by using id
        et_name = findViewById(R.id.et_name);

        tv_title.setOnClickListener(new View.OnClickListener() { // 해당 뷰를 클릭할 때 변화
           @Override
            public void onClick(View v) {
                tv_title.setText("쿠루마켓");

            }
        });

        btn_buy.setOnClickListener(new View.OnClickListener() { //해당 뷰를 클릭 할 경우
            @Override
            public void onClick(View v) {
                //앞에는 나 자신, 이동할 곳은 class로 써준다.
                Intent intent = new Intent(MainActivity.this , HistoryActivity.class);
                startActivity(intent);

                //Show Toast Message
                Toast.makeText(MainActivity.this, et_name.getText().toString() + "가 구매되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });






    }

}