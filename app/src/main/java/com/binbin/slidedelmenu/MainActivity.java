package com.binbin.slidedelmenu;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SlideMenuListView lv;
    private List<String> str=new ArrayList<>();
    private MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv= (SlideMenuListView) findViewById(R.id.lv);
        for(int i=0;i<20;i++){
            str.add(i+"个");
        }
        adapter=new MyAdapter();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,str.get(position)+"hhhhhhonItemClickhhhhhhh",Toast.LENGTH_SHORT).show();
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,str.get(position)+"AAAAAAAAAAsetOnItemLongClickListenerAAAAAAAAAAAAA",Toast.LENGTH_SHORT).show();
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
                convertView=View.inflate(MainActivity.this,R.layout.item, null);
                holder.item=(MenuItem) convertView.findViewById(R.id.item);
                holder.content=View.inflate(MainActivity.this,R.layout.item_content, null);
                holder.menu=View.inflate(MainActivity.this,R.layout.menu, null);
                holder.item.setItemAndMenu(holder.content,holder.menu);
                convertView.setTag(holder);
//                Log.e("tianbin","======getView111111111111=========");
            }else {
                holder = (HolderView) convertView.getTag();
//                Log.e("tianbin","======getView22222222222222=========");
            }

            final int pos=position;
            final MenuItem mi=holder.item;
            mi.setOnClickListener(new MenuItem.OnClickListener() {
                @Override
                public void onClick(View view,int type) {
                    if(type==0){
                        Toast.makeText(MainActivity.this,str.get(pos)+"@@@Click",Toast.LENGTH_SHORT).show();
                    }else if(type==1){
                        switch (view.getId()){
                            case R.id.menu_delete:
                                mi.delItem();
                                str.remove(pos);
//                                notifyDataSetChanged();
                                break;
                            case R.id.menu_hello:
                                Toast.makeText(MainActivity.this, "hello"+pos, Toast.LENGTH_SHORT).show();
                                if(pos==0){
                                    startActivity(new Intent(MainActivity.this,SecondActivity.class));
                                }
                                break;
                            case R.id.bt:
                                Toast.makeText(MainActivity.this, "button=========="+pos, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }

                @Override
                public void onLongClick(View view) {
                    Toast.makeText(MainActivity.this,str.get(pos)+"###LongClick",Toast.LENGTH_SHORT).show();
                }
            });
            ((TextView)holder.content.findViewById(R.id.tv_top)).setText(str.get(position));
            return convertView;
        }

    }
    static class HolderView {
        public MenuItem item;
        public View content;
        public View menu;
    }

}
