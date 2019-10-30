package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class NoteModify {
    private static NoteModify instance;
    private Context context;
    private SQLiteDatabase db;
    private DatabaseHandler dbHelper;

    private NoteModify(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHandler(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public static NoteModify getInstance(Context context) {
        if (instance == null) {
            instance = new NoteModify(context);
        }
        return instance;
    }

    void insertNote(Note note) {
        ContentValues contentValues = new ContentValues();

        Toast.makeText(context, note.getContent(), Toast.LENGTH_SHORT).show();
        contentValues.put(DatabaseHandler.KEY_CONTENT, note.getContent());
        contentValues.put(DatabaseHandler.KEY_NAME_SUB_LIST, note.getSubList());
        contentValues.put(DatabaseHandler.KEY_COMPLETED, note.isCompleted());
        contentValues.put(DatabaseHandler.KEY_PRIORITY, note.getPriority());
        contentValues.put(DatabaseHandler.KEY_HIGHTLIGHT, note.isHightlight());

        String strDateCreate = Utilities.datetimeToString(note.getDateCreate());
        contentValues.put(DatabaseHandler.KEY_DATE_CREATE, strDateCreate);
        String strDeadline = Utilities.datetimeToString(note.getDeadline());
        contentValues.put(DatabaseHandler.KEY_DEADLINE, strDeadline);

        int newRowId = (int) db.insert(DatabaseHandler.TABLE_NAME, null, contentValues);
        note.setNodeId(newRowId);
    }

    void updateNote(int id, Note note) {
        ContentValues contentValues = new ContentValues();
        String selection = DatabaseHandler.KEY_ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};

        contentValues.put(DatabaseHandler.KEY_ID, note.getNodeId());
        contentValues.put(DatabaseHandler.KEY_CONTENT, note.getContent());
        contentValues.put(DatabaseHandler.KEY_NAME_SUB_LIST, note.getSubList());
        contentValues.put(DatabaseHandler.KEY_COMPLETED, note.isCompleted());
        contentValues.put(DatabaseHandler.KEY_PRIORITY, note.getPriority());
        contentValues.put(DatabaseHandler.KEY_HIGHTLIGHT, note.isHightlight());

        String strDeadline = Utilities.datetimeToString(note.getDeadline());
        contentValues.put(DatabaseHandler.KEY_DEADLINE, strDeadline);

        String strCreate = Utilities.datetimeToString(note.getDateCreate());
        contentValues.put(DatabaseHandler.KEY_DATE_CREATE, strCreate);

        db.update(DatabaseHandler.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    void deleteNote(int id) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String selection = DatabaseHandler.KEY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        database.delete(DatabaseHandler.TABLE_NAME, selection, selectionArgs);
    }

    void deleteNoteCompleted() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String selection = DatabaseHandler.KEY_COMPLETED + " = ?";
        String[] selectionArgs = {String.valueOf(1)};

        database.delete(DatabaseHandler.TABLE_NAME, selection, selectionArgs);
    }

    Cursor getCursorInSubList(String sublist) {
        String selection = DatabaseHandler.KEY_NAME_SUB_LIST + "=?";
        String[] selectionArgs = {sublist};

        return db.query(DatabaseHandler.TABLE_NAME, null, selection, selectionArgs, null, null, null);
    }

    Note getCursorInId(int id) {
        String selection = DatabaseHandler.KEY_ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(DatabaseHandler.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Note note = new Note(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_ID)), cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CONTENT)),
                cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_NAME_SUB_LIST)), Utilities.stringToDate(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_DATE_CREATE))),
                Utilities.stringToDate(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_DEADLINE))), (cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_COMPLETED)) == 1),
                (cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_HIGHTLIGHT)) == 1), cursor.getColumnIndex(DatabaseHandler.KEY_PRIORITY));
        cursor.close();
        return note;
    }

    Cursor getCursorAllNotes() {
        return db.query(DatabaseHandler.TABLE_NAME, null, null, null, null, null, null);
    }

    Cursor getCursorAllNotesInSort(String whereClause) {
        return db.query(DatabaseHandler.TABLE_NAME, null, null, null, null, null, whereClause);
    }
}
