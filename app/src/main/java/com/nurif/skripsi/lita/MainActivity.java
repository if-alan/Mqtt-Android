package com.nurif.skripsi.lita;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private MqttAndroidClient client;
    private PahoMqttClient pahoMqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pahoMqttClient = new PahoMqttClient();

        setConnect();
    }

    public void setConnect() {

        String urlBroker = "tcp://broker.mqtt-dashboard.com:1883";

        Random r = new Random();        //Unique Client ID for connection
        int i1 = r.nextInt(5000 - 1) + 1;
        String clientid = "mqtt" + i1;

        //Connect to Broker
        client = pahoMqttClient.getMqttClient(this, urlBroker, clientid, "", "");

        //Set Mqtt Message Callback
        mqttCallback();
    }

    public void setSubscribe(View view) {

        String topic = "nusahijau/kwhmeter";
        if (!topic.isEmpty()) {
            try {
                pahoMqttClient.subscribe(client, topic, 1);

            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    protected void mqttCallback() {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                //msg("Connection lost...");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                TextView tvMessage = (TextView) findViewById(R.id.tv_tegangan);
                if (topic.equals("mycustomtopic1")) {
                    //Add custom message handling here (if topic = "mycustomtopic1")
                } else if (topic.equals("mycustomtopic2")) {
                    //Add custom message handling here (if topic = "mycustomtopic2")
                } else {
                    String msg = "topic: " + topic + "\r\n";
                    tvMessage.append(msg);

                    try {
                        JSONObject obj = new JSONObject(message.toString());

                        String voltage = obj.getString("voltage");
                        String t_current = obj.getString("t_current");
                        String watt = obj.getString("watt");
                        String t_kwh = obj.getString("t_kwh");
                        String tagihan = obj.getString("tagihan");
                        String people = obj.getString("people");

                        tvMessage.append("Voltage: " + voltage + "\n");
                        tvMessage.append("Time current: " + t_current + "\n");
                        tvMessage.append("Watt: " + watt + "\n");
                        tvMessage.append("t_kwh: " + t_kwh + "\n");
                        tvMessage.append("tagihan: " + tagihan + "\n");
                        tvMessage.append("people: " + people + "\n\n");
                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + message + "\"");
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                startActivity(new Intent(MainActivity.this, ContentActivity.class));
                finish();
            }
        });
    }
}
