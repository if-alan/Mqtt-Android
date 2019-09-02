package com.monitoring.kendali.listrik.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monitoring.kendali.listrik.R;
import com.monitoring.kendali.listrik.data.Report;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {
    private List<Report> mDataset;

    public ReportAdapter(List<Report> myDataset) {
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
        private TextView tvDate;
        private TextView tvEnergy;
        private TextView tvPrice;

        public MyViewHolder(View view) {
            super(view);

            tvDate = view.findViewById(R.id.tv_date);
            tvEnergy = view.findViewById(R.id.tv_energy);
            tvPrice = view.findViewById(R.id.tv_price);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Report report = mDataset.get(position);

        holder.tvDate.setText(report.getDate());
        holder.tvEnergy.setText(report.getEnergy() + " Wh");
        holder.tvPrice.setText(report.getPrice() + " V");

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}