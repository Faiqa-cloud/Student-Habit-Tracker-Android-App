package com.example.studenthabittrackerapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

    public class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DB_NAME    = "StudentTracker.db";
        private static final int    DB_VERSION = 1;

        // ── Tables ──────────────────────────────────────────
        private static final String TABLE_STUDENTS  = "students";
        private static final String TABLE_SCHEDULE  = "schedule";
        private static final String TABLE_HABITS    = "habits";
        private static final String TABLE_ATTENDANCE = "attendance";

        // ── Students columns ────────────────────────────────
        private static final String COL_ID   = "id";
        private static final String COL_NAME = "name";
        private static final String COL_ROLL = "roll";
        private static final String COL_DEPT = "dept";

        // ── Schedule columns ────────────────────────────────
        private static final String COL_SUBJECT = "subject";
        private static final String COL_DAY     = "day";
        private static final String COL_TIME    = "time";

        // ── Habits columns ──────────────────────────────────
        private static final String COL_HABIT_NAME = "habit_name";
        private static final String COL_IS_DONE    = "is_done";

        // ── Attendance columns ──────────────────────────────
        private static final String COL_MONTH      = "month";
        private static final String COL_DAY_NUM    = "day_num";
        private static final String COL_STATUS     = "status";

        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_STUDENTS + " (" +
                    COL_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NAME + " TEXT, " +
                    COL_ROLL + " TEXT, " +
                    COL_DEPT + " TEXT)");

            db.execSQL("CREATE TABLE " + TABLE_SCHEDULE + " (" +
                    COL_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_SUBJECT + " TEXT, " +
                    COL_DAY     + " TEXT, " +
                    COL_TIME    + " TEXT)");

            db.execSQL("CREATE TABLE " + TABLE_HABITS + " (" +
                    COL_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_HABIT_NAME + " TEXT, " +
                    COL_IS_DONE    + " INTEGER DEFAULT 0)");

            db.execSQL("CREATE TABLE " + TABLE_ATTENDANCE + " (" +
                    COL_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_MONTH   + " TEXT, " +
                    COL_DAY_NUM + " INTEGER, " +
                    COL_STATUS  + " INTEGER DEFAULT 0)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
            onCreate(db);
        }

        // ════════════════════════════════════════════════════
        // STUDENTS
        // ════════════════════════════════════════════════════

        public void saveStudent(String name, String roll, String dept) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL_NAME, name);
            cv.put(COL_ROLL, roll);
            cv.put(COL_DEPT, dept);
            db.insert(TABLE_STUDENTS, null, cv);
            db.close();
        }

        public ArrayList<String[]> getAllStudents() {
            ArrayList<String[]> list = new ArrayList<>();
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_STUDENTS, null);
            if (cursor.moveToFirst()) {
                do {
                    list.add(new String[]{
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_DEPT))
                    });
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return list;
        }

        // ════════════════════════════════════════════════════
        // SCHEDULE
        // ════════════════════════════════════════════════════

        public void saveSchedule(String subject, String day, String time) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL_SUBJECT, subject);
            cv.put(COL_DAY,     day);
            cv.put(COL_TIME,    time);
            db.insert(TABLE_SCHEDULE, null, cv);
            db.close();
        }

        public ArrayList<String[]> getAllSchedule() {
            ArrayList<String[]> list = new ArrayList<>();
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_SCHEDULE, null);
            if (cursor.moveToFirst()) {
                do {
                    list.add(new String[]{
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_DAY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME))
                    });
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return list;
        }

        // ════════════════════════════════════════════════════
        // HABITS
        // ════════════════════════════════════════════════════

        public void saveHabit(String habitName) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL_HABIT_NAME, habitName);
            cv.put(COL_IS_DONE,    0);
            db.insert(TABLE_HABITS, null, cv);
            db.close();
        }

        public void updateHabit(int id, boolean isDone) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL_IS_DONE, isDone ? 1 : 0);
            db.update(TABLE_HABITS, cv,
                    COL_ID + "=?", new String[]{String.valueOf(id)});
            db.close();
        }

        public ArrayList<String[]> getAllHabits() {
            ArrayList<String[]> list = new ArrayList<>();
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_HABITS, null);
            if (cursor.moveToFirst()) {
                do {
                    list.add(new String[]{
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_HABIT_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_IS_DONE))
                    });
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return list;
        }

        // ════════════════════════════════════════════════════
        // ATTENDANCE
        // ════════════════════════════════════════════════════

        public void saveAttendance(String month, int dayNum, int status) {
            SQLiteDatabase db = getWritableDatabase();

            // Pehle check karo exist karta hai ya nahi
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_ATTENDANCE +
                            " WHERE " + COL_MONTH + "=? AND " + COL_DAY_NUM + "=?",
                    new String[]{month, String.valueOf(dayNum)});

            ContentValues cv = new ContentValues();
            cv.put(COL_MONTH,   month);
            cv.put(COL_DAY_NUM, dayNum);
            cv.put(COL_STATUS,  status);

            if (cursor.getCount() > 0) {
                // Update karo
                db.update(TABLE_ATTENDANCE, cv,
                        COL_MONTH + "=? AND " + COL_DAY_NUM + "=?",
                        new String[]{month, String.valueOf(dayNum)});
            } else {
                // Naya insert karo
                db.insert(TABLE_ATTENDANCE, null, cv);
            }
            cursor.close();
            db.close();
        }

        public int[] getAttendance(String month) {
            int[] statuses = new int[30];
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_ATTENDANCE +
                            " WHERE " + COL_MONTH + "=?",
                    new String[]{month});
            if (cursor.moveToFirst()) {
                do {
                    int dayNum = cursor.getInt(
                            cursor.getColumnIndexOrThrow(COL_DAY_NUM));
                    int status = cursor.getInt(
                            cursor.getColumnIndexOrThrow(COL_STATUS));
                    if (dayNum >= 1 && dayNum <= 30) {
                        statuses[dayNum - 1] = status;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return statuses;
        }

        // Notes update karo
        public void updateStudent(int id, String name, String roll, String dept) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL_NAME, name);
            cv.put(COL_ROLL, roll);
            cv.put(COL_DEPT, dept);
            db.update(TABLE_STUDENTS, cv,
                    COL_ID + "=?", new String[]{String.valueOf(id)});
            db.close();
        }

        // Note delete karo
        public void deleteStudent(int id) {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLE_STUDENTS, COL_ID + "=?",
                    new String[]{String.valueOf(id)});
            db.close();
        }

        // Sab students ID ke saath lo
        public ArrayList<String[]> getAllStudentsWithId() {
            ArrayList<String[]> list = new ArrayList<>();
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STUDENTS, null);
            if (cursor.moveToFirst()) {
                do {
                    list.add(new String[]{
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_DEPT))
                    });
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return list;
        }
        public void updateSchedule(int id, String subject, String day, String time) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL_SUBJECT, subject);
            cv.put(COL_DAY, day);
            cv.put(COL_TIME, time);
            db.update(TABLE_SCHEDULE, cv,
                    COL_ID + "=?", new String[]{String.valueOf(id)});
            db.close();
        }

        public void deleteSchedule(int id) {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLE_SCHEDULE, COL_ID + "=?",
                    new String[]{String.valueOf(id)});
            db.close();
        }

        public ArrayList<String[]> getAllScheduleWithId() {
            ArrayList<String[]> list = new ArrayList<>();
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCHEDULE, null);
            if (cursor.moveToFirst()) {
                do {
                    list.add(new String[]{
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_DAY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME))
                    });
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return list;
        }
        public void deleteHabit(int id) {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLE_HABITS, COL_ID + "=?",
                    new String[]{String.valueOf(id)});
            db.close();
        }

        public void updateHabitName(int id, String newName) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL_HABIT_NAME, newName);
            db.update(TABLE_HABITS, cv,
                    COL_ID + "=?", new String[]{String.valueOf(id)});
            db.close();
        }
    }


