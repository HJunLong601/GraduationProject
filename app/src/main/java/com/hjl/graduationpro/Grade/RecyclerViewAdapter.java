package com.hjl.graduationpro.Grade;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hjl.graduationpro.R;


import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyRecyclerHolder> {

    private List<Long> gradelist;

    public RecyclerViewAdapter(List<Long> gradelist) {
        this.gradelist = gradelist;
    }

    @Override
    public MyRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item,parent,false);
        MyRecyclerHolder holder = new MyRecyclerHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyRecyclerHolder holder, int position) {
        holder.grade.setText(String.valueOf(gradelist.get(position)));
        holder.rank.setText(String.valueOf(position + 1) + "、");
//        Log.i("Adapter",getItemCount()+"");
    }

    @Override
    public int getItemCount() {
        return gradelist.size();
    }

    static class MyRecyclerHolder extends RecyclerView.ViewHolder{
        View gradeView;
        TextView rank;
        TextView grade;

        public MyRecyclerHolder(View itemView) {
            super(itemView);

            rank = itemView.findViewById(R.id.rec_item_rank);
            grade = itemView.findViewById(R.id.rec_item_grade);
            gradeView = itemView;
        }

    }

}
