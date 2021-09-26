package com.monitoring.kendali.listrik.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.monitoring.kendali.listrik.MainActivity;
import com.monitoring.kendali.listrik.R;
import com.monitoring.kendali.listrik.mqtt.PahoMqttClient;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Random;

public class HomeFragment extends Fragment {
    Button btnConnect;

    private ProgressDialog loading;
//    final MediaPlayer mp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnConnect = view.findViewById(R.id.btn_connect);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        mp = MediaPlayer.create(this, R.raw.soho);
//        mp.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSubscribe();
            }
        });
    }

    public void setSubscribe() {
//        setLoadingProgress();
        setConnect();
    }

    private void setLoadingProgress() {
        loading = ProgressDialog.show(getActivity(), "",
                "Menghubungkan...", true);
        loading.setCancelable(false);
    }

    public void setConnect() {
        final PahoMqttClient pahoMqttClient = new PahoMqttClient();

        String urlBroker = "tcp://broker.mqtt-dashboard.com:1883";

        Random r = new Random();
        int i1 = r.nextInt(5000 - 1) + 1;
        String clientid = "mqtt" + i1;

        final MqttAndroidClient client = new MqttAndroidClient(getActivity(), urlBroker, clientid);

        try {
            MqttConnectOptions myMqttcnxoptions = getMqttConnectionOption();

            IMqttToken token = client.connect(myMqttcnxoptions);

            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    client.setBufferOpts(getDisconnectedBufferOptions());

                    ((MainActivity) getActivity()).setClient(client);
                    ((MainActivity) getActivity()).setPahoMqttClient(pahoMqttClient);

                    ((MainActivity) getActivity()).setContent(new ListEnergyFragment());

                    loading.dismiss();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    loading.dismiss();

                    Toast.makeText(getActivity(), "Gagal mendapatkan data perangkat", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private MqttConnectOptions getMqttConnectionOption() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(true);

        return mqttConnectOptions;
    }

    private DisconnectedBufferOptions getDisconnectedBufferOptions() {
        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(100);
        disconnectedBufferOptions.setPersistBuffer(false);
        disconnectedBufferOptions.setDeleteOldestMessages(false);

        return disconnectedBufferOptions;
    }

    @Override
    public void onStop() {
        super.onStop();
//        mp.release();
    }
}
