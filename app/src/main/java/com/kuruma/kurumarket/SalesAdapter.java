package com.kuruma.kurumarket;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

//recyclerview DataModel을 연동하는 과정
public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.CustomViewHolder> {

    private ArrayList<SalesItem> arrayList; //판매목록 리스트를 담고있음
    private Context context;
    private DatabaseReference databaseReference; //파이어베이스 데이터베이스

    public SalesAdapter(ArrayList<SalesItem> arraylist, Context context) { //생성자를 구성
        //Grap Set Data
        this.arrayList = arraylist;
        this.context = context;
    }

    @NonNull
    @Override
    public SalesAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sales_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        databaseReference = FirebaseDatabase.getInstance().getReference("SalesItem"); // 파이어베이스 데이터베이스 연동


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SalesAdapter.CustomViewHolder holder, int position) {
        holder.iv_profile.setImageResource(R.drawable.cart);
        //pull Data from arraylist
        holder.tv_product.setText(arrayList.get(position).getTv_product());
        holder.tv_price.setText(arrayList.get(position).getTv_price());
        holder.tv_date.setText(arrayList.get(position).getTv_date());
    }

    @Override
    public int getItemCount() { //list 아이템의 전체 갯수
        return arrayList.size();
    }

    //view를 쥐고있음 salesitem 만든 것
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_profile;
        TextView tv_product;
        TextView tv_price;
        TextView tv_date;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.iv_profile = itemView.findViewById(R.id.iv_pofile);
            this.tv_product = itemView.findViewById(R.id.tv_product);
            this.tv_price = itemView.findViewById(R.id.tv_price);
            this.tv_date = itemView.findViewById(R.id.tv_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int curPos = getAdapterPosition(); // 현재 클릭한 아이템의 포지션(위치 .. 0 부터 시작), 리사이클러뷰에 연동된 리스트의 포지션을 가지고 온다.

                    final Dialog dialogconfirm = new Dialog(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
                    dialogconfirm.setContentView(R.layout.dialog_confirm_password);

                    EditText et_pass = dialogconfirm.findViewById(R.id.et_pass); //게시글의 비밀번호를 확인 받을 입력필드
                    Button btn_confirm = dialogconfirm.findViewById(R.id.btn_confirm);

                    btn_confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (et_pass.getText().length() == 0) {
                                Toast.makeText(context, "비어있는 입력필드가 존재합니다.", Toast.LENGTH_SHORT).show();
                                return; //if문 벗어나 on click 수행 취소.
                            }

                            // 게시글 비밀번호  입력필드에 입력한 값이랑 내가 클릭한 게시글의 비밀번호가 일치한다면.
                            if (et_pass.getText().toString().equals(arrayList.get(curPos).getTv_password())) {


                                final Dialog dialogmod = new Dialog(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
                                dialogmod.setContentView(R.layout.dialog_modify); //xml 레이아웃 연결

                                final EditText et_product = dialogmod.findViewById(R.id.et_product); //상품명
                                final EditText et_price = dialogmod.findViewById(R.id.et_price); // 가격
                                final EditText et_date = dialogmod.findViewById(R.id.et_date); // 날짜

                                Button btn_modify = dialogmod.findViewById(R.id.btn_modify); // 수정완료 버튼

                                btn_modify.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (et_product.getText().length() == 0 || et_price.getText().length() == 0 || et_date.getText().length() == 0) {
                                            Toast.makeText(context, "비어있는 입력필드가 존재합니다.", Toast.LENGTH_SHORT).show();
                                            return; //if문 벗어나 on click 수행 취소.
                                        }

                                        final SalesItem item = new SalesItem();
                                        item.setTv_product(et_product.getText().toString());
                                        item.setTv_price(et_price.getText().toString());
                                        item.setTv_date(et_date.getText().toString());
                                        item.setTv_password(arrayList.get(curPos).getTv_password());



                                        databaseReference.orderByChild("tv_product").equalTo(arrayList.get(curPos).getTv_product()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                //파이어베이스의 db데이터를 가지고 오는 곳
                                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){ // 반복문을 통해 데이터 List 추출해 냄.
                                                    databaseReference.child(snapshot.getKey()).setValue(item);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });



                                        //curPos에 포지션 값이 담겨 있고, 그 위치에 아이템을 셋팅해 줌
                                        arrayList.set(curPos, item); //리스트에 있는 데이터를 수정
                                        notifyItemChanged(curPos); //수정 완료 후 새로고침

                                        dialogmod.dismiss(); // 다이얼로그 닫기

                                    }
                                });

                                Button btn_delete = dialogmod.findViewById(R.id.btn_delete); //삭제 버튼
                                btn_delete.setOnClickListener(new View.OnClickListener() {



                                    @Override
                                    public void onClick(View v) {

                                        databaseReference.orderByChild("tv_product").equalTo(arrayList.get(curPos).getTv_product()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                //파이어베이스의 db데이터를 가지고 오는 곳
                                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){ // 반복문을 통해 데이터 List 추출해 냄.
                                                    databaseReference.child(snapshot.getKey()).setValue(null);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        arrayList.remove(curPos); // position 받고 삭제
                                        notifyItemRemoved(curPos);
                                        notifyItemRangeChanged(curPos, arrayList.size());

                                        dialogmod.dismiss();
                                    }
                                });


                                dialogmod.show();
                            } else{
                                Toast.makeText(context, "비밀번호가 틀렸습니다. ", Toast.LENGTH_SHORT).show();
                            }
                            dialogconfirm.dismiss(); // 비밀번호 틀리면 다이올로그 종료
                        }
                    });

                    dialogconfirm.show(); //다이얼로그 활성


                }
            });
        }
    }
}
