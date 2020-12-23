package com.kuruma.kurumarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    private ArrayList<MemberInfo> arrayList;
    private DatabaseReference databaseReference; //파이어베이스 데이터베이스
    EditText et_email;
    EditText et_loginpassword;
    EditText et_loginpasswordcheck;
    Button btn_signup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_email = (EditText)findViewById(R.id.et_email);
        et_loginpassword = (EditText)findViewById(R.id.et_loginpassword);
        et_loginpasswordcheck = (EditText)findViewById(R.id.et_loginpasswordcheck);
        btn_signup = (Button)findViewById(R.id.btn_signup);
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

                //Duplicate Check at DataBase



                //Set Sources
                final MemberInfo memberInfo = new MemberInfo();
                memberInfo.setTv_email(et_email.getText().toString());
                memberInfo.setTv_loginpassword(et_loginpassword.getText().toString());

                //Push Data to DataBase
                databaseReference.push().setValue(memberInfo);

                //redirect
                Intent intent = new Intent(SignupActivity.this, ViewPagerActivity.class);
                startActivity(intent);
                finish(); // 현재 엑티비티를 파괴시킴.
            }
        });

    }



}