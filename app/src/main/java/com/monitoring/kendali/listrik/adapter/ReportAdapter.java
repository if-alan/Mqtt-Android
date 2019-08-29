package com.monitoring.kendali.listrik.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monitoring.kendali.listrik.R;
import com.monitoring.kendali.listrik.data.Report;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {
    private ArrayList<Report> mDataset;

    public ReportAdapter(ArrayList<Report> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ReportAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);

        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public MyViewHolder(View view) {
            super(view);
        }

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}