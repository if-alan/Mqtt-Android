package com.monitoring.kendali.listrik.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        private TextView tvDate;
        private TextView tvVoltage;
        private TextView tvCurrent;
        private TextView tvPower;
        private TextView tvEnergy;

        public MyViewHolder(View view) {
            super(view);

            tvDate = view.findViewById(R.id.tv_date);
            tvEnergy = view.findViewById(R.id.tv_energy);
            tvVoltage = view.findViewById(R.id.tv_voltage);
            tvCurrent = view.findViewById(R.id.tv_current);
            tvPower = view.findViewById(R.id.tv_power);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Report report = mDataset.get(position);

        holder.tvDate.setText(report.getTime());
        holder.tvEnergy.setText(report.getEnergy() + " Wh");
        holder.tvVoltage.setText(report.getVoltage() + " V");
        holder.tvCurrent.setText(report.getCurrent() + " A");
        holder.tvPower.setText(report.getPower() + " W");

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}