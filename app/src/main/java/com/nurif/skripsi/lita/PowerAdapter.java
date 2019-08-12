package com.nurif.skripsi.lita;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PowerAdapter extends RecyclerView.Adapter<PowerAdapter.MyViewHolder> {
    private ArrayList<String> mDataset;

    public PowerAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public PowerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_power, parent, false);

        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvTitle;

        public MyViewHolder(View view) {
            super(view);
            this.tvTitle = view.findViewById(R.id.tv_title);
        }

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvTitle.setText(mDataset.get(position));

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}