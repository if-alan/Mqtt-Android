package com.nurif.skripsi.lita;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.rv_content);

        setToolbar();

        setListView();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_assignment_white_24dp));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setListView() {
        ArrayList<String> power = new ArrayList<>();
        power.add("Lampu Taman");
        power.add("Ruang Keluarga");
        power.add("Kamar");
        power.add("Kebun Belakang");

        ReportAdapter powerAdapter = new ReportAdapter(power);
        recyclerView.setAdapter(powerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
