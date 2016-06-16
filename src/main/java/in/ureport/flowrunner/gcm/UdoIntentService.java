package in.ureport.flowrunner.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import in.ureport.flowrunner.R;

/**
 * Created by redmine on 5/30/16.
 */
public class UdoIntentService extends GcmListenerService {
    private static final String TAG = "UdoIntentService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String title = data.getString("gcm.notification.title");
        String message = data.getString("message");
        String type = data.getString("type");
        String packageName = getApplicationContext().getPackageName();
        Intent launchIntent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(packageName);
        PendingIntent pContentIntent = PendingIntent.getActivity(getApplicationContext(), 873459238, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (type.equals("Rapidpro")) {
            ApplicationInfo info = getApplicationInfo();
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(getApplicationInfo().icon)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setSound(alarmSound)
                            .setAutoCancel(true);
            mBuilder.setContentIntent(pContentIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(81723469, mBuilder.build());
        }
    }
}
