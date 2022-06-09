package com.example.bookkeeping.listeners;

import com.example.bookkeeping.adapters.MyRecyclerViewAdapter;

public interface MyUniversalListener {
    //RecyclerView 的item长按回调
    void MyOnLongClickItemListener(
            MyRecyclerViewAdapter.MyRecyclerViewHolder myRecyclerViewHolder,
            int position
    );
}
