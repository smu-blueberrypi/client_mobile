package com.example.user.mainapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<MyList> myList = new ArrayList<MyList>();
    public ListViewAdapter() {

    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Object getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_listview, parent, false);
        }

        TextView sosPercent = (TextView)convertView.findViewById(R.id.sosPercent);
        TextView lat = (TextView)convertView.findViewById(R.id.lat);
        TextView lng = (TextView)convertView.findViewById(R.id.lng);
        TextView height = (TextView)convertView.findViewById(R.id.height);
        TextView time = (TextView)convertView.findViewById(R.id.time);

        MyList listItem = myList.get(position);
        sosPercent.setText(listItem.getSosPercent());
        lat.setText(listItem.getLat());
        lng.setText(listItem.getLng());
        height.setText(listItem.getHeight());
        time.setText(listItem.getTime());

        return convertView;
    }

    public void addList(String sosPercent,String lat,String lng, String height, String time ) {
        MyList item = new MyList();

        item.setSosPercent(sosPercent);
        item.setLat(lat);
        item.setLng(lng);
        item.setHeight(height);
        item.setTime(time);

        myList.add(item);

    }
}
