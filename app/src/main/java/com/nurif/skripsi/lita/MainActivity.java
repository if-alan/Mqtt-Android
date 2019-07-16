package com.nurif.skripsi.lita;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nurif.skripsi.lita.fragment.HomeFragment;
import com.nurif.skripsi.lita.mqtt.PahoMqttClient;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Random;

import static com.nurif.skripsi.lita.utils.Constants.CLIENT;
import static com.nurif.skripsi.lita.utils.Constants.PAHO_MQTT_CLIENT;

public class MainActivity extends AppCompatActivity {

    private MqttAndroidClient client;
    private PahoMqttClient pahoMqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment(), HomeFragment.class.getClass().getSimpleName())
                .commit();
    }

    public void setContent(Fragment fragment) {
        String tag = fragment.getClass().getSimpleName();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit);

        fragmentTransaction
                .replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(tag)
                .commit();
    }

    public void setClient(MqttAndroidClient client) {
        this.client = client;
    }

    public MqttAndroidClient getClient() {
        return client;
    }

    public void setPahoMqttClient(PahoMqttClient pahoMqttClient) {
        this.pahoMqttClient = pahoMqttClient;
    }

    public PahoMqttClient getPahoMqttClient() {
        return pahoMqttClient;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            setAlertDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void setAlertDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Apakah kamu yakin untuk memutuskan hubungan?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (client.isConnected()) {
                            try {
                                client.disconnect();
                                getSupportFragmentManager().popBackStack();
                            } catch (MqttException e) {

                            }
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
