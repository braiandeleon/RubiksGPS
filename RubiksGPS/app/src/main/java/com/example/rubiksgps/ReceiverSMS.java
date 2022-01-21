package com.example.rubiksgps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class ReceiverSMS extends BroadcastReceiver  {
    private final String Admin = "6566605292";
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    public void onReceive(Context context, Intent intent){
        /*processReceive(context, intent);*/

    }

   private void processReceive(Context context, Intent intent) {
       Bundle extras = intent.getExtras();
        String message = "";
        String body = "";
        String address = "";
        if (extras != null) {
            //Get content message
            Object[] smsExtras = (Object[]) extras.get("pdus");
            //Read all message
            for (int i = 0; i < smsExtras.length; i++) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtras[i]);
                //Get body
                body = sms.getMessageBody();
                //Get address
                address = sms.getOriginatingAddress();
                //Get message
                message += "Mensaje de: " + address + "\n Body : " + body + "\n";
            }
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
                if (body.contains("newSOSContact")) {
                    mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    mEditor = mPreferences.edit();
                    mEditor.putString(context.getString(R.string.SOS_ALERT_BODY), body);
                    mEditor.commit();
                    String getNewUserConfigSOS = mPreferences.getString(context.getString(R.string.SOS_ALERT_BODY), "");
                    String ConfigSavedforSOS[] = getNewUserConfigSOS.split("\n");
                    String getSOSContact = ConfigSavedforSOS[1];
                    mEditor.putString(context.getString(R.string.SOS_ALERT_NUMBER), getSOSContact);
                    mEditor.commit();
                    Toast.makeText(context, "newSOSContact is "+getSOSContact, Toast.LENGTH_SHORT).show();
                }
                if (body.contains("newDisplayInfo")) {
                    mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    mEditor = mPreferences.edit();
                    mEditor.putString(context.getString(R.string.userInfoBody), body);
                    mEditor.commit();
                    String getNewUserInfo = mPreferences.getString(context.getString(R.string.userInfoBody), "");
                    String ConfigSavedInfo[] = getNewUserInfo.split("\n");


                    String stringUserDisplay = ConfigSavedInfo[1];
                    String stringVehicleDisplay = ConfigSavedInfo[2];
                    String stringEmailDisplay = ConfigSavedInfo[3];

                    mEditor.putString(context.getString(R.string.userDisplay), stringUserDisplay);
                    mEditor.commit();

                    mEditor.putString(context.getString(R.string.vehicleDisplay), stringVehicleDisplay);
                    mEditor.commit();

                    mEditor.putString(context.getString(R.string.emailDisplay), stringEmailDisplay);
                    mEditor.commit();
                    Toast.makeText(context, "New user is:  "+stringUserDisplay, Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "New vehicle is:  "+stringVehicleDisplay, Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "New email is: "+stringEmailDisplay, Toast.LENGTH_SHORT).show();
                }


                if (body.contains("Contract finished")) {
                    FirebaseAuth.getInstance().signOut();
                    Intent i = new Intent(context, MainActivity.class);        // Specify any activity here e.g. home or splash or login etc
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("EXIT", true);
                    context.startActivity(i);
                }

            }
            mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            mEditor = mPreferences.edit();
            String GPS_DEVICE_NUMBER = mPreferences.getString(context.getString(R.string.GPS_DEVICE_NUMBER), "");
            if (GPS_DEVICE_NUMBER != null && !GPS_DEVICE_NUMBER.isEmpty()) {
                if (address.equals(GPS_DEVICE_NUMBER)) {
                    //El mensaje debe contener o empezar con sensor alarm!
                    if (body.contains("alarm!")) {
                        MainActivity.mp.setLooping(true);
                        MainActivity.mp.start();
                    }
                    if (body.contains("GPRS: ON")) {
                        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        mEditor = mPreferences.edit();
                        mEditor.putString(context.getString(R.string.POWER), body); //Temporary contains all the message
                        mEditor.commit();


                    }
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
                        MainActivity.not.start();
                        String SOSValor = mPreferences.getString(context.getString(R.string.SOSmessageStatus), "False");
                        String ParkLocationWasAsked = mPreferences.getString(context.getString(R.string.AskParkLocationString), "False");
                        if (SOSValor.equals("True")) {
                            mEditor.putString(context.getString(R.string.SOSPedido), "True");
                            mEditor.commit();
                        }
                        if (ParkLocationWasAsked.equals("True")) {
                            mEditor.putString(context.getString(R.string.ParkingSpotLocationWasAsked), "True");
                            mEditor.commit();
                        }


                    }
                    if (body.equals("Tracker is activated")) {
/*                        MainActivity cls = new MainActivity();
                        if(cls.setValorb1(true)){
                            cls.loadData();
                            cls.setValorb1(false);
                            cls.saveData();*/
                        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        mEditor = mPreferences.edit();
                        mEditor.putString(context.getString(R.string.EstadoSwitchArm), "True");
                        mEditor.commit();
                        Toast.makeText(context, "GPS activado", Toast.LENGTH_SHORT).show();
                        MainActivity.not.start();

                    }
                    if (body.equals("Tracker is deactivated")) {
/*                        MainActivity cls = new MainActivity();
                        if(cls.setValorb1(true)){
                            cls.loadData();
                            cls.setValorb1(false);
                            cls.saveData();*/
                        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        mEditor = mPreferences.edit();
                        mEditor.putString(context.getString(R.string.EstadoSwitchArm), "False");
                        mEditor.commit();
                        Toast.makeText(context, "GPS desactivado.", Toast.LENGTH_SHORT).show();
                        MainActivity.not.start();

                    }
                    if (body.equals("Resume engine Succeed")) {
                        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        mEditor = mPreferences.edit();
                        mEditor.putString(context.getString(R.string.engineStatus), "True");
                        mEditor.commit();
                        Toast.makeText(context, "Motor activado.", Toast.LENGTH_SHORT).show();
                        MainActivity.not.start();
                    }
                    if (body.equals("Stop engine Succeed")) {
                        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        mEditor = mPreferences.edit();
                        mEditor.putString(context.getString(R.string.engineStatus), "False");
                        mEditor.commit();
                        Toast.makeText(context, "Motor desactivado.", Toast.LENGTH_SHORT).show();
                        MainActivity.not.start();
                    }
                }
            }


        }
        //Asigna los mensajes que llegan a body y address

        //CONFIGURATIONS ONLY FOR ADMIN

    }



}