package com.example.rubiksgps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class ReceiverSMS extends BroadcastReceiver  {
    private final String Admin = "+525639082350";
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    public void onReceive(Context context, Intent intent){
        {String message = "";
        String body ="";
        String address="";
        Bundle extras = intent.getExtras();
        if (extras != null) {
            //Get content message
            Object[] smsExtras = (Object[]) extras.get("pdus");
            //Read all message
            for (int i = 0; i < smsExtras.length; i++) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtras[i]);
                body = sms.getMessageBody();
                address = sms.getOriginatingAddress();
            }
        Intent smsReceivedIntent = new Intent("custom.action.SMS_RECEIVED");
        smsReceivedIntent.putExtra("body",body);
        smsReceivedIntent.putExtra("address",address);
        context.sendBroadcast(smsReceivedIntent);}


    } // Send SMS Info to MainActivity
        processReceive(context, intent);
    }

   private void processReceive(Context context, Intent intent) {
       Bundle extras = intent.getExtras();
        String message = "";
        String body = "";
        String address = "";
        if (extras != null) {
        //Get content message and Read all message
            Object[] smsExtras = (Object[]) extras.get("pdus");
            for (int i = 0; i < smsExtras.length; i++) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtras[i]);
                //Get body
                body = sms.getMessageBody();
                //Get address
                address = sms.getOriginatingAddress();
                //Get message
                message += "Mensaje de: " + address + "\n Body : " + body + "\n";

            }
        //Working with the SMS
            mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            mEditor = mPreferences.edit();
            String GPS_DEVICE_NUMBER = mPreferences.getString(context.getString(R.string.GPS_DEVICE_NUMBER), "");
            //Admin Device Messages
            if (address.equals(Admin)) {
                Toast.makeText(context, "Estamos configurando algunas cosas...", Toast.LENGTH_SHORT).show();
                if (body.contains("newUserConfig")) {
                    mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    mEditor = mPreferences.edit();
                    mEditor.putString(context.getString(R.string.NEW_GPSWOXCONFIG), body);
                    mEditor.commit();
                    Toast.makeText(context, "newUserConfig is ready.", Toast.LENGTH_SHORT).show();
                }

                if (body.contains("newGPSDEVICENUMBER")) {
                    mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    mEditor = mPreferences.edit();
                    mEditor.putString(context.getString(R.string.SMS_GPSDEVICENUMBER), body);
                    mEditor.commit();
                    String getNewUserConfig = mPreferences.getString(context.getString(R.string.SMS_GPSDEVICENUMBER), "");
                    String ConfigSaved[] = getNewUserConfig.split("\n");
                    String getGPSDeviceNumber = ConfigSaved[1];
                    mEditor.putString(context.getString(R.string.GPS_DEVICE_NUMBER), getGPSDeviceNumber);
                    mEditor.commit();
                    Toast.makeText(context, "newGPSDEVICENUMBER is "+getGPSDeviceNumber, Toast.LENGTH_SHORT).show();
                }


            }
            //GPS Device Messages
            if (GPS_DEVICE_NUMBER != null && !GPS_DEVICE_NUMBER.isEmpty()) {
                if (address.equals(GPS_DEVICE_NUMBER)) {
                    if (body.contains("lat:")) {
                        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        mEditor = mPreferences.edit();
                        String ubicacionSMS = body.substring(60, 120);

                        mEditor.putString(context.getString(R.string.mensajeubicacion), ubicacionSMS);
                        mEditor.commit();
                        mEditor.putString(context.getString(R.string.UltimoMensaje), body);
                        mEditor.commit();
                        Toast.makeText(context, "Mensaje de localizacion recibido", Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, ubicacionSMS, Toast.LENGTH_SHORT).show();
                        MainActivity.notifications_player.start();

                    }
                }
            }
        }

    }
}