package com.nurif.skripsi.lita;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class PowerAdapter extends RecyclerView.Adapter<PowerAdapter.MyViewHolder> {
    private ArrayList<String> mDataset;
    private static onItemSelected itemSelected;

    public PowerAdapter(ArrayList<String> myDataset, onItemSelected onItemSelected) {
        mDataset = myDataset;
        itemSelected = onItemSelected;
    }

    @Override
    public PowerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_power, parent, false);

        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View view;
        public Button btnViewReport;
        public TextView tvTitle;

        public MyViewHolder(View view) {
            super(view);
            this.btnViewReport = view.findViewById(R.id.btn_viewReport);
            this.tvTitle = view.findViewById(R.id.tv_title);

            btnViewReport.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == btnViewReport){
                itemSelected.viewReport();
            }else{
                itemSelected.setEnergy(getAdapterPosition());
            }
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

    public interface onItemSelected{
        void setEnergy(int position);

        void viewReport();
    }
}