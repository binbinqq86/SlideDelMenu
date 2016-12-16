package com.binbin.slidedelmenu.singlelistview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.binbin.slidedelmenu.R;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private SlideMenuListView2 slv;
    private List<String> str=new ArrayList<>();
    private MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        slv= (SlideMenuListView2) findViewById(R.id.slv);
        for(int i=0;i<20;i++){
            str.add(i+"个个个个个个个个个");
        }
        adapter=new MyAdapter();
        slv.setAdapter(adapter);
        slv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SecondActivity.this,str.get(position)+"hhhhhhonItemClickhhhhhhh",Toast.LENGTH_SHORT).show();
            }
        });
        slv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SecondActivity.this,str.get(position)+"AAAAAAAAAAsetOnItemLongClickListenerAAAAAAAAAAAAA",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
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
                convertView=View.inflate(SecondActivity.this,R.layout.item2, null);
                holder.item=convertView.findViewById(R.id.ll_item);
                holder.menu=convertView.findViewById(R.id.ll_menu);
                convertView.setTag(holder);
//                Log.e("tianbin","======getView111111111111=========");
            }else {
                holder = (HolderView) convertView.getTag();
//                Log.e("tianbin","======getView22222222222222=========");
            }

            final int pos=position;
            return convertView;
        }

    }
    static class HolderView {
        public View item;
        public View menu;
    }
}
