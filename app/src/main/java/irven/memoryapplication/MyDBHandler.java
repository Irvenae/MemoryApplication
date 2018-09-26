package irven.memoryapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.Vector;

public class MyDBHandler extends SQLiteOpenHelper {
    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "memoryDB.db";
    public static final String TABLE_NAME = "Memory";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_MNEMONIC = "Mnemonic";
    public static final String COLUMN_CONTENT = "Content";
    public static final String COLUMN_TIME = "Checkdate";
    public static final String COLUMN_TIMING_INDEX = "TimingIndex";

    //initialize the database
    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //public MyDBHandler(Context context, String Stringname, SQLiteDatabase.CursorFactory factory, int intversion) {
    //    super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    //}
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table if not exists
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID +
                "INTEGER PRIMARYKEY," + COLUMN_MNEMONIC + "TEXT" + COLUMN_CONTENT + "TEXT" +
                COLUMN_TIME + "INTEGER" + COLUMN_TIMING_INDEX + "INTEGER )";
        db.execSQL(CREATE_TABLE);
    }

    public void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}
    public Vector<Memory> loadAllMemories() {
        Vector<Memory> memories = new Vector();
        String result = "";
        String query = "Select * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String mnemonic = cursor.getString(1);
            String content = cursor.getString(2);
            Long time = cursor.getLong(3);
            int timeIndex = cursor.getInt(4);
            Memory memory = new Memory(id, mnemonic, content, time, timeIndex);
            memories.add(memory);
        }
        cursor.close();
        db.close();
        return memories;
    }

    public Vector<Memory> loadMemoriesToRepeat() {
        Vector<Memory> memories = new Vector();
        String query = "Select * FROM " + TABLE_NAME + "WHERE" + COLUMN_TIME + "<'" + String.valueOf(System.currentTimeMillis()) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String mnemonic = cursor.getString(1);
            String content = cursor.getString(2);
            Long time = cursor.getLong(3);
            int timeIndex = cursor.getInt(4);
            Memory memory = new Memory(id, mnemonic, content, time, timeIndex);
            memories.add(memory);
        }
        cursor.close();
        db.close();
        return memories;
    }

    public Memory addMemory(Memory memory) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MNEMONIC, memory.mnemonic);
        values.put(COLUMN_TIME, memory.time);
        values.put(COLUMN_TIMING_INDEX, memory.timingIndex);
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(TABLE_NAME, null, values);
        memory.id = (int) id;
        db.close();
        return memory;
    }

    public boolean deleteMemory(Memory memory) {
        boolean result = false;
        String query = "Select * FROM " + TABLE_NAME + "WHERE" + COLUMN_ID + "= '" + String.valueOf(memory.id) + "'";
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
        ContentValues args = new ContentValues();
        args.put(COLUMN_ID, memory.id);
        args.put(COLUMN_MNEMONIC, memory.mnemonic);
        args.put(COLUMN_TIME, memory.time);
        args.put(COLUMN_TIMING_INDEX, memory.timingIndex);
        return db.update(TABLE_NAME, args, COLUMN_ID + "=" + memory.id, null) > 0;
    }
}