package com.binbin.slidedelmenu.item2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.binbin.slidedelmenu.R;

import java.util.ArrayList;
import java.util.List;

public class FourthActivity extends AppCompatActivity {

    private ListView lv;
    private List<String> str=new ArrayList<>();
    private MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        lv= (ListView) findViewById(R.id.lv);
        for(int i=0;i<20;i++){
            str.add(i+"个");
        }
        adapter=new MyAdapter();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(FourthActivity.this,str.get(position)+"onItemClick",Toast.LENGTH_SHORT).show();
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(FourthActivity.this,str.get(position)+"setOnItemLongClickListener",Toast.LENGTH_SHORT).show();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            HolderView holder = null;
            if (convertView == null) {
                holder = new HolderView();
                convertView=View.inflate(FourthActivity.this,R.layout.item4, null);
                holder.tv=(TextView) convertView.findViewById(R.id.tv);
                holder.tvDel=(TextView) convertView.findViewById(R.id.tv_del);
                holder.tvHello=(TextView) convertView.findViewById(R.id.tv_hello);
                holder.bt= (Button) convertView.findViewById(R.id.bt);
                convertView.setTag(holder);
//                Log.e("tianbin","======getView111111111111=========");
            }else {
                holder = (HolderView) convertView.getTag();
//                Log.e("tianbin","======getView22222222222222=========");
            }

            final MenuItem mi= (MenuItem) convertView;

            holder.tv.setText(str.get(position));
            holder.bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FourthActivity.this,"bt========onClick",Toast.LENGTH_SHORT).show();
                }
            });
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FourthActivity.this,"tv========onClick",Toast.LENGTH_SHORT).show();
                }
            });
            holder.tvHello.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FourthActivity.this,"hello=====onClick",Toast.LENGTH_SHORT).show();
                }
            });
            holder.tvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FourthActivity.this,"del========onClick",Toast.LENGTH_SHORT).show();
                    mi.quickClose();
                    str.remove(position);
                    notifyDataSetChanged();
                }
            });
            holder.tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(FourthActivity.this,"tv==========onLongClick",Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            holder.tvHello.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(FourthActivity.this,"hello============onLongClick",Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            return convertView;
        }

    }
    static class HolderView {
        public TextView tv;
        public TextView tvDel;
        public TextView tvHello;
        public Button bt;
    }

}
