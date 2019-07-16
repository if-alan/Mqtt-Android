package com.nurif.skripsi.lita.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nurif.skripsi.lita.MainActivity;
import com.nurif.skripsi.lita.R;
import com.nurif.skripsi.lita.mqtt.PahoMqttClient;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class EnergyFragment extends Fragment {
    public static final String TAG = EnergyFragment.class.getClass().getSimpleName();

    private MqttAndroidClient client;
    private PahoMqttClient pahoMqttClient;

    Toolbar toolbar;
    TextView tvVolt;
    TextView tvCurrent;
    TextView tvPower;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_energy, container, false);

        setHasOptionsMenu(true);

        toolbar = view.findViewById(R.id.toolbar);
        tvVolt = (TextView) view.findViewById(R.id.tv_volt);
        tvCurrent = (TextView) view.findViewById(R.id.tv_current);
        tvPower = (TextView) view.findViewById(R.id.tv_power);

        client = ((MainActivity) getActivity()).getClient();
        pahoMqttClient = ((MainActivity) getActivity()).getPahoMqttClient();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setToolbar();

        mqttCallback();

        setConnect();
    }

    private void setToolbar() {
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);

        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
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
                    JSONObject obj = new JSONObject(topic.toString());

                    String voltage = obj.getString("voltage");
                    String t_current = obj.getString("t_current");
                    String watt = obj.getString("watt");
                    String t_kwh = obj.getString("t_kwh");
                    String tagihan = obj.getString("tagihan");
                    String people = obj.getString("people");

                    tvVolt.setText(getString(R.string.voltage, voltage));
                    tvCurrent.setText(getString(R.string.current, t_current));
                    tvPower.setText(getString(R.string.power, watt));

                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + topic + "\"");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    private void setConnect() {
        String topic = "pejaten/kwhmeter";
        if (!topic.isEmpty()) {
            try {
                pahoMqttClient.subscribe(client, topic, 1);

            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
