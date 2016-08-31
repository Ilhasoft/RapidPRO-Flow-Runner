package br.com.ilhasoft.flowrunner.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by redmine on 5/30/16.
 */
public class UdoIntentService extends GcmListenerService {

    private static final String TAG = "UdoIntentService";
    private static final String EXTRA_TYPE = "Rapidpro";

    public static String NotificationUDO = "notification_udo";

    private String title;
    private String message;
    private String packageName;

    private Uri alarmSound;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        try {
            String type = data.getString("type");
            if (!TextUtils.isEmpty(type) && type.equals(EXTRA_TYPE)) {
                title = data.getString("gcm.notification.title");
                message = handleNotificationMessage(data.getString("message"));
                packageName = getApplicationContext().getPackageName();
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                showLocalNotification();
            }
        } catch (Exception exception) {
            Log.e(TAG, "onMessageReceived: ", exception);
        }
    }

    public String handleNotificationMessage(String message) {
        return message;
    }

    public void onCreateLocalNotication(NotificationCompat.Builder mBuilder) {
        NotificationManager mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(81723469, mBuilder.build());
    }

    private void showLocalNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(getApplicationInfo().icon)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setSound(alarmSound)
                        .setAutoCancel(true);
        mBuilder.setContentIntent(createLaunchPendingIntent());
        onCreateLocalNotication(mBuilder);
    }

    private PendingIntent createLaunchPendingIntent() {
        Intent launchIntent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(packageName)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(NotificationUDO, true);
        PendingIntent pContentIntent = PendingIntent.getActivity(getApplicationContext(), 873459238, launchIntent, PendingIntent.FLAG_ONE_SHOT);
        return pContentIntent;
    }
}
