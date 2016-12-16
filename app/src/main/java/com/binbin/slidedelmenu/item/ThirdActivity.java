package com.binbin.slidedelmenu.item;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.binbin.slidedelmenu.R;
import com.binbin.slidedelmenu.itemandlistview.MenuItem;
import com.binbin.slidedelmenu.itemandlistview.SlideMenuListView;
import com.binbin.slidedelmenu.singlelistview.SecondActivity;

import java.util.ArrayList;
import java.util.List;

public class ThirdActivity extends AppCompatActivity {

    private ListView lv;
    private List<String> str=new ArrayList<>();
    private MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        lv= (ListView) findViewById(R.id.lv);
        for(int i=0;i<20;i++){
            str.add(i+"个");
        }
        adapter=new MyAdapter();
        lv.setAdapter(adapter);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(ThirdActivity.this,str.get(position)+"hhhhhhonItemClickhhhhhhh",Toast.LENGTH_SHORT).show();
//            }
//        });
//        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(ThirdActivity.this,str.get(position)+"AAAAAAAAAAsetOnItemLongClickListenerAAAAAAAAAAAAA",Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return str.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return str.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            HolderView holder = null;
            if (convertView == null) {
                holder = new HolderView();
                convertView=View.inflate(ThirdActivity.this,R.layout.item3, null);
                holder.tv=(TextView) convertView.findViewById(R.id.tv);
                holder.tvDel=(TextView) convertView.findViewById(R.id.tv_del);
                holder.tvHello=(TextView) convertView.findViewById(R.id.tv_hello);
                convertView.setTag(holder);
//                Log.e("tianbin","======getView111111111111=========");
            }else {
                holder = (HolderView) convertView.getTag();
//                Log.e("tianbin","======getView22222222222222=========");
            }

            holder.tv.setText(str.get(position));
            return convertView;
        }

    }
    static class HolderView {
        public TextView tv;
        public TextView tvDel;
        public TextView tvHello;
    }

}
