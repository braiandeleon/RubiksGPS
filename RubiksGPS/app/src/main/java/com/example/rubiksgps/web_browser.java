package com.example.rubiksgps;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.os.*;
import android.app.ProgressDialog;
import android.os.CountDownTimer;

import com.example.rubiksgps.R;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class web_browser extends AppCompatActivity {
    WebView browser;
    Button button2;
    ProgressDialog progressDialog;
    public static web_browser activityB;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        waitforpagetoload();
        setContentView(R.layout.activity_web_browser);
        activityB = this;
        //This is the screen size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            display.getRealSize(size);
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }
        final int width = size.x;
        final int height = size.y;
        final int alturay = (width/7);

        browser = (WebView) findViewById(R.id.webview);
        browser.loadUrl("https://www.gpswox.com/en/sign-in");
        button2 = (Button) findViewById(R.id.button2);

        //Activar javascript
        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        browser.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (browser.getUrl().equals("https://www.gpswox.com/en/sign-in")){
                    //Toast.makeText(getApplicationContext(), "Estas en la pagina correcta", Toast.LENGTH_SHORT).show();
                }
                else{
                    return false;

                }
                return (event.getAction() == MotionEvent.ACTION_MOVE);

            }


        });

        browser.setWebViewClient(new MyWebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            //Al cargar la pagina...
            public void onPageFinished(WebView view, String url){
                //String javaScript ="javascript:(function() {alert();})()";
                //browser.loadUrl(javaScript);
                String DireccionActual = browser.getUrl();


                if(browser.getUrl().equals("https://www.gpswox.com/en/sign-in")){
                    //Inicio de sesion automatico
                    super.onPageFinished(view, url);


                    String getNewUserConfig = mPreferences.getString(getString(R.string.NEW_GPSWOXCONFIG), "");

                    //if(getNewUserConfig!=null&&!getNewUserConfig.isEmpty()){
                       // String ConfigSaved[] = getNewUserConfig.split("\n");
                       // String getUsername = ConfigSaved[1];
                        //String getPassword = ConfigSaved[2];
                        final String username = "braiandeleon+02@gmail.com";
                        final String password = "yQOXY6";
                        final String server = "eu";

                        final String xs = "javascript:" +
                                "document.getElementById('sign-in-form-password').value = '" + password + "';"  +
                                "document.getElementById('sign-in-form-email').value = '" + username + "';";
                        String js = "javascript:(function(){"+
                                "l=document.getElementsByClassName('sign-in-form-submit button button-color-green is-float-right')[0];"+
                                "l.click();"+
                                "})()";
                        browser.evaluateJavascript(xs, s -> {
                            String result = s;
                        });
                        browser.evaluateJavascript(js, s -> {
                            String result = s;
                        });
                       // view.loadUrl(xs);
                        Toast.makeText(getApplicationContext(), "Conexion establecida con el servidor", Toast.LENGTH_SHORT).show();
                  //  }


                }
                if (browser.getUrl().equals("https://europe.gpswox.com/objects")){
                    //Toast.makeText(getApplicationContext(), "Estas en la pagina correcta", Toast.LENGTH_SHORT).show();

                    Timer buttonTimer = new Timer();
                    buttonTimer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, alturay);
                                    button2.setLayoutParams(lp);
                                    //button2.setBackground(null);
                                    button2.setBackgroundColor(Color.parseColor("#1a99bc"));
                                    //button2.setClickable(false);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }, 1000);


                }

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

            @Override
            public boolean shouldOverrideUrlLoading(WebView webview, WebResourceRequest request) {

                String hereim = request.getUrl().toString();
                //Toast.makeText(getApplicationContext(), hereim, Toast.LENGTH_SHORT).show(); //Solo para saber donde estamos
                if((hereim.contains("login")||hereim.contains("europe")||hereim.contains("gpswox")||hereim.contains("layer"))){
                    webview.loadUrl(request.getUrl().toString());

                    return false;
                }else{
                    Toast.makeText(getApplicationContext(), "No disponible por el momento", Toast.LENGTH_SHORT).show();
                    // This is my web site, so do not override; let my WebView load the page
                    return true;
                }


/*                // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(request.getUrl().toString()));
                startActivity(intent);
                return true;*/






            }


        });



    }

    public static web_browser getInstance(){

        return   activityB;
    }
    public interface ScreenInterface {
        float getWidth();
        float getHeight();
    }
    public void waitforpagetoload(){
        super.onStart();
        progressDialog = ProgressDialog.show(this, null,"Conectando con GPS...", true);

    }

    public class Screen implements ScreenInterface {
        private Activity activity;

        public Screen(Activity activity) {
            this.activity = activity;
        }

        private DisplayMetrics getScreenDimension(Activity activity) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics;
        }

        private float getScreenDensity(Activity activity) {
            return activity.getResources().getDisplayMetrics().density;
        }

        @Override
        public float getWidth() {
            DisplayMetrics displayMetrics = getScreenDimension(activity);
            return displayMetrics.widthPixels / getScreenDensity(activity);
        }

        @Override
        public float getHeight() {
            DisplayMetrics displayMetrics = getScreenDimension(activity);
            return displayMetrics.heightPixels / getScreenDensity(activity);
        }

    }

    public class MyWebViewClient extends WebViewClient {
        List<String> whiteHosts = Arrays.asList("http://europe.gpswox.com/objects",  "https://www.gpswox.com/en/sign-in");

        @Override
        public boolean shouldOverrideUrlLoading
                (WebView view, String url) {

            String host = Uri.parse(url).getHost();
            if(whiteHosts.contains(host)) {
                return false;
            }

            // here you can check the url
            // (whitelist / blacklist)
            return false;
            // will NOT load the link
            // use "return false;" to allow it to load
        }
    }

}


