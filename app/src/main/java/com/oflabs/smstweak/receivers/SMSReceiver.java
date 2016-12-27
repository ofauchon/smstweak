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

package com.oflabs.smstweak.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import com.oflabs.smstweak.ParserException;
import com.oflabs.smstweak.R;
import com.oflabs.smstweak.RulesParser;
import com.oflabs.smstweak.Sms;
import com.oflabs.smstweak.activities.SMSActivity;
import com.oflabs.smstweak.smsdb.SmsDB;

import static android.R.id.icon;

/**
 * Created by IntelliJ IDEA.
 * User: olivier
 * Date: 7/28/11
 * Time: 1:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class SMSReceiver extends BroadcastReceiver  {

    public static final int ID_NOTIFICATION = 1;
    public static final String MYTAG = "SMSReceiver";

    private RulesParser rulesparser = new RulesParser();



    //Méthode qui créer la notification
    private void createNotify(Context context, String titreNotification, String texteNotification, boolean vibrate) {
/*
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, SMSActivity.class), 0);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      //  Notification notification = new Notification(R.drawable.icon, "sms-tweeak: New message!", System.currentTimeMillis());
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);

        nBuilder.
        (pendingIntent)
                .setSmallIcon(icon).setWhen( System.currentTimeMillis() )
                .setContentTitle("sms-tweak: New message!")
                .setContentText("message").build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        //Le PendingIntent c'est ce qui va nous permettre d'atteindre notre deuxième Activity
        //ActivityNotification sera donc le nom de notre seconde Activity
        //On configure notre notification avec tous les paramètres que l'on vient de créer
        notification.set
        notification.setLatestEventInfo(context, titreNotification, texteNotification, pendingIntent);
        //On ajoute un style de vibration à notre notification
        //L'utilisateur est donc également averti par les vibrations de son téléphone
        //Ici les chiffres correspondent à 0sec de pause, 0.2sec de vibration, 0.1sec de pause, 0.2sec de vibration, 0.1sec de pause, 0.2sec de vibration
        //Vous pouvez bien entendu modifier ces valeurs à votre convenance
        if (vibrate) notification.vibrate = new long[]{0, 200, 100, 200, 100, 200};
        //Enfin on ajoute notre notification et son ID à notre gestionnaire de notification
        notificationManager.notify(ID_NOTIFICATION, notification);
*/
    }

    // -- Decode SMS --
    SmsMessage[] getSmsFromBundle(Bundle bundle) {
        SmsMessage[] msgs = null;
        if (bundle != null) {
            //---get the Sms ---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
        }
        return msgs;
    }


    // --- ACTION PASS SMS TO
    public void passSms(Context context, SmsMessage msgs) {
        ContentValues values = new ContentValues();
        values.put("address", msgs.getOriginatingAddress());
        values.put("date", Long.toString(msgs.getTimestampMillis()));
        values.put("read", 1);
        values.put("status", -1);
        values.put("type", 2);
        values.put("body", msgs.getMessageBody());
        context.getContentResolver().insert(Uri.parse("content://sms"), values);

    }

    // --- ACTION VIBRATE
    private void actionVibrate(Context context, long millis){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(millis
        );
    }

    // --- ACTION PLAY SOUND
    public void actionPlay(Context context,  int resId) {
        //--- play sound
        MediaPlayer player;
        player = MediaPlayer.create(context, resId);
        player.setLooping(false); // Set looping
        player.start();
    }

    // --- ACTION POPUP
    public void actionPopup(Context context, SmsMessage msg) {
        Toast.makeText(context, msg.getMessageBody(), Toast.LENGTH_SHORT).show();
    }

    /*
     * SMS Processing : each incoming SMS will cross this method
     */
    private void processSMS(Context pContext, SmsMessage pCurSms, String pCurRule, String pCurAction) throws ParserException
    {
        // Load preferences
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(pContext);
        SharedPreferences.Editor myPrefsEditor = myPrefs.edit();

        //Prepare Parser
        this.rulesparser.setCallerid(pCurSms.getOriginatingAddress());
        this.rulesparser.setMessagebody(pCurSms.getMessageBody());

        if (rulesparser.evaluate(pCurRule))
        {
            Log.d(MYTAG, "processSMS: From " +pCurSms.getDisplayOriginatingAddress() + " " +
                    pCurSms.getMessageBody().substring(0,20)+"... match"   );

            // Increase counter
            myPrefsEditor.putInt(SMSActivity.PREFS_FILTERED_SMS,
                    myPrefs.getInt(SMSActivity.PREFS_FILTERED_SMS, 0) + 1).commit();
            // We can abort broadcast chain
            this.abortBroadcast();
            // We can create notification
            if (myPrefs.getBoolean("statusnotify", false))
            {
                this.createNotify(pContext, "sms-tweak: New incoming Sms",
                    pCurSms.getOriginatingAddress() + ":" + pCurSms.getMessageBody(), true);
            }

            // Do something
            if (pCurAction.contains("popup")) actionPopup(pContext, pCurSms);
            if (pCurAction.contains("play")) actionPlay(pContext, R.raw.sirenhilowithrumbler);
            if (pCurAction.contains("vibrate")) actionVibrate(pContext, 1000);
            if (pCurAction.contains("pass")) passSms(pContext, pCurSms);
            //mTts.speak("Bien, me voila maintenant prêt à manger une bonne tarte au pommes et boire un bon café", TextToSpeech.QUEUE_ADD, null);
            if (pCurAction.contains("voice")) SMSActivity.speak(pCurSms.getMessageBody());


        }//match
        
    }


    // -- Process incoming SMS --
    public void onReceive(Context context, Intent intent) {

        // Load preferences
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        //SharedPreferences.Editor myPrefsEditor = myPrefs.edit();
//        confRule01 = myPrefs.getString("rule01", "");
//        confAction01 = myPrefs.getString("action01", "");
//        confIsEnabled01 = myPrefs.getBoolean("isenabled01", false);

        //---get the Sms message passed in---
        Bundle bundle = intent.getExtras();

        if (bundle != null) 
        {
            SmsMessage msgs[] = getSmsFromBundle(bundle);


            for (SmsMessage msg : msgs) {

                // Add SMS to DB
                SmsDB mydb = new SmsDB(context);
                mydb.open();
                Sms s = new Sms(msg);
                mydb.insertSms(s);
                mydb.close();

                if (myPrefs.getBoolean("isenabled", false)) {
                    Log.d(MYTAG, "onReceive: Filtering enabled, processing sms");

                    int tRuleIdx = 1;
                    boolean tRuleExists;
                    boolean tRuleEnabled;
                    String tRuleStr, tRuleAction;

                    do {
                        tRuleExists = myPrefs.contains("isenabled" + String.format("%02d", tRuleIdx));
                        tRuleEnabled = myPrefs.getBoolean("isenabled" + String.format("%02d", tRuleIdx), false);
                        tRuleStr = myPrefs.getString("rule" + String.format("%02d", tRuleIdx), "");
                        tRuleAction = myPrefs.getString("action" + String.format("%02d", tRuleIdx), "");
                        if (tRuleExists && tRuleEnabled && tRuleStr.length() > 0) {
                            Log.d(MYTAG, "onReceive: " + "rule" + String.format("%02d", tRuleIdx) + " = " + tRuleStr + " and enabled, processing message");
                            try {
                                processSMS(context,  msg, tRuleStr, tRuleAction);
                            } catch (ParserException e) {
                                e.printStackTrace();
                            }
                        }
                        tRuleIdx++;

                    }
                    while (tRuleExists);

                }//confEnabled


            }//msg loop


        } // bundle



    } //onReceive




} // class


