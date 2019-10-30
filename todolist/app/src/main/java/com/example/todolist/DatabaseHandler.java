package com.example.todolist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "note";
    public static final String KEY_ID = "id";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_PRIORITY = "priority";
    public static final String KEY_HIGHTLIGHT = "hightlight";
    public static final String KEY_COMPLETED = "completed";
    public static final String KEY_DATE_CREATE = "date_create";
    public static final String KEY_DEADLINE = "deadline";
    public static final String KEY_NAME_SUB_LIST = "name_sub_list";
    private static final String DATABASE_NAME = "todolist.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CONTENT + " TEXT," + KEY_NAME_SUB_LIST + " TEXT," +
                    KEY_PRIORITY + " INT DEFAULT 0," + KEY_HIGHTLIGHT + " BIT DEFAULT 0," + KEY_COMPLETED + " BIT DEFAULT 0, " +
                    KEY_DATE_CREATE + " DATE," + KEY_DEADLINE + " DATE" + ")";
    private static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void addNote() {

    }

    public void queryDatabase(String sql) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public Cursor getDatabase(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DELETE_TABLE);
        onCreate(sqLiteDatabase);
    }
}
