package com.example.rubiksgps;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;

public class notifications extends AppCompatActivity{
    private int ALL_PERMISSIONS = 1;
    ToggleButton speedSwitch;
    ToggleButton positionSwitch;
    ToggleButton extbatterySwitch;
    ToggleButton gpsbatterySwitch;
    ToggleButton accSwitch;
    EditText speedEditText;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == ALL_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        speedSwitch = (ToggleButton) findViewById(R.id.speedSwitch);
        positionSwitch = (ToggleButton) findViewById(R.id.positionSwitch);
        extbatterySwitch = (ToggleButton) findViewById(R.id.extbatterySwitch);
        gpsbatterySwitch = (ToggleButton) findViewById(R.id.gpsbatterySwitch);
        accSwitch = (ToggleButton) findViewById(R.id.accSwitch);
        speedEditText = (EditText) findViewById(R.id.speedEditText);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        checkSharedPreferences();

        speedSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String speedlimitText = speedEditText.getText().toString().trim();
                if(speedSwitch.isChecked()){
                    if (speedlimitText.isEmpty()) {
                        speedEditText.setError("No puede estar vacio");
                        speedEditText.requestFocus();
                        speedSwitch.setChecked(false);
                        return;
                    }
                    if (speedlimitText.length() < 3){
                        speedEditText.setError("Ejemplo 005 km/h. Solo puedes incluir numeros a tres digitos, enteros no negativos y cantidades mayores a cero.");
                        speedEditText.requestFocus();
                        speedSwitch.setChecked(false);
                        return;
                    }
                    if(speedlimitText.equals("000")){
                        speedEditText.setError("No puede ser 000. Solo puedes incluir numeros a tres digitos, enteros no negativos y cantidades mayores a cero.");
                        speedEditText.requestFocus();
                        speedSwitch.setChecked(false);
                        return;
                    }
                }

