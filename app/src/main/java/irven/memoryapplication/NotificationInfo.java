package irven.memoryapplication;

import android.app.Notification;

public class NotificationInfo {
    public Notification notification;
    public int id;
    public long timeAlarm;

    NotificationInfo(Notification notification, int id, long timeAlarm) {
        this.notification = notification;
        this.id = id;
        this.timeAlarm = timeAlarm;
    }

}
