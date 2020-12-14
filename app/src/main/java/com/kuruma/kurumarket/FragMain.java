package com.kuruma.kurumarket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragMain extends Fragment {

    private  View view;

    public static FragMain newInstance(){
        FragMain fragMain = new FragMain();
        return fragMain;
    }

    //Activity의 자식 개념
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       //Activity 안에 fragment를 inflater는 붙이는 개념
        view = inflater.inflate(R.layout.frag_main, container, false);

        return view;
    }
}
