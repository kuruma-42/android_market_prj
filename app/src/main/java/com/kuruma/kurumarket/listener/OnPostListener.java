package com.kuruma.kurumarket.listener;

import com.kuruma.kurumarket.PostInfo;

public interface OnPostListener {
    void onDelete(PostInfo postInfo);
    void onModify();
}
