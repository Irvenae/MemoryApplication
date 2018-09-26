package irven.memoryapplication;

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

    @Override
    public String toString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return "Memory: id: "+ Integer.toString(id) + " mnemonic: " + mnemonic + " content: " +
            content + " time: " + calendar + " timingIndex: " + Integer.toString(timingIndex);
    }
}
