package com.nurif.skripsi.lita;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.nurif.skripsi.lita.fragment.HomeFragment;
import com.nurif.skripsi.lita.mqtt.PahoMqttClient;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MainActivity extends AppCompatActivity {

    private MqttAndroidClient client;
    private PahoMqttClient pahoMqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** MEMASANG TAMPILAN AWAL*/
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment(), HomeFragment.class.getClass().getSimpleName())
                .commit();
    }

    /** MEMASANG TAMPILAN SESUAI KEBUTUHAN*/
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
            setBackAlertDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void setBackAlertDialog() {
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
