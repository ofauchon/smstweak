/*
Copyright (C) 2013, 2014 Olivier Fauchon
This file is part of SMS-TWEAK Android Aplication.

SMS-TWEAK is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SMS-TWEAK is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
*/

package com.oflabs.smstweak.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import com.oflabs.smstweak.R;
import com.oflabs.smstweak.receivers.SMSReceiver;
import com.oflabs.smstweak.smsdb.ListCursorAdapter;
import com.oflabs.smstweak.smsdb.SmsDB;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SMSActivity extends Activity implements TextToSpeech.OnInitListener {
    //public static final String PREFS_NAME = "SMSTweakPrefsFile";
    public static final String PREFS_RECEIVED_SMS = "received_sms";
    public static final String PREFS_FILTERED_SMS = "filtered_sms";
    private static final String TAG = "SMSTweak";
    private static final int MY_DATA_CHECK_CODE = 433334;

    private static final int MAX_SMS_IN_LIST = 100;
    private static final int CODE_RETOUR = 1;

    // For auto-update UI
    private Timer autoUpdate;

    // SB Stuff
    private Cursor lvCursor;
    private SmsDB mydb;

    // TextToSpeech
    private static TextToSpeech mTts;


    public static void speak(String message) {
        mTts.speak(message, TextToSpeech.QUEUE_ADD, null);
    }


    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //Toast.makeText(SMSActivity.this,
            //        "Text-To-Speech engine is initialized", Toast.LENGTH_LONG).show();
            mTts.setLanguage(Locale.ENGLISH);
        } else if (status == TextToSpeech.ERROR) {
            //Toast.makeText(SMSActivity.this,
            //        "Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);    //To change body of overridden methods use File | Settings | File Templates.
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                mTts = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuprincipal, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemOptions) {
            startActivityForResult(new Intent(this, MesPrefActivity.class), CODE_RETOUR);
        }
        if (item.getItemId() == R.id.itemAbout) {
            startActivity(new Intent(this, About.class));
        }
        if (item.getItemId() == R.id.itemClearHistory) {
            Intent checkIntent = new Intent();
            //checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            //startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
            mydb.deleteAllSms();
        }
        //       if (item.getItemId() == R.id.itemTest1) {
        //        }


        return super.onOptionsItemSelected(item);
    }

    public void updateUI() {
        // Update UI with prefs
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        int r_sms = myPrefs.getInt(PREFS_RECEIVED_SMS, 0);
        int m_sms = myPrefs.getInt(PREFS_FILTERED_SMS, 0);
        String mess = "Total / Matching SMS : " + Integer.toString(r_sms) + " / " + Integer.toString(m_sms);
        TextView tv;
        tv = (TextView) findViewById(R.id.widget21);
        tv.setText(mess);
        // Update listview with latest DB values.
        lvCursor.requery();

    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onResume() {
        super.onResume();

        autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        updateUI();
                    }
                });
            }
        }, 0, 5000); // updates each 40 secs


    }


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.main);

        // Suppression de la notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(SMSReceiver.ID_NOTIFICATION);

        // Init database
        mydb = new SmsDB(this);
        mydb.open();

        // initialize cursor
        lvCursor = mydb.getLastSms(MAX_SMS_IN_LIST);
        ListView lv = (ListView) findViewById(R.id.lvListe);
        ListCursorAdapter lca = new ListCursorAdapter(this, lvCursor);
        lv.setAdapter(lca);
        lca.notifyDataSetChanged();

        // Update UI
        this.updateUI();

        /*
        PackageInfo pinfo;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return;
        }
        */
        // TTS
        mTts = new TextToSpeech(this, this);
        mTts.setLanguage(Locale.US);


    }

    @Override
    public void onPause() {
        super.onPause();
        autoUpdate.cancel();
    }


}
