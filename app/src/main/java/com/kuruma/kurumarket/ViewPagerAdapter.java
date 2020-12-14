package com.kuruma.kurumarket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;



public class ViewPagerAdapter extends FragmentPagerAdapter {



    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) { // 프래그먼트 교체를 보여주는 처리를 구현할 곳
        switch (position) {
            case 0:
                return FragMain.newInstance();//FragMain을 생성
            case 1:
                return FragSales.newInstance();//FragSales를 생성
            default:
                return null;
        }
    }

    @Override
    public int getCount() { // 연결한 프래그먼트의 갯수를 적어줌.
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) { // 뷰페이저의 위치에 대한 이름을 지정
        switch (position) {
            case 0:
                return "메인";
            case 1:
                return "판매목록";
            default:
                return null;
        }
    }
}
