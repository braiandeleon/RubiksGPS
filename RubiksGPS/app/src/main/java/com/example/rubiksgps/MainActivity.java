package com.example.rubiksgps;

import android.Manifest;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.rubiksgps.record.TextRecord;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    EditText txt_message;
    ImageButton button;
    public TextView userDisplay;
    public TextView vehicleDisplay;
    public TextView emailDisplay;
    private ToggleButton botonqueseguarda;
    private ToggleButton sosButton;
    private ImageButton imageButtonRefresh;
    private ImageButton imageButton2;
    private ToggleButton enginebutton;
    public TextView mensaje;
    public TextView cadena;
    public TextView batteryTextView,signalTextView, powerTextView;
    ImageButton imageButtonGPS;
    public static MediaPlayer mp;
    public  static  MediaPlayer not;
    public int count = 0;
    public static MainActivity activityA;
    private int ALL_PERMISSIONS = 1;
    final String[] permissions = new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.VIBRATE};



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activityA = this;
        Boolean newString;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            newString = extras.getBoolean("EXIT");
            if (newString=true) {
                newString = false;
                Toast.makeText(this, "Suscripción finalizada", Toast.LENGTH_SHORT).show();
                finish();
            }}

        userDisplay = (TextView) findViewById(R.id.userDisplay);
        vehicleDisplay= (TextView) findViewById(R.id.vehicleDisplay);
        emailDisplay = (TextView) findViewById(R.id.emailDisplay);


        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        String stringUserDisplay = mPreferences.getString(getString(R.string.userDisplay), "");
        String stringVehicleDisplay = mPreferences.getString(getString(R.string.vehicleDisplay), "");
        String stringEmailDisplay = mPreferences.getString(getString(R.string.emailDisplay), "");
        userDisplay.setText(stringUserDisplay);
        vehicleDisplay.setText(stringVehicleDisplay);
        emailDisplay.setText(stringEmailDisplay);
