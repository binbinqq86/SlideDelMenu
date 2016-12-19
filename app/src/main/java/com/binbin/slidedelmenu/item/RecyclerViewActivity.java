package com.binbin.slidedelmenu.item;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.binbin.slidedelmenu.DividerItemDecoration;
import com.binbin.slidedelmenu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by -- on 2016/12/19.
 */

public class RecyclerViewActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<String> str = new ArrayList<>();
    private MyAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView = new RecyclerView(this);
        setContentView(mRecyclerView);
        for (int i = 0; i < 20; i++) {
            str.add(i + "个");
        }
        //设置adapter
        adapter=new MyAdapter(str,this);
        mRecyclerView.setAdapter(adapter);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //设置Item增加、移除动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation(),R.drawable.divider2));
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private List<String> datas;
        private Context mContext;
        public MyAdapter(List<String> datas,Context mContext){
            this.datas=datas;
            this.mContext=mContext;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item3, parent,
                    false));
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.tv.setText(datas.get(position));
            holder.bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RecyclerViewActivity.this,"bt========onClick",Toast.LENGTH_SHORT).show();
                }
            });
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RecyclerViewActivity.this,"tv========onClick",Toast.LENGTH_SHORT).show();
                }
            });
            holder.tvHello.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RecyclerViewActivity.this,"hello=====onClick",Toast.LENGTH_SHORT).show();
                }
            });
            holder.tvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RecyclerViewActivity.this,holder.getAdapterPosition()+"del========onClick"+position,Toast.LENGTH_SHORT).show();
//                    ((MenuItem)holder.itemView).quickClose();
                    str.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
//                    notifyDataSetChanged();
                }
            });
            holder.tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(RecyclerViewActivity.this,"tv==========onLongClick",Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            holder.tvHello.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(RecyclerViewActivity.this,"hello============onLongClick",Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv;
            TextView tvHello;
            TextView tvDel;
            Button bt;

            public MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.tv);
                tvDel=(TextView) view.findViewById(R.id.tv_del);
                tvHello=(TextView) view.findViewById(R.id.tv_hello);
                bt= (Button) view.findViewById(R.id.bt);
            }
        }
    }
}
