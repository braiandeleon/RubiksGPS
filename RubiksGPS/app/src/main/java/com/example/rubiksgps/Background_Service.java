package com.example.rubiksgps;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class Background_Service extends Service {

    private static boolean isRunning;
    private static boolean isRinging;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    BroadcastReceiver mReceiver;
    private MediaPlayer player;
    private MediaPlayer vstarted;
    private MediaPlayer vstopped;

    private final String Admin = "5639082350";
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            processReceive(context, intent);
        }



        private void processReceive(Context context, Intent intent) {
            Toast.makeText(context, "DDDD", Toast.LENGTH_SHORT).show();
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
                assert address != null;
                if (address.equals(Admin)) {
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    assert am != null;
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
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

                    if (body.contains("test")) {
                        player.setLooping(true);
                        player.start();
                        isRinging = true;
                        Toast.makeText(context, "Prueba exitosa.", Toast.LENGTH_SHORT).show();
                        AudioManager am1 = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        assert am1 != null;
                        am1.setStreamVolume(AudioManager.STREAM_MUSIC, am1.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                        //player.setLooping(true);
                        // player.start();

//Para realizar pruebas

                    }

                }
                mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                mEditor = mPreferences.edit();
                String GPS_DEVICE_NUMBER = mPreferences.getString(context.getString(R.string.GPS_DEVICE_NUMBER), "");
                if (GPS_DEVICE_NUMBER != null && !GPS_DEVICE_NUMBER.isEmpty()) {
                    if (address.equals(GPS_DEVICE_NUMBER)) {
                        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        assert am != null;
                        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                        MainActivity.notifications_player.start();
                        //El mensaje debe contener o empezar con sensor alarm!
                        if (body.contains("alarm!")) {
                            player.setLooping(true);
                            player.start();
                            isRinging = true;
                            Toast.makeText(context, "Alarma: vehiculo en riesgo.", Toast.LENGTH_SHORT).show();
                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            long[] pattern = {0, 250, 100,250};
                            //int[] mAmplitudes = new int[]{0, 255, 255, 255, 255, 255, 255, 255};
                            // Vibrate for 500 milliseconds
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                v.vibrate(VibrationEffect.createWaveform(pattern, 0));
                            } else {
                                //deprecated in API 26
                                v.vibrate(pattern, 0);
                            }
                        }
                        if (body.contains("no gps")||body.contains("nogps")) {
                            player.setLooping(true);
                            player.start();
                            isRinging = true;
                            Toast.makeText(context, "Alarma: Sin señal de GPS.", Toast.LENGTH_SHORT).show();
                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            long[] pattern = {0, 250, 100,250};
                            //int[] mAmplitudes = new int[]{0, 255, 255, 255, 255, 255, 255, 255};
                            // Vibrate for 500 milliseconds
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                v.vibrate(VibrationEffect.createWaveform(pattern, 0));
                            } else {
                                //deprecated in API 26
                                v.vibrate(pattern, 0);
                            }
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
                            //El mensaje debe contener o empezar con sensor alarm!
                        if (body.contains("acc on!")) { vstarted.start(); }
                        //El mensaje debe contener o empezar con sensor alarm!
                        if (body.contains("acc off!")) { vstopped.start(); }
                }



            }}
            //Asigna los mensajes que llegan a body y address

            //CONFIGURATIONS ONLY FOR ADMIN

        }



    }

    @Override
    public void onCreate() {
        // get an instance of the receiver in your service



        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        mReceiver = new MyReceiver();
        registerReceiver(mReceiver, filter);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        isRunning = true;

        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Servicio disponible")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_satellitelogo)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
    player = MediaPlayer.create(Background_Service.this , R.raw.siren);
    vstarted = MediaPlayer.create(Background_Service.this, R.raw.vstarted);
    vstopped = MediaPlayer.create(Background_Service.this, R.raw.vstopped);

    return  START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        isRinging = false;
        super.onDestroy();
        player.stop();
        player = MediaPlayer.create(Background_Service.this , R.raw.siren);
        player.setLooping(true);
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.cancel();
        Toast.makeText(this, "El servicio se detuvo!", Toast.LENGTH_SHORT).show();
       // unregisterReceiver(mReceiver);


    }
    public static boolean isRunning() {
        return isRunning;
    }

    public static boolean isRinging() {
        return isRinging;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
