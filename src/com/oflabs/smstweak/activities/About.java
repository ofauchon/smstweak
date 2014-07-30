package com.oflabs.smstweak.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import com.oflabs.smstweak.R;

/**
 * Created by IntelliJ IDEA.
 * User: olivier
 * Date: 9/11/11
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class About extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        WebView wv = (WebView) findViewById(R.id.webview);
        wv.loadUrl("file:///android_asset/help.html");
    }
}