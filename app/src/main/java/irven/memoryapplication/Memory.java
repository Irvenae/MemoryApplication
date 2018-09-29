package irven.memoryapplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class Memory {
    int id;
    String mnemonic;
    String content;
    Long time;
    int timingIndex; // selects how long to wait next

    Memory(String mnemonic, String content) {
        this.id = -1;
        this.mnemonic = mnemonic;
        this.content = content;
        this.time = System.currentTimeMillis();
        this.timingIndex = 0;
    }

    Memory(int id, String mnemonic, String content, Long time, int timingIndex) {
        this.id = id;
        this.mnemonic = mnemonic;
        this.content = content;
        this.time = time;
        this.timingIndex = timingIndex;
    }

    public String getTime() {
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

    @Override
    public String toString() {
        String time = getTime();
        return "Memory: id: "+ Integer.toString(id) + " mnemonic: " + mnemonic + " content: " +
            content + " time: " + time + " timingIndex: " + Integer.toString(timingIndex);
    }
}
