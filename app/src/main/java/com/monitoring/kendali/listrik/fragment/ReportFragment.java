package com.monitoring.kendali.listrik.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.monitoring.kendali.listrik.MainActivity;
import com.monitoring.kendali.listrik.R;
import com.monitoring.kendali.listrik.adapter.ReportAdapter;
import com.monitoring.kendali.listrik.data.Report;
import com.monitoring.kendali.listrik.mqtt.PahoMqttClient;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportFragment extends Fragment {

    private TextView tvTotalPrice;
    private TextView tvTotalEnergy;

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private MqttAndroidClient client;
    private PahoMqttClient pahoMqttClient;

    private ProgressDialog loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        setHasOptionsMenu(true);

        tvTotalEnergy = view.findViewById(R.id.tv_energyTotal);
        tvTotalPrice = view.findViewById(R.id.tv_priceTotal);
        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.rv_content);

        setMqttClient();

        return view;
    }

    private void setMqttClient() {
        client = ((MainActivity) getActivity()).getClient();
        pahoMqttClient = ((MainActivity) getActivity()).getPahoMqttClient();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setToolbar();

        mqttCallback();

        setLoadingProgress();

        setConnect();
    }

    private void setToolbar() {
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_assignment_white_24dp));

        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        toolbar.setTitle("Lampu Taman");
    }

    private void setListView(List<Report> reports) {
        ReportAdapter powerAdapter = new ReportAdapter(reports);
        recyclerView.setAdapter(powerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    protected void mqttCallback() {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                setConnectonFailed();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                try {
                    JSONObject obj = new JSONObject(message.toString());
                    ArrayList<Report> reports = new ArrayList<>();


                    JSONArray data = obj.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        Report report = new Report();
                        JSONObject item = data.getJSONObject(i);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date d = sdf.parse(item.getString("time"));
                        sdf.applyPattern("dd MMMM yyyy");

                        report.setDate(sdf.format(d));
                        report.setEnergy(Integer.parseInt(item.getString("watt_h")));

                        reports.add(report);
                    }

                    setList(reports);
                    if (loading.isShowing()) loading.dismiss();
                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + topic + "\"");
                    setConnectonFailed();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    private void setConnectonFailed() {
        if (loading.isShowing()) loading.dismiss();
        getFragmentManager().popBackStack();

        Toast.makeText(getActivity(), "Gagal mendapatkan data perangkat", Toast.LENGTH_SHORT).show();
    }

    private void setList(List<Report> reports) {
        int totalEnergy = 0;

        Report firstDateReport = null;
        List<Report> newReports = new ArrayList<>();

        for (int i = 0; i < reports.size(); i++) {
            if (firstDateReport == null) {
                firstDateReport = new Report();
                firstDateReport = reports.get(i);
            } else if (!reports.get(i).getDate().equals(reports.get(i - 1).getDate())) {
                Report newReport = new Report();

                int totalEnergyDay = reports.get(i - 1).getEnergy() - firstDateReport.getEnergy();

                if (totalEnergyDay != 0) {
                    newReport.setDate(firstDateReport.getDate());
                    newReport.setEnergy(totalEnergyDay);

                    newReports.add(newReport);

                    totalEnergy += totalEnergyDay;
                } else {
                    newReports.add(firstDateReport);

                    totalEnergy += totalEnergyDay;
                }

                firstDateReport = null;
            }
        }

        setListView(newReports);

        setTotal(totalEnergy);
    }

    private void setTotal(int totalEnergy) {
        double energyKwh = totalEnergy / 1000.0;

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        tvTotalEnergy.setText(getString(R.string.energy_total, energyKwh + ""));
        tvTotalPrice.setText(formatRupiah.format(energyKwh * 1467));
    }

    private void setLoadingProgress() {
        loading = ProgressDialog.show(getActivity(), "",
                "Mengambil data. Mohon tunggu...", true);

        loading.setCancelable(true);
        loading.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                loading.dismiss();
                getFragmentManager().popBackStack();
            }
        });
    }

    private void setConnect() {
        try {
            loading.show();
            pahoMqttClient.publishMessage(client, "{\"data\":53,\"time1\":\"2019-08-08\",\"time2\":\"2019-08-31\"}", 0, "pejaten/request");
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String topic = "pejaten/respon";
        if (!topic.isEmpty()) {
            try {
                pahoMqttClient.subscribe(client, topic, 1);

            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
