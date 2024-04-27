package com.example.init_project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;

    TextView nhietDo;
    TextView doAmKhongKhi;
    TextView doAmDat;
    TextView anhSang;

    SwitchCompat nutNhan1, nutNhan2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        nhietDo = findViewById(R.id.nhietDo);
        doAmKhongKhi = findViewById(R.id.doAmKhongKhi);
        doAmDat = findViewById(R.id.doAmDat);
        anhSang = findViewById(R.id.anhSang);

        nutNhan1 = findViewById(R.id.nutNhan1);
        nutNhan2 = findViewById(R.id.nutNhan2);

        mqttHelper = new MQTTHelper(
                this.getApplicationContext());

        mqttHelper.client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("mqtt", "Recv message from: " + topic + ", message: " + message.toString());
                mqttOnMessage(topic, message.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        nutNhan1.setOnCheckedChangeListener(
                (compoundButton, b) -> mqttPublish("nguyentruongthan/feeds/ai",
                        "4:" + (nutNhan1.isChecked() ? "1" : "0"))
        );

        nutNhan2.setOnCheckedChangeListener(
                (compoundButton, b) -> mqttPublish("nguyentruongthan/feeds/ai",
                        "5:" + (nutNhan2.isChecked() ? "1" : "0"))
        );
    }

    void mqttOnMessage(String topic, String message){
        //mesage = <deviceID>:<value>
        String splitMessage[] = message.split(":");
        String deviceID = splitMessage[0];
        String value = splitMessage[1];

        if(deviceID.equals("0")){//light sensor
            anhSang.setText(value + " Lux");
        }else if(deviceID.equals("1")){//humi soil sensor
            doAmDat.setText(value + "%");
        }else if(deviceID.equals("2")){//humi air sensor
            doAmKhongKhi.setText(value + "℃");
        }else if(deviceID.equals("3")){//temp sensor
            nhietDo.setText(value + "℃");
        }else if(deviceID.equals("4")){//nut nhan 1
            boolean isChecked = value.equals("1");
            nutNhan1.setChecked(isChecked);
        }else if(deviceID.equals("5")){//nut nhan 2
            boolean isChecked = value.equals("1");
            nutNhan2.setChecked(isChecked);
        }
    }

    void mqttPublish(String topic, String payload){
        mqttHelper.publish(topic, payload);
    }


}