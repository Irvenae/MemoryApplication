package irven.memoryapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper {
    // Singleton instance
    private static MyDBHandler sInstance = null;
    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "memoryDB.db";
    private static final long assumeLearned = (long) 3600 * 1000 * 24 * 30 * 6; // half a year

    public static final String TABLE_NAME = "Memory";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_MNEMONIC = "Mnemonic";
    public static final String COLUMN_CONTENT = "Content";
    public static final String COLUMN_STARTTIME = "startDate";
    public static final String COLUMN_REPEATTIME = "CheckDate";
    public static final String COLUMN_TIMING_INDEX = "TimingIndex";


    //initialize the database
    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // Setup singleton instance
        sInstance = this;
    }

    // Getter to access Singleton instance
    public static MyDBHandler getInstance() {
        return sInstance ;
    }
    //public MyDBHandler(Context context, String Stringname, SQLiteDatabase.CursorFactory factory, int intversion) {
    //    super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    //}
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table if not exists
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_MNEMONIC + " TEXT," +
                COLUMN_CONTENT + " TEXT," +
                COLUMN_STARTTIME + " INTEGER," +
                COLUMN_REPEATTIME + " INTEGER," +
                COLUMN_TIMING_INDEX + " INTEGER );";
        db.execSQL(CREATE_TABLE);
    }

    public void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    private Memory getMemoryFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        String mnemonic = cursor.getString(cursor.getColumnIndex(COLUMN_MNEMONIC));
        String content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));
        Long startTime = cursor.getLong(cursor.getColumnIndex(COLUMN_STARTTIME));
        Long repeatTime = cursor.getLong(cursor.getColumnIndex(COLUMN_REPEATTIME));
        int timeIndex = cursor.getInt(cursor.getColumnIndex(COLUMN_TIMING_INDEX));
        Memory memory = new Memory(id, mnemonic, content, startTime, repeatTime, timeIndex);
        return memory;
    }

    public List<Memory> loadAllMemories() {
        List<Memory> memories = new ArrayList<>();
        String result = "";
        String query = "Select * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Memory memory = getMemoryFromCursor(cursor);
            memories.add(memory);
        }
        cursor.close();
        db.close();
        return memories;
    }

    public List<Memory> loadMemoriesToRepeat() {
        List<Memory> memories = new ArrayList<>();
        String query = "Select * FROM " + TABLE_NAME + " WHERE "
                + COLUMN_REPEATTIME + " < '" + String.valueOf(System.currentTimeMillis()) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Memory memory = getMemoryFromCursor(cursor);
            memories.add(memory);
        }
        cursor.close();
        db.close();
        return memories;
    }

    public List<Memory> loadLearningMemories() {
        List<Memory> memories = new ArrayList<>();
        String query = "Select * FROM " + TABLE_NAME + " WHERE "
                + COLUMN_REPEATTIME + " < '" + String.valueOf(System.currentTimeMillis() + assumeLearned) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Memory memory = getMemoryFromCursor(cursor);
            memories.add(memory);
        }
        cursor.close();
        db.close();
        return memories;
    }

    public List<Memory> loadLearnedMemories() {
        List<Memory> memories = new ArrayList<>();
        String query = "Select * FROM " + TABLE_NAME + " WHERE "
                + COLUMN_REPEATTIME + " > '" + String.valueOf(System.currentTimeMillis() + assumeLearned) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Memory memory = getMemoryFromCursor(cursor);
            memories.add(memory);
        }
        cursor.close();
        db.close();
        return memories;
    }

    // Id is NOT added
    private ContentValues memoryToContentValues(Memory memory) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MNEMONIC, memory.mnemonic);
        values.put(COLUMN_CONTENT, memory.content);
        values.put(COLUMN_STARTTIME, memory.startTime);
        values.put(COLUMN_REPEATTIME, memory.repeatTime);
        values.put(COLUMN_TIMING_INDEX, memory.timingIndex);
        return values;
    }

    public Memory addMemory(Memory memory) {
        ContentValues args = memoryToContentValues(memory);
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(TABLE_NAME, null, args);
        memory.id = (int) id;
        db.close();
        return memory;
    }

    public boolean deleteMemory(Memory memory) {
        boolean result = false;
        String query = "Select * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_ID + " = '" + String.valueOf(memory.id) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            db.delete(TABLE_NAME, COLUMN_ID + "=" + String.valueOf(memory.id), null);
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public boolean updateMemory(Memory memory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = memoryToContentValues(memory);
        args.put(COLUMN_ID, memory.id);
        return db.update(TABLE_NAME, args, COLUMN_ID + "=" + memory.id, null) > 0;
    }
}