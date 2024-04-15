package com.example.init_project;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;



import java.io.UnsupportedEncodingException;

public class MQTTHelper {
  String clientId;
  MqttAndroidClient client;
  String username = "nguyentruongthan";
  String password = "aio_qNbo///71vk0I///uvOGutmbxMWMfphX8E";
  String topics[] = {"temp", "light", "humiAir", "humiSoil", "nutnhan1", "nutnhan2"};

  MQTTHelper(Context context){
    this.password = this.password.replace("/", "");
    this.clientId = MqttClient.generateClientId();
    this.client =
            new MqttAndroidClient(context, "tcp://io.adafruit.com:1883",
                    this.clientId);

    MqttConnectOptions options = new MqttConnectOptions();
    options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
    options.setUserName(this.username);
    options.setPassword(this.password.toCharArray());

    try {
      IMqttToken token = this.client.connect(options);
      token.setActionCallback(new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
          // We are connected
          MQTTHelper.this.onConnect();
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
          // Something went wrong e.g. connection timeout or firewall problems
          Log.d("mqtt", "onFailure");
        }
      });
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }
  void onConnect(){
    Log.d("mqtt", "onSuccess");
    this.subscribe();
  }
  void subscribe(){
    int qos = 1;
    try {
      for (int i = 0; i < this.topics.length; i++) {
        IMqttToken subToken = this.client.subscribe(this.username + "/feeds/" + topics[i], qos);
        subToken.setActionCallback(new IMqttActionListener() {
          @Override
          public void onSuccess(IMqttToken asyncActionToken) {
            Log.d("mqtt", "Subscribe success");
          }

          @Override
          public void onFailure(IMqttToken asyncActionToken,
                                Throwable exception) {
            Log.d("mqtt", "Subscribe faild");
          }
        });
      }
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }

  void publish(String topic, String payload){
    byte[] encodedPayload = new byte[0];
    try {
      encodedPayload = payload.getBytes("UTF-8");
      MqttMessage message = new MqttMessage(encodedPayload);
      message.setRetained(true);
      this.client.publish(topic, message);
    } catch (UnsupportedEncodingException | MqttException e) {
      e.printStackTrace();
    }
  }
}
