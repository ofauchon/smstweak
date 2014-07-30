package com.oflabs.smstweak.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.oflabs.smstweak.R;
import com.oflabs.smstweak.activities.SMSActivity;

/**
 * Created by IntelliJ IDEA.
 * User: olivier
 * Date: 9/9/11
 * Time: 10:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Booter extends BroadcastReceiver {

    public static final int ID_NOTIFICATION = 1;


    //Méthode qui créer la notification
    private void createNotify(Context context, String titreNotification, String texteNotification) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.icon, "sms-tweak running !", System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, SMSActivity.class), 0);
        notification.setLatestEventInfo(context, titreNotification, texteNotification, pendingIntent);
        notificationManager.notify(ID_NOTIFICATION, notification);
    }

    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences.getBoolean("autostart", false)) {
                createNotify(context,"sms-tweak running.","Click to open");
            }
        }

    }
}
