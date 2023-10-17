// Scheduled Activation Time. Calculate value based on Thread and CurrentDate
// Background Service should be running in notifications
// Block any other website to open once in objects screen in browser
// NFC feature to arm/disarm device

package com.example.rubiksgps;
import java.time.LocalDateTime;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.HashMap;
import java.util.Objects;
import com.google.firebase.auth.FirebaseAuth;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    public SharedPreferences mPreferences;
    private final int ALL_PERMISSIONS = 1;
    final String[] permissions = new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.VIBRATE};
    private WebView browser;
    private ToggleButton set_engine_button, set_arm_button;
    private TextView batteryTextView, signalTextView, powerTextView, doorsTextView, gas_levelTextView, last_update_label;
    private Button loading_button, check_info_button;
    public static MediaPlayer siren_player,notifications_player;

    private final BroadcastReceiver smsReceivedReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            String gps_id = "6561129196";
            String admin = "+525639082350";
            Log.d("Tag", admin);
            String sms_body = intent.getStringExtra("body");
            String sms_address = intent.getStringExtra("address");
            // Check for previous settings
            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor mEditor = mPreferences.edit();
            //GPS Device Messages
            assert sms_address != null;
            if (sms_address.equals(gps_id)){
                notifications_player.start();
                LocalDateTime currentDateTime = LocalDateTime.now();
                last_update_label.setText(currentDateTime.toString());
                assert sms_body != null;
                if (sms_body.contains("GPRS")){
                    // Split the input string into lines
                    String[] sms_body_in_lines = sms_body.split("\n");
                    // Create a HashMap to store the key-value pairs
                    HashMap<String, String> sms_body_dictionary = new HashMap<>();
                    // Process each line to extract key-value pairs
                    for (String line : sms_body_in_lines) {
                        String[] parts = line.split(": ");
                        if (parts.length == 2) {
                            String key = parts[0];
                            String value = parts[1];
                            sms_body_dictionary.put(key, value);
                        }
                    }
                    //Update Top Fields Values
                    int signal_value = Integer.parseInt(Objects.requireNonNull(sms_body_dictionary.get("GSM")));
                    int signal_value_percent = ((signal_value)*100)/32;
                    signalTextView.setText(signal_value_percent+"%");
                    batteryTextView.setText(sms_body_dictionary.get("Bat"));
                    powerTextView.setText(sms_body_dictionary.get("ACC"));
                    gas_levelTextView.setText(sms_body_dictionary.get("Oil"));
                    String doors_text = sms_body_dictionary.get("Door");
                    assert doors_text != null;
                    if (doors_text.equals("ON")){
                        doorsTextView.setText("Doors: Open");
                    } else if (doors_text.equals("OFF")) {
                        doorsTextView.setText("Doors: Closed");
                    }
                }
                if (sms_body.contains("Stop engine Succeed")){
                    set_engine_button.setChecked(false);
                    mEditor.putString(getString(R.string.engine_state_string),"False");
                    mEditor.apply();

                } else if (sms_body.contains("Resume engine Succeed")) {
                    set_engine_button.setChecked(true);
                    mEditor.putString(getString(R.string.engine_state_string),"True");
                    mEditor.apply();
                }
                if (sms_body.contains("Tracker is activated")){
                    set_arm_button.setChecked(true);
                    mEditor.putString(getString(R.string.arm_state_string),"True");
                    mEditor.apply();
                } else if (sms_body.contains("Tracker is deactivated")) {
                    set_arm_button.setChecked(false);
                    mEditor.putString(getString(R.string.arm_state_string),"False");
                    mEditor.apply();
                }
                if (sms_body.contains("alarm!")) {
                    siren_player.setLooping(true);
                    siren_player.start();
                    vibrate_alarm();
                }
            }
            //Admin Device Messages
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override //Decorator Override makes function to execute first
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==ALL_PERMISSIONS){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Tag", "Empty if");
            }else {Toast.makeText(this, "Please grant permissions!", Toast.LENGTH_SHORT).show();}
        }
    }
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("custom.action.SMS_RECEIVED");
        registerReceiver(smsReceivedReceiver, intentFilter);
        loading_button.setVisibility(View.VISIBLE);
        browser.setWebViewClient(new MyWebViewClient(){
            public void onPageFinished(WebView view, String url){
                if(browser.getUrl().equals("https://www.gpswox.com/en/sign-in")){
                    //Automatic login
                    super.onPageFinished(view, url);
                    final String username = "braiandeleon+03@gmail.com";
                    final String password = "GHPbw5";
                    final String server = "eu";
                    Log.d("Tag", server);
                    final String xs = "javascript:" +
                            "document.getElementById('sign-in-form-password').value = '" + password + "';"  +
                            "document.getElementById('sign-in-form-email').value = '" + username + "';";
                    String js = "javascript:(function(){"+
                            "l=document.getElementsByClassName('sign-in-form-submit button button-color-green is-float-right')[0];"+
                            "l.click();"+
                            "})()";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        browser.evaluateJavascript(xs, s -> {});
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        browser.evaluateJavascript(js, s -> {});
                    }
                }
                if(browser.getUrl().contains("objects")){
                    super.onPageFinished(view, url);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        loading_button.setVisibility(View.GONE);
                        browser.setVisibility(View.VISIBLE);
                    }, 2000);
                }
            }
        });
        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        browser.loadUrl("https://www.gpswox.com/en/sign-in");
    }
    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(smsReceivedReceiver);
    }
    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Context and not visible components initialization
        siren_player = MediaPlayer.create(MainActivity.this, R.raw.siren);
        notifications_player = MediaPlayer.create(this, R.raw.not);
        //Component initialization
        ImageButton silence_button = findViewById(R.id.silence_button);//Private Components. Accessible by other than onCreate (parent) function
        ImageButton notifications_button = findViewById(R.id.buttonNotifications);
        ImageButton logout_button = findViewById(R.id.logout_button);
        ImageButton start_service = findViewById(R.id.start_service_button);
        browser = (WebView) findViewById(R.id.webView);//Public Components
        check_info_button = findViewById(R.id.ButtonRefresh);
        loading_button = findViewById(R.id.loading_button);
        last_update_label = findViewById(R.id.textNumeroUltimaActualizacion);
        set_engine_button = findViewById(R.id.enginebutton);
        set_arm_button = findViewById(R.id.botonqueseguarda);
        batteryTextView = findViewById(R.id.batteryTextView);
        signalTextView = findViewById(R.id.signalTextView);
        powerTextView = findViewById(R.id.powerTextView);
        doorsTextView = findViewById(R.id.doors_text);
        gas_levelTextView = findViewById(R.id.gas_level_text);
        // Check for previous settings
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String stored_arm_state = mPreferences.getString(getString(R.string.arm_state_string), "null");
        String stored_engine_state = mPreferences.getString(getString(R.string.engine_state_string), "null");
