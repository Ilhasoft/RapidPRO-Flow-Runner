package in.ureport.flowrunner.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import in.ureport.flowrunner.R;

/**
 * Created by redmine on 5/30/16.
 */
public class UdoIntentService extends GcmListenerService {
    private static final String TAG = "UdoIntentService";
    public static String NotificationUDO = "notification_udo";
    private String title;
    private String type;
    private String message;
    private String packageName;
    private Uri alarmSound;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        title = data.getString("gcm.notification.title");
        message = data.getString("message");
        type = data.getString("type");
        packageName = getApplicationContext().getPackageName();
        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (type.equals("Rapidpro")) {
            showLocalNotication();
        }
    }

    public void onCreateLocalNotication(NotificationCompat.Builder mBuilder) {
        NotificationManager mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(81723469, mBuilder.build());
    }

    private void showLocalNotication() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(getApplicationInfo().icon)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),getApplicationInfo().icon))
                        .setContentTitle(title)
                        .setContentText(message)
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
