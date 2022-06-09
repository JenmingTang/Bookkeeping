package com.example.bookkeeping.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookkeeping.R;
import com.example.bookkeeping.entities.GridViewItem;

import java.util.List;

public class MyGridViewAdapter extends BaseAdapter {
    List<GridViewItem> gridViewItems;
    Context context;

    public MyGridViewAdapter(List<GridViewItem> gridViewItems, Context context) {
        this.gridViewItems = gridViewItems;
        this.context = context;
    }

    @Override
    public int getCount() {
        return gridViewItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyGridViewHolder mHolder;
        if (convertView == null) {
            mHolder = new MyGridViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_view_item, null);
            mHolder.img = convertView.findViewById(R.id.gvi_img);
            mHolder.text = convertView.findViewById(R.id.gvi_tv);
            convertView.setTag(mHolder);
        } else
            mHolder = (MyGridViewHolder) convertView.getTag();

        mHolder.img.setImageResource(gridViewItems.get(position).getImg());
        mHolder.text.setText(gridViewItems.get(position).getText());

        return convertView;
    }

    class MyGridViewHolder {
        private ImageView img;
        private TextView text;
    }
}
