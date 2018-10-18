package irven.memoryapplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class Memory {
    private
        void updateRepeatTime(){
            // we use a rate 2 learning curve, starting from 1 day
            long addTime = 24 * 3600 * 1000; // 1 day
            if (timingIndex != 0) {
                addTime = addTime * 2 * timingIndex;
            }
        repeatTime = repeatTime + addTime;
        }

        String convertTime(Long time) {
            SimpleDateFormat dateFormatHour = new SimpleDateFormat("hh");
            SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd");
            SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMM");
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(time);

            String hour = dateFormatHour.format(date.getTime()) + " h";
            String day = dateFormatDay.format(date.getTime());
            String month = dateFormatMonth.format(date.getTime());

            String strDate = day + " " + month + " ("+ hour +")";
            return strDate;
        }
    public
    int id;
    String mnemonic;
    String content;
    Long startTime; // in millies
    Long repeatTime; // in millies
    int timingIndex; // selects how long to wait next


    Memory(String mnemonic, String content) {
        this.id = -1;
        this.mnemonic = mnemonic;
        this.content = content;
        this.startTime = System.currentTimeMillis();
        this.timingIndex = 0;
        this.repeatTime = this.startTime;
        updateRepeatTime();
    }

    Memory(int id, String mnemonic, String content, Long startTime, long repeatTime, int timingIndex) {
        this.id = id;
        this.mnemonic = mnemonic;
        this.content = content;
        this.startTime = startTime;
        this.repeatTime = repeatTime;
        this.timingIndex = timingIndex;
    }

    public String getstartTime() {
        return convertTime(startTime);
    }

    public String getrepeatTime() {
        return convertTime(repeatTime);
    }

    public void onForgot() {
        timingIndex = 0;
        updateRepeatTime();
    }

    public void onRememberedDifficult() {
        if (timingIndex != 0)
            timingIndex = timingIndex - 1;
        updateRepeatTime();
    }
    public void onRememberedWell() {
        timingIndex = timingIndex + 1;
        updateRepeatTime();
    }

    @Override
    public String toString() {
        String startTime = getstartTime();
        String repeatTime = getrepeatTime();
        return "Memory: id: "+ Integer.toString(id) + " mnemonic: " + mnemonic + " content: " +
            content + " start time: " + startTime  + " repeat time: " + repeatTime + " timingIndex: " + Integer.toString(timingIndex);
    }
}
