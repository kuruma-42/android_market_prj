package com.kuruma.kurumarket;

import android.app.Dialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragSales extends Fragment {

    private  View view;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<SalesItem> arrayList;
    private RecyclerView.LayoutManager layoutManager;
    private Button btn_add; //아이템 추가 버튼
    private DatabaseReference databaseReference; //파이어베이스 데이터베이스

    public static FragSales newInstance(){
        FragSales fragSales = new FragSales();
        return fragSales;
    }

    //Activity의 자식 개념
    @Nullable
    @Override
    //fagement시작시 수행하는 곳
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       //Activity 안에 fragment를 inflater는 붙이는 개념
        view = inflater.inflate(R.layout.frag_sales, container, false);

        btn_add = view.findViewById(R.id.btn_add);
        recyclerView = view.findViewById(R.id.rv_sales); //id 연동
        recyclerView.setHasFixedSize(true);//리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager(getContext());

        databaseReference = FirebaseDatabase.getInstance().getReference("SalesItem"); // 파이어베이스 데이터베이스 연동



        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // 판매목록을 담아주려는 빈 배열 리스트 생성

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //기존 배열들이 남아있지 않게 초기화
                arrayList.clear();

                //파이어베이스의 db데이터를 가지고 오는 곳
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){ // 반복문을 통해 데이터 List 추출해 냄.
                    SalesItem salesItem = snapshot.getValue(SalesItem.class); //만들어뒀던 SalesItem 모델객체에 데이터를 input
                    arrayList.add(salesItem);
                }

                adapter = new SalesAdapter(arrayList, getContext()); //어댑터를 생성하면서 리스트를 어댑터에 넘긴다.
                recyclerView.setAdapter(adapter); //리사이클러뷰에 커스텀 어댑터를 연결(장착)

                adapter.notifyDataSetChanged(); //리스트뷰 새로고침 해주기
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

//        SalesItem salesItem = new SalesItem(); //판매목록 데이터 클래스
//        salesItem.setTv_product("게임기");
//        salesItem.setTv_price("50000원");
//        salesItem.setTv_date("2020-03-04");
//        arrayList.add(salesItem); // 배열리스트에 추가.
//
//        SalesItem salesItem2 = new SalesItem(); //판매목록 데이터 클래스
//        salesItem2.setTv_product("마우스");
//        salesItem2.setTv_price("10000원");
//        salesItem2.setTv_date("2020-03-04");
//        arrayList.add(salesItem2); // 배열리스트에 추가.
//
//        SalesItem salesItem3 = new SalesItem(); //판매목록 데이터 클래스
//        salesItem3.setTv_product("키보드");
//        salesItem3.setTv_price("30000원");
//        salesItem3.setTv_date("2020-03-04");
//        arrayList.add(salesItem3); // 배열리스트에 추가.




        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Dialog dialogadd = new Dialog(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert);
                dialogadd.setContentView(R.layout.dialog_add); //xml 레이아웃 연결

                final EditText et_product =  dialogadd.findViewById(R.id.et_product); //상품명
                final EditText et_price =  dialogadd.findViewById(R.id.et_price); // 가격
                final EditText et_date =  dialogadd.findViewById(R.id.et_date); // 날짜
                final EditText et_pass =  dialogadd.findViewById(R.id.et_pass); // 비밀번호


                Button btn_add = dialogadd.findViewById(R.id.btn_add);
                btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (et_product.getText().length() == 0 || et_price.getText().length() == 0 || et_date.getText().length() == 0 || et_pass.getText().length() == 0){
                            Toast.makeText(getContext(), "비어있는 입력필드가 존재합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SalesItem item = new SalesItem();
                        item.setTv_product(et_product.getText().toString());
                        item.setTv_price(et_price.getText().toString());
                        item.setTv_date(et_date.getText().toString());
                        item.setTv_password(et_pass.getText().toString());

                        databaseReference.push().setValue(item);


                        arrayList.add(0,item);
                        adapter.notifyItemInserted(0);

                        dialogadd.dismiss();
                    }
                });

                dialogadd.show();
            }
        });

        return view;
    }
}
