package in.ureport.flowrunner.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
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
        String message = data.getString("message");
        String type = data.getString("type");
       if(type.equals("rapidpro")) {
           Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
           NotificationCompat.Builder mBuilder =
                   new NotificationCompat.Builder(getApplicationContext())
                           .setSmallIcon(R.drawable.img_button_play)
                           .setContentTitle(message)
                           .setContentText(getApplicationContext().getText(R.string.new_flow))
                           .setSound(alarmSound)
                           .setAutoCancel(true);
           NotificationManager mNotificationManager =
                   (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
           mNotificationManager.notify(81723469, mBuilder.build());
       }
    }
}
