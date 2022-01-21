package com.example.rubiksgps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class pagos extends AppCompatActivity {
    WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos);

        browser = (WebView) findViewById(R.id.webview);
        browser.loadUrl("https://rubiksgpsmexico.wixsite.com/home");
        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        browser.setInitialScale(1);
        browser.getSettings().setUseWideViewPort(true);
        browser.getSettings().setLoadWithOverviewMode(true);
        WebView browser = (WebView) findViewById(R.id.webview);
        browser.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("whatsapp")|(url.contains("facebook"))) {

                    try {
                        view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;
                        // Stay within this webview and load url
          /*  view.loadUrl(url);
            return true;*/
                    } catch (android.content.ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "Application not installed",
                                Toast.LENGTH_LONG).show();
                    }
                    // Stay within this webview and load url
            view.loadUrl(url);
            return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

    }
}