//        .Toast.makeText(getApplicationContext(), stored_arm_state, Toast.LENGTH_SHORT).show();
//        .Toast.makeText(getApplicationContext(), stored_engine_state, Toast.LENGTH_SHORT).show();
        set_arm_button.setChecked(Boolean.parseBoolean(stored_arm_state));
        set_engine_button.setChecked(Boolean.parseBoolean(stored_engine_state));
        //Browser Initialization
        browser.setWebViewClient(new MyWebViewClient(){
            public void onPageFinished(WebView view, String url){
                if(browser.getUrl().equals("https://www.gpswox.com/en/sign-in")){
                    //Automatic login
                    super.onPageFinished(view, url);
                    final String username = "braiandeleon+03@gmail.com";
                    final String password = "GHPbw5";
                    final String server = "eu";
                    Log.d("Tag", server);
                    final String xs = "javascript:" +
                            "document.getElementById('sign-in-form-password').value = '" + password + "';"  +
                            "document.getElementById('sign-in-form-email').value = '" + username + "';";
                    String js = "javascript:(function(){"+
                            "l=document.getElementsByClassName('sign-in-form-submit button button-color-green is-float-right')[0];"+
                            "l.click();"+
                            "})()";
                    browser.evaluateJavascript(xs, s -> {});
                    browser.evaluateJavascript(js, s -> {});
                }
                if(browser.getUrl().contains("objects")){
                    super.onPageFinished(view, url);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        loading_button.setVisibility(View.GONE);
                        browser.setVisibility(View.VISIBLE);
                    }, 2000);
                }
            }
        });
        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        browser.loadUrl("https://www.gpswox.com/en/sign-in");
        //Components Listeners
        check_info_button.setOnClickListener(v -> checkMessage());
        start_service.setOnClickListener(v -> is_service_running());
        notifications_button.setOnClickListener(v -> openNotifications());
        logout_button.setOnClickListener(v -> logoutMethod());
        silence_button.setOnClickListener(v -> {
            siren_player.stop(); //stops local media player siren sound
            silence_service_ringing_siren(); //stops service siren sound
            siren_player = MediaPlayer.create(MainActivity.this, R.raw.siren);
            siren_player.setLooping(true);
        });
        set_engine_button.setOnClickListener(v -> {
            if(set_engine_button.isChecked()){
                set_engine_button.setChecked(false);
                engine_on();
            }else{
                set_engine_button.setChecked(true);
                engine_off();
            }
        });
        set_arm_button.setOnClickListener(v -> {
            if(set_arm_button.isChecked()){
                set_arm_button.setChecked(false);
                arm_on();
            }else{
                set_arm_button.setChecked(true);
                arm_off();
            }
        });
    }
    // Create a custom WebViewClient to handle URL loading within the WebView.
    private static class MyWebViewClient extends WebViewClient {
//        .List<String> whiteHosts = Arrays.asList("http://europe.gpswox.com/objects",  "https://www.gpswox.com/en/sign-in");
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url); // Load the URL in the WebView, not in an external browser.
            return true;
        }
    }
    public void silence_service_ringing_siren(){
        Intent myService = new Intent(this, Background_Service.class);
        if(Background_Service.isRinging()){
            stopService(myService);
            stopService(myService);
            stopService(myService);
            Toast.makeText(this, "Cellphone Siren Off", Toast.LENGTH_LONG).show();
            startForegroundService();
        }else{
            Toast.makeText(this, "There is no active siren!", Toast.LENGTH_LONG).show();
        }
    }
    private void is_service_running() {
        if(Background_Service.isRunning()){
            Toast.makeText(this, "Service already running.", Toast.LENGTH_SHORT).show();
        }else{
            startForegroundService();
        }

    }
    private void startForegroundService(){
        Intent serviceIntent = new Intent(this, Background_Service.class);
        serviceIntent.putExtra("inputExtra", "System is active.");
        ContextCompat.startForegroundService(this, serviceIntent);
        Toast.makeText(this, "Starting service...", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Updating...", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "GPS Device Number not configured.", Toast.LENGTH_SHORT).show();
        }


    }
    private void openNotifications(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        if(phoneNumber!=null&&!phoneNumber.isEmpty()){
            Intent intent = new Intent(this, notifications.class);
            startActivity(intent);
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

    } //Vibration alarm (no sound)
    private void arm_off() {
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        if(phoneNumber!=null&&!phoneNumber.isEmpty()){
            String message = "disarm280697";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Deactivating system", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "GPS Device Number not configured.", Toast.LENGTH_SHORT).show();
        }


    }
    private void arm_on() {
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        if(phoneNumber!=null&&!phoneNumber.isEmpty()){
            String message = "arm280697";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Activating system", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "GPS Device Number not configured.", Toast.LENGTH_SHORT).show();
        }


    }
    private void engine_on(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        if(phoneNumber!=null&&!phoneNumber.isEmpty()){
            String message = "resume280697";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Turning On A1", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "GPS Device Number not configured.", Toast.LENGTH_SHORT).show();
        }

    }
    private void engine_off(){
        String phoneNumber = mPreferences.getString(getString(R.string.GPS_DEVICE_NUMBER), "");
        if(phoneNumber!=null&&!phoneNumber.isEmpty()){
            String message = "stop280697";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Turning Off A1", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "GPS Device Number not configured.", Toast.LENGTH_SHORT).show();
        }

    }

}

