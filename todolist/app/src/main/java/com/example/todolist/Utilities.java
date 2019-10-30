package com.example.todolist;

import android.database.Cursor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utilities {
    private static final String DATETIME_PATTERN = "dd-MM-yyyy HH:mm:ss";
    private static final String DATE_PATTERN = "dd-MM-yyyy";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static DateFormat datetimeFormat = new SimpleDateFormat(DATETIME_PATTERN, Locale.KOREAN);
    private static SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.KOREAN);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.KOREAN);

    public static Date stringToDate(String strDate) {
        try {
            return datetimeFormat.parse(strDate);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static String datetimeToString(Date date) {
        return datetimeFormat.format(date);
    }

    public static String dateToString(Date date) {
        return dateFormat.format(date);
    }

    public static String timeToString(Date date) {
        return timeFormat.format(date);
    }

    public static Note cursorToNote(Cursor cursor, int position) {
        Note note = new Note();
        cursor.move(position);

        //String content = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CONTENT));
        note.setNodeId(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_ID)));
        note.setPriority(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_PRIORITY)));

        int completed = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_COMPLETED));
        int hightlight = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_HIGHTLIGHT));

        note.setCompleted((completed == 1));
        note.setHightlight((hightlight == 1));
        note.setContent(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CONTENT)));
        note.setSubList(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_NAME_SUB_LIST)));
        note.setDeadline(stringToDate(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_DEADLINE))));
        note.setDateCreate(stringToDate(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_DATE_CREATE))));

        cursor.close();
        return note;
    }
}
