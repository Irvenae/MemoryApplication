package irven.memoryapplication;

import android.content.Context;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

// creates a notification for an item at position 0.
// assumes that this is the third child from the itemview

public class NotificationHandler {
    int mCurrentNrNotifications = 0;
    int mNewNrNotifications = 0;
    Context mContext;
    public NotificationHandler(Context context){
        mContext = context;
    };

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

    public void update(BottomNavigationView navigationBottom) {
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
                View badge = LayoutInflater.from(mContext)
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
}
