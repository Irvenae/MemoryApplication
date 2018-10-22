package irven.memoryapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import java.util.List;

// creates a notification for an item at position 0.
// assumes that this is the third child from the itemview

public class NotificationHandler {
    // Singleton instance
    private static NotificationHandler sInstance = null;

    private int mCurrentNrNotifications = 0;
    private int mNewNrNotifications = 0;
    private String CHANNEL_ID;
    private int notificationId = 0;

    public NotificationHandler(Context context, Intent startIntent){
        CHANNEL_ID = context.getResources().getString(R.string.channel_name);


        createNotificationChannel(context);

        // Setup singleton instance
        sInstance = this;
    }

    // Getter to access Singleton instance
    public static NotificationHandler getInstance() {
        return sInstance ;
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getResources().getString(R.string.channel_name);
            String description = context.getResources().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void Remove(BottomNavigationView navigationBottom) {
        mCurrentNrNotifications = mNewNrNotifications;
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) navigationBottom.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        if (itemView.getChildCount() == 3) {
            itemView.removeViewAt(itemView.getChildCount() - 1);
        }
    }

    public void update(Context context, BottomNavigationView navigationBottom) {
        //get next nr notifications
        MyDBHandler memoryDB = MyDBHandler.getInstance();
        List<Memory> memories = memoryDB.loadMemoriesToRepeat();
        mNewNrNotifications = memories.size();
        int diff = mNewNrNotifications - mCurrentNrNotifications;
        if (diff > 0) {
            BottomNavigationMenuView bottomNavigationMenuView =
                    (BottomNavigationMenuView) navigationBottom.getChildAt(0);
            BottomNavigationItemView itemView = (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(0);
            String textNumber;
            if (diff < 10) {
                textNumber = Integer.toString(diff);
            } else {
                textNumber = "9+";
            }
            if (itemView.getChildCount() != 3) {
                View badge = LayoutInflater.from(context)
                        .inflate(R.layout.notification_badge, bottomNavigationMenuView, false);
                TextView nrNotificationsTextview = (TextView) badge.findViewById(R.id.notifications_badge);
                nrNotificationsTextview.setText(textNumber);
                itemView.addView(badge);
            } else {
                View badge = itemView.getChildAt(2);
                TextView nrNotificationsTextview = (TextView) badge.findViewById(R.id.notifications_badge);
                nrNotificationsTextview.setText(textNumber);
            }
        } else {
            // current larger or equal to new !
            mCurrentNrNotifications = mNewNrNotifications;
        }
    }

    NotificationInfo makeNotification(Context context, Intent clickIntent) {
        Memory memory = MyDBHandler.getInstance().getSoonestNewMemoryToRepeat();
        if (memory.id == -1) {
            return new NotificationInfo(null, -1, 0);
        }
        String textTitle = "new data to repeat";
        String textContent = memory.mnemonic;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                //.setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Create pending intent, mention the Activity which needs to be
        //triggered when user clicks on notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        // notificationId is a unique int for each notification that you must define
        NotificationInfo notificationInfo = new NotificationInfo(mBuilder.build(), notificationId, memory.repeatTime);
        notificationId = notificationId + 1;
        return notificationInfo;
    }
}