//Codigo redundante
        imageButtonGPS = (ImageButton) findViewById(R.id.imageButtonGPS);
        imageButtonRefresh = (ImageButton) findViewById(R.id.imageButtonRefresh);
        imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
        batteryTextView = (TextView) findViewById(R.id.batteryTextView);
        signalTextView = (TextView) findViewById(R.id.signalTextView);
        powerTextView = (TextView) findViewById(R.id.powerTextView);
        mensaje = (TextView) findViewById(R.id.mensaje);

        mensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ubicacion();
            }
        });
        cadena = (TextView) findViewById(R.id.cadena);
        sosButton = (ToggleButton) findViewById(R.id.sosButton);
        enginebutton = (ToggleButton) findViewById(R.id.enginebutton);
        final TextView textodelLogo =  (TextView) findViewById(R.id.textodelLogo);
        final TextView NumeroActuaizacion = findViewById(R.id.textNumeroUltimaActualizacion);
        ImageButton btnStop = findViewById(R.id.btnStop);////boton que detiene el sonido de alarma
        ImageButton pagoButton = findViewById(R.id.pagoButton);
        ImageButton parkingbutton = findViewById(R.id.imageButton10);
        button = (ImageButton)   findViewById(R.id.button);
        ImageButton imageButtonNotifications = findViewById(R.id.buttonNotifications);
        ConstraintLayout your_Layout = (ConstraintLayout) findViewById(R.id.motherlayout);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        botonqueseguarda = (ToggleButton)   findViewById(R.id.botonqueseguarda);
        checkSharedPreferences();
        imageButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { checkMessage(); }
        });
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ToastServicio();






              //--------------------------------------------------------------------------------------------->

            }
        });
        enginebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enginebutton.isChecked()){
                    mEditor.putString(getString(R.string.engineStatus), "False");
                    mEditor.commit();
                }else{
                    mEditor.putString(getString(R.string.engineStatus), "True");
                    mEditor.commit();
                }
                engine_state();
            }
        });

        botonqueseguarda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(botonqueseguarda.isChecked()){
                    mEditor.putString(getString(R.string.EstadoSwitchArm), "False");
                    mEditor.commit();
                }else{
                    mEditor.putString(getString(R.string.EstadoSwitchArm), "True");
                    mEditor.commit();
                }
                arm_state();
            }
        });
        imageButtonNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openNotifications();
            }
        });
        pagoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPagos();
            }
        });
        parkingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ubicacion(); logoutCommand();}
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mp.stop();
                silentmodebuttonclick();
                mp = MediaPlayer.create(MainActivity.this, R.raw.siren);
                mp.setLooping(true);


            }
        });
        mp = MediaPlayer.create(MainActivity.this, R.raw.siren);
        not = MediaPlayer.create(this, R.raw.not);
        //BOTON QUE ABRE LA VENTANA DE GPSWOX
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebBrowser();
            }
        });
        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sosCommand();
            }
        });
        imageButtonGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutMethod();
            }
        });


        Thread t=new Thread(){
            @Override
            public void run(){
                while(!interrupted()){
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                count++;

                                String segundos = String.valueOf(count);
                                NumeroActuaizacion.setText("hace "+segundos+" segundos");
                                String NuevoValor = mPreferences.getString(getString(R.string.EstadoSwitchArm), "False");
                                String EngineStatus = mPreferences.getString(getString(R.string.engineStatus), "True");

                                if(NuevoValor.equals("False")){
                                    botonqueseguarda.setBackgroundResource(R.drawable.shield_off);
                                    botonqueseguarda.setChecked(false);}


                                if(NuevoValor.equals("True")){
                                    botonqueseguarda.setBackgroundResource(R.drawable.shield_on);
                                    botonqueseguarda.setChecked(true); }


                                if(EngineStatus.equals("True")){
                                    enginebutton.setBackgroundResource(R.drawable.engine_on);
                                    enginebutton.setChecked(true); }


                                if (EngineStatus.equals("False")){
                                    enginebutton.setBackgroundResource(R.drawable.engine_off);
                                    enginebutton.setChecked(false);}


                                if(mp.isPlaying()){
                                    vibrate_alarm();}

                                String SOSValor = mPreferences.getString(getString(R.string.SOSmessageStatus), "False");
                                String SOSPedido = mPreferences.getString(getString(R.string.SOSPedido), "False");
                                if(SOSPedido.equals("True")){
                                    if(SOSValor.equals("True")){
                                        MensajeAuxilio();
                                        mEditor.putString(getString(R.string.SOSPedido), "False");
                                        mEditor.commit();
                                        mEditor.putString(getString(R.string.SOSmessageStatus), "False");
                                        mEditor.commit();
                                    }
                                }
                                String ParkLocationWasAsked = mPreferences.getString(getString(R.string.ParkingSpotLocationWasAsked), "False");
                                String AskParkLocation = mPreferences.getString(getString(R.string.AskParkLocationString), "False");
                                if(ParkLocationWasAsked.equals("True")){
                                    if(AskParkLocation.equals("True")){
                                        openMaps();
                                        mEditor.putString(getString(R.string.ParkingSpotLocationWasAsked), "False");
                                        mEditor.commit();
                                        mEditor.putString(getString(R.string.AskParkLocationString), "False");
                                        mEditor.commit();
                                    }
                                }

                                String power = mPreferences.getString(getString(R.string.POWER), "");
                                if(power!=null&&!power.isEmpty()){
                                    String separado[] = power.split("\n");
                                    String BatteryCiento = separado[1];
                                    String Encendido = separado[4];
                                    String GSM = separado[6];
                                    String arregloGSM[] = GSM.split(": ");
                                    String GSMsolonumero = arregloGSM[1];
                                    int GSMinteger = Integer.parseInt(GSMsolonumero);
                                    int resultadoGSMinteger = ((GSMinteger)*100)/32;
                                    batteryTextView.setText(BatteryCiento);
                                    signalTextView.setText("Signal: "+resultadoGSMinteger+"%");
                                    powerTextView.setText(Encendido);
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }
                }
            }
        };
        t.start();





    } //termina on Create

    private void ToastServicio() {
        if(Background_Service.isRunning()){
            Toast.makeText(this, "Servicio previamente iniciado.", Toast.LENGTH_SHORT).show();
        }else{
            startForegroundService();
        }

    }


    public static MainActivity getInstance(){
        return   activityA;

    }

    public void checkSharedPreferences(){
        String ValorArmSwitch = mPreferences.getString(getString(R.string.EstadoSwitchArm), "False");
        String EstadoMotor = mPreferences.getString("estadoEngine", "True");

        if(ValorArmSwitch.equals("False")){
            botonqueseguarda.setChecked(false);
        }else{
            botonqueseguarda.setChecked(true);
        }

        if(EstadoMotor.equals("True")){
            enginebutton.setChecked(true);
        }else{
            enginebutton.setChecked(false);
        }
    }
    private void logoutMethod(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }




    private void checkMessage(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");


        if(phoneNumber!=null&&!phoneNumber.isEmpty()){
            String Message = "check280697";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, Message, null, null);
            PendingIntent sentIntent = null, deliveryIntent = null;
            Toast.makeText(this, "Actualizando datos de barra superior...", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "GPS Device Number not configured.", Toast.LENGTH_SHORT).show();
        }


    }

    private void startForegroundService(){
        Intent serviceIntent = new Intent(this, Background_Service.class);
        serviceIntent.putExtra("inputExtra", "El sistema se encuentra activo.");
        ContextCompat.startForegroundService(this, serviceIntent);
        Toast.makeText(this, "Iniciando servicio...", Toast.LENGTH_SHORT).show();
    }
    private void MensajeAuxilio(){
        String miContactoSOS = mPreferences.getString(getString(R.string.SOS_ALERT_NUMBER), "");
        if(miContactoSOS!=null&&!miContactoSOS.isEmpty()){
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            String UbicacionFinal = mPreferences.getString(getString(R.string.mensajeubicacion), "");
            String Message = currentDateTimeString+"\n¡Auxilio!, estoy en "+UbicacionFinal;

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(miContactoSOS, null, Message, null, null);
            Toast.makeText(this, "Mensaje de ayuda enviado exitosamente...", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "GPS Device Number not configured.", Toast.LENGTH_SHORT).show();
        }

    }
    private void AskParkSpotMethod(){
        ubicacion();
        mEditor.putString(getString(R.string.ParkingSpotLocationWasAsked), "True");
        mEditor.commit();

        Toast.makeText(this, "Pidiendo ParkSpot Location...", Toast.LENGTH_SHORT).show();
    }
    private void sosCommand(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        if(phoneNumber!=null&&!phoneNumber.isEmpty()){
            ubicacion();
            mEditor.putString(getString(R.string.SOSmessageStatus), "True");
            mEditor.commit();
            // Este comando pone el valor TRUE a la condicion Toast.makeText(this, "Mensaje de ayuda enviado...", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "GPS Device Number not configured.", Toast.LENGTH_SHORT).show();
        }

    }
    private void logoutCommand(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        if(phoneNumber!=null&&!phoneNumber.isEmpty()){
            mEditor.putString(getString(R.string.AskParkLocationString), "True");
            mEditor.commit();
        }else{
            Toast.makeText(this, "GPS Device Number not configured.", Toast.LENGTH_SHORT).show();
        }


    }
    private void openNotifications(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        if(phoneNumber!=null&&!phoneNumber.isEmpty()){
            Intent intent = new Intent(this, notifications.class);
            startActivity(intent);
// Estas se pueden borrar si ya no son necesarias           String getNewUserConfig = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
//            Toast.makeText(this, getNewUserConfig, Toast.LENGTH_SHORT).show();
//            String getNewUserConfigSOS = mPreferences.getString(getString(R.string.SOS_ALERT_NUMBER), "");
//            Toast.makeText(this, getNewUserConfigSOS, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "GPS Device Number not configured.", Toast.LENGTH_SHORT).show();
        }

    }
    private void openPagos(){
        Intent intent = new Intent(this, pagos.class);
        startActivity(intent);
    }
    public void openMaps (){
        String UbicacionFinal = mPreferences.getString(getString(R.string.mensajeubicacion), "");
        if(UbicacionFinal!=null&&!UbicacionFinal.isEmpty()){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(UbicacionFinal));
            startActivity(browserIntent);
        }else{
            Toast.makeText(this, "No hay datos, por favor presiona antes el boton obtener ubicación.", Toast.LENGTH_LONG).show();
        }

    }
    public  void openWebBrowser(){

        Intent intent = new Intent(this, com.example.rubiksgps.web_browser.class);
        startActivity(intent);
    }
    public void ubicacion(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        if(phoneNumber!=null&&!phoneNumber.isEmpty()){
            String Message = "fix010s001n280697";
            if (!phoneNumber.toString().equals("") || !txt_message.getText().toString().equals("")) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, Message, null, null);

                Toast.makeText(this, "Obteniendo ubicación...", Toast.LENGTH_SHORT).show();
                count=0;


            } else {
                Toast.makeText(this, "Comando no enviado", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "GPS Device Number not configured.", Toast.LENGTH_SHORT).show();
        }



    }
    public void vibrate_alarm(){
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
    public void arm_state() {
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS);
        if (permissionCheck== PackageManager.PERMISSION_GRANTED) {
            if (botonqueseguarda.isChecked()) {
                arm_on();
            }
            else if(!botonqueseguarda.isChecked()) {
                arm_off();
            }
        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 0) ;
        }
    }
    public void engine_state(){
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS);
        if (permissionCheck== PackageManager.PERMISSION_GRANTED) {
            if (enginebutton.isChecked()) {
                engine_on();
            }
            else if(!enginebutton.isChecked()) {
                engine_off();
            }
        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 0) ;
        }


    }
    private void arm_off() {
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        if(phoneNumber!=null&&!phoneNumber.isEmpty()){
            String Message = "disarm280697";

            if (!phoneNumber.toString().equals("") || !txt_message.getText().toString().equals("")) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, Message, null, null);
                count=0;

                Toast.makeText(this, "Desactivando sistema...", Toast.LENGTH_SHORT).show();



            } else {
                Toast.makeText(this, "Comando no enviado", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "GPS Device Number not configured.", Toast.LENGTH_SHORT).show();
        }


    }
    private void arm_on() {
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        if(phoneNumber!=null&&!phoneNumber.isEmpty()){
            String Message = "arm280697";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, Message, null, null);
            Toast.makeText(this, "Activando sistema...", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "GPS Device Number not configured.", Toast.LENGTH_SHORT).show();
        }


    }
    private void engine_on(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        if(phoneNumber!=null&&!phoneNumber.isEmpty()){
            String Message = "resume280697";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, Message, null, null);
            Toast.makeText(this, "Encendiendo motor...", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "GPS Device Number not configured.", Toast.LENGTH_SHORT).show();
        }

    }
    private void engine_off(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        if(phoneNumber!=null&&!phoneNumber.isEmpty()){
            String Message = "stop280697";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, Message, null, null);
            Toast.makeText(this, "Apagando motor...", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "GPS Device Number not configured.", Toast.LENGTH_SHORT).show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void silentmodebuttonclick(){

        Intent myService = new Intent(this, Background_Service.class);

        if(Background_Service.isRinging()){
            stopService(myService);
            stopService(myService);
            stopService(myService);
            Toast.makeText(this, "Se detuvo la alarma activa. ", Toast.LENGTH_LONG).show();
            startForegroundService();


        }else{
            Toast.makeText(this, "No hay ninguna alarma activa. ", Toast.LENGTH_LONG).show();
        }



    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==ALL_PERMISSIONS){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            }
        }
    }
}

