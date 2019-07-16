package com.nurif.skripsi.lita.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.io.UnsupportedEncodingException;

public class EnergyFragment extends Fragment {
    public static final String TAG = EnergyFragment.class.getClass().getSimpleName();

    private MqttAndroidClient client;
    private PahoMqttClient pahoMqttClient;

    Toolbar toolbar;
    TextView tvVolt;
    TextView tvCurrent;
    TextView tvPower;

    ImageView ivPower;

    Boolean status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_energy, container, false);

        setHasOptionsMenu(true);

        toolbar = view.findViewById(R.id.toolbar);
        tvVolt = (TextView) view.findViewById(R.id.tv_volt);
        tvCurrent = (TextView) view.findViewById(R.id.tv_current);
        tvPower = (TextView) view.findViewById(R.id.tv_power);
        ivPower = (ImageView) view.findViewById(R.id.iv_power);

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

        ivPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    pahoMqttClient.publishMessage(client, status ? "0" : "1", status ? 0 : 1, "pejaten/home/lamp1");
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
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
                    JSONObject obj = new JSONObject(message.toString());

                    String voltage = obj.getString("voltage");
                    String t_current = obj.getString("current");
                    String watt = obj.getString("power");
                    int feedback = obj.getInt("feedback");

                    tvVolt.setText(getString(R.string.voltage, voltage));
                    tvCurrent.setText(getString(R.string.current, t_current));
                    tvPower.setText(getString(R.string.power, watt));

                    if(feedback == 0){
                        status = false;
                        ivPower.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.black));
                    }else{
                        status = true;
                        ivPower.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.holo_green_light));
                    }

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
        String topic = "pejaten/kwhmeter1";
        if (!topic.isEmpty()) {
            try {
                pahoMqttClient.subscribe(client, topic, 1);

            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
