package com.nurif.skripsi.lita.fragment;

import android.app.ProgressDialog;
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

import com.nurif.skripsi.lita.MainActivity;
import com.nurif.skripsi.lita.R;
import com.nurif.skripsi.lita.adapter.ReportAdapter;
import com.nurif.skripsi.lita.mqtt.PahoMqttClient;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ReportFragment extends Fragment {
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

        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.rv_content);

        setMqttClient();

        return view;
    }

    private void setMqttClient(){
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
    }

    private void setListView() {
        ArrayList<String> power = new ArrayList<>();
        power.add("Lampu Taman");
        power.add("Ruang Keluarga");
        power.add("Kamar");
        power.add("Kebun Belakang");

        ReportAdapter powerAdapter = new ReportAdapter(power);
        recyclerView.setAdapter(powerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    protected void mqttCallback() {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                //msg("Connection lost...");
                Log.d("bismillah", "Alhamdulillah");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                try {
                    JSONObject obj = new JSONObject(message.toString());

                    setListView();
                    if (loading.isShowing()) loading.dismiss();
                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + topic + "\"");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    private void setLoadingProgress() {
        loading = ProgressDialog.show(getActivity(), "",
                "Mengambil data. Mohon tunggu...", true);
        loading.setCancelable(false);
    }

    private void setConnect() {
        try {
            loading.show();
            pahoMqttClient.publishMessage(client, "{\"data\":53,\"time1\":\"2019-08-08\",\"time2\":\"2019-08-09\"}", 0, "pejaten/request");
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
