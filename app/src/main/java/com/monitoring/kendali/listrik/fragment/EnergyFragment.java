package com.monitoring.kendali.listrik.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.monitoring.kendali.listrik.MainActivity;
import com.monitoring.kendali.listrik.R;
import com.monitoring.kendali.listrik.dialog.PriceDialog;
import com.monitoring.kendali.listrik.mqtt.PahoMqttClient;

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

    private ProgressDialog loading;

    Toolbar toolbar;

    LinearLayout btnPower;
    LinearLayout fdCount;

    TextView tvVolt;
    TextView tvCurrent;
    TextView tvPower;
    TextView tvEnergy;
    TextView tvStatus;
    TextView tvTimer;
    EditText etPrice;

    ImageView ivPower;

    Button btnCount;

    Handler handler;

    String power;
    long startTime;
    long millisecondTime, timeBuff, updateTime = 0L;
    int seconds, minutes, hour, milliSeconds;
    Boolean status = false;
    long time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_energy, container, false);
        setHasOptionsMenu(true);

        setContent(view);

        setMqttClient();

        return view;
    }

    private void setContent(View view) {
        toolbar = view.findViewById(R.id.toolbar);

        fdCount = (LinearLayout) view.findViewById(R.id.fd_count);
        btnPower = (LinearLayout) view.findViewById(R.id.btn_power);

        tvVolt = (TextView) view.findViewById(R.id.tv_volt);
        tvCurrent = (TextView) view.findViewById(R.id.tv_current);
        tvPower = (TextView) view.findViewById(R.id.tv_power);
        tvEnergy = (TextView) view.findViewById(R.id.tv_energy);
        tvStatus = (TextView) view.findViewById(R.id.tv_status);
        tvTimer = (TextView) view.findViewById(R.id.tv_timer);
        etPrice = (EditText) view.findViewById(R.id.et_price);

        ivPower = (ImageView) view.findViewById(R.id.iv_power);

        btnCount = (Button) view.findViewById(R.id.btn_count);
    }

    private void setMqttClient() {
        client = ((MainActivity) getActivity()).getClient();
        pahoMqttClient = ((MainActivity) getActivity()).getPahoMqttClient();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler = new Handler();

        setToolbar();

        mqttCallback();

        setConnect();

        setLoadingProgress();

        buttonListener();
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

        toolbar.setTitle("Lampu Taman");
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

                    String voltage = obj.getString("voltage");
                    String t_current = obj.getString("current");
                    power = obj.getString("power");
                    String energy = obj.getString("energy");
                    int feedback = obj.getInt("feedback");

                    tvVolt.setText(getString(R.string.voltage, voltage));
                    tvCurrent.setText(getString(R.string.current, t_current));
                    tvPower.setText(getString(R.string.power, power));
                    tvEnergy.setText(getString(R.string.energy, energy));

                    if (feedback == 0) {
                        if (status) time = System.currentTimeMillis();

                        if (status) {
                            handler.removeCallbacks(runnable);

                            millisecondTime = 0L;
                            startTime = 0L;
                            timeBuff = 0L;
                            updateTime = 0L;
                            seconds = 0;
                            minutes = 0;
                            hour = 0;
                            milliSeconds = 0;

                            tvTimer.setText("00:00:00");
                            status = false;
                        } else {
                            status = false;
                        }

                        ivPower.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.black));

                        tvStatus.setText("OFF");
                        tvStatus.setBackgroundColor(Color.BLACK);

                        fdCount.setVisibility(View.INVISIBLE);
                        btnCount.setEnabled(status);
                    } else {
                        if (!status) time = System.currentTimeMillis();

                        if (!status) {
                            startTime = SystemClock.uptimeMillis();
                            handler.postDelayed(runnable, 0);
                            status = true;
                        } else {
                            status = true;
                        }

                        ivPower.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.holo_green_light));

                        tvStatus.setText("ON");
                        tvStatus.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.holo_green_light));

                        fdCount.setVisibility(View.VISIBLE);
                        btnCount.setEnabled(status);
                    }

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

    private void setConnectonFailed() {
        if (loading.isShowing()) loading.dismiss();
        getFragmentManager().popBackStack();

        Toast.makeText(getActivity(), "Gagal mendapatkan data perangkat", Toast.LENGTH_SHORT).show();
    }

    private Runnable runnable = new Runnable() {
        public void run() {

            millisecondTime = SystemClock.uptimeMillis() - startTime;

            seconds = (int) (millisecondTime / 1000);

            minutes = seconds / 60;

            hour = minutes / 60;

            seconds = seconds % 60;

            tvTimer.setText(String.format("%02d", hour) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));

            handler.postDelayed(this, 0);
        }

    };

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

    private void buttonListener() {
        btnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    loading.show();
                    pahoMqttClient.publishMessage(client, status ? "0" : "1", status ? 0 : 1, "pejaten/home/lamp");
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        btnCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etPrice.getText().toString().isEmpty()) {
                    PriceDialog dialog = PriceDialog.newInstance(time, power, etPrice.getText().toString());
                    dialog.show(getActivity().getFragmentManager(), PriceDialog.class.getSimpleName());
                } else {
                    Toast.makeText(getActivity(), "Mohon masukkan data perhitungan terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