                if(speedSwitch.isChecked()){
                    mEditor.putString(getString(R.string.checkStatusString), "True");
                    mEditor.commit();
                }else{
                    mEditor.putString(getString(R.string.checkStatusString), "False");
                    mEditor.commit();
                }
                checkStatusSpeed();

            }//This closes OnClick

        });//This closes OnClickListener, OnClick upper class

        positionSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(positionSwitch.isChecked()){
                    mEditor.putString(getString(R.string.checkStatusPosition), "True");
                    mEditor.commit();
                }else{
                    mEditor.putString(getString(R.string.checkStatusPosition), "False");
                    mEditor.commit();
                }
                checkStatusPosition();
            }
        });//This closes position Switch

        extbatterySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(extbatterySwitch.isChecked()){
                    mEditor.putString(getString(R.string.checkStatusExtBattery), "True");
                    mEditor.commit();
                }else{
                    mEditor.putString(getString(R.string.checkStatusExtBattery), "False");
                    mEditor.commit();
                }
                checkStatusExtBattery();
            }
        });//This closes External Battery OnClick

        gpsbatterySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gpsbatterySwitch.isChecked()){
                    mEditor.putString(getString(R.string.checkStatusGPSBattery), "True");
                    mEditor.commit();
                }else{
                    mEditor.putString(getString(R.string.checkStatusGPSBattery), "False");
                    mEditor.commit();
                }
                checkStatusGPSBattery();
            }
        });// This closes GPS Battery OnClick

        accSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(accSwitch.isChecked()){
                    mEditor.putString(getString(R.string.checkStatusACCSwitch), "True");
                    mEditor.commit();
                }else{
                    mEditor.putString(getString(R.string.checkStatusACCSwitch), "False");
                    mEditor.commit();
                }
                checkStatusACC();
            }
        });

    }//This closes onCreate


    private void checkSharedPreferences() {
        String checkStatusString = mPreferences.getString(getString(R.string.checkStatusString), "False"); //String para SpeedSwitch
        String checkStatusPosition = mPreferences.getString(getString(R.string.checkStatusPosition), "False");
        String checkStatusExtBattery = mPreferences.getString(getString(R.string.checkStatusExtBattery), "False");
        String checkStatusGPSBattery = mPreferences.getString(getString(R.string.checkStatusGPSBattery), "False");
        String checkStatusACC = mPreferences.getString(getString(R.string.checkStatusACCSwitch), "False");


        if(checkStatusString.equals("False")){
            speedSwitch.setChecked(false);
        }else{
            speedSwitch.setChecked(true);
        }

        if(checkStatusPosition.equals("False")){
            positionSwitch.setChecked(false);
        }else{
            positionSwitch.setChecked(true);
        }

        if(checkStatusExtBattery.equals("False")){
            extbatterySwitch.setChecked(false);
        }else{
            extbatterySwitch.setChecked(true);
        }

        if(checkStatusGPSBattery.equals("False")){
            gpsbatterySwitch.setChecked(false);
        }else{
            gpsbatterySwitch.setChecked(true);
        }

        if(checkStatusACC.equals("False")){
            accSwitch.setChecked(false);
        }else{
            accSwitch.setChecked(true);
        }


    }///////////////////////////////////PREFERENCIAS AL INICIAR LA ACTIVIDAD////////////////////////////////

    private void checkStatusSpeed() {
        if(speedSwitch.isChecked()){
            activarSpeed();
        }else{
            desactivarSpeed();
        }
    }

    private void checkStatusPosition(){
        if(positionSwitch.isChecked()){
            activarPosition();
        }else{
            desactivarPosition();
        }
    }

    private void checkStatusExtBattery() {
        if(extbatterySwitch.isChecked()){
            activarExtBattery();
        }else{
            desactivarExtBattery();
        }
    }

    private void checkStatusGPSBattery() {
        if(gpsbatterySwitch.isChecked()){
            activarGPSBattery();
        }else{
            desactivarGPSBattery();
        }
    }

    private void checkStatusACC() {
        if(accSwitch.isChecked()){
            activarACC();
        }else{
            desactivarACC();
        }
    }





    private void desactivarSpeed(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        String Message = "nospeed280697";
        //Send message
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, Message, null, null);
        Toast.makeText(this, "Desactivando limite de velocidad", Toast.LENGTH_SHORT).show();

    }

    private void activarSpeed() {
        //Activar las notificaciones de velocidad
        String speedlimitText = speedEditText.getText().toString().trim();
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        String Message = "speed280697 "+speedlimitText;
        //Send message
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, Message, null, null);
        Toast.makeText(this, "Activando limite de velocidad", Toast.LENGTH_SHORT).show();
    }

    private void desactivarPosition(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        String Message = "nomove280697";
        //Send message
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, Message, null, null);
        Toast.makeText(this, "Desactivando alarma de movimiento", Toast.LENGTH_SHORT).show();
    }

    private void activarPosition(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        String Message = "move280697 0007";
        //Send message
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, Message, null, null);
        Toast.makeText(this, "Activando alarma de movimiento", Toast.LENGTH_SHORT).show();
    }

    private void activarExtBattery(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        String Message = "extpower280697 on";
        //Send message
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, Message, null, null);
        Toast.makeText(this, "Activando alarma de extracción de bateria", Toast.LENGTH_SHORT).show();

    }

    private void desactivarExtBattery(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        String Message = "extpower280697 off";
        //Send message
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, Message, null, null);
        Toast.makeText(this, "Desactivando alarma de extracción de bateria", Toast.LENGTH_SHORT).show();

    }

    private void desactivarGPSBattery(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        String Message = "lowbattery280697 off";
        //Send message
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, Message, null, null);
        Toast.makeText(this, "Desactivando alarma de bateria baja", Toast.LENGTH_SHORT).show();

    }

    private void activarGPSBattery(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        String Message = "lowbattery280697 on";
        //Send message
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, Message, null, null);
        Toast.makeText(this, "Activando alarma de bateria baja", Toast.LENGTH_SHORT).show();

    }

    private void activarACC(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        String Message = "acc280697";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, Message, null, null);
        Toast.makeText(this, "Activando alarma de encendido", Toast.LENGTH_SHORT).show();
    }

    private void desactivarACC(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        String Message = "noacc280697";
        //Send message
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, Message, null, null);
        Toast.makeText(this, "desactivando alarma de encendido", Toast.LENGTH_SHORT).show();
    }



}//This closes the class
