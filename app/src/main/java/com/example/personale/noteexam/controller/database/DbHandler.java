package com.example.personale.noteexam.controller.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.personale.noteexam.model.Note;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by personale on 13/03/2017.
 */

public class DbHandler extends SQLiteOpenHelper {


    public DbHandler(Context context) {
        super(context, DbField.DBNAME, null, DbField.DBVER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + DbField.TABLE_NOTE + " ("
                + DbField.ID + " INTEGER PRIMARY KEY, "
                + DbField.COLOR + " INTEGER, "
                + DbField.SPECIAL + " INTEGER, "
                + DbField.TITLE + " TEXT, "
                + DbField.DESCRIPTION + " TEXT, "
                + DbField.CREATIONDATE + " INTEGER, "
                + DbField.LASTEDITDATE + " INTEGER, "
                + DbField.EXPIRATIONDATE + " INTEGER); ";

        System.out.println(CREATE_TABLE);

        db.execSQL(CREATE_TABLE);
    }

    public int insert(Note n){
        ContentValues values = new ContentValues();
        values.put(DbField.COLOR, n.getColor());
        values.put(DbField.TITLE, n.getTitle());
        values.put(DbField.SPECIAL, setSpecial(n.isSpecialNote()));
        values.put(DbField.DESCRIPTION, n.getDescription());
        values.put(DbField.CREATIONDATE, n.getCreationDate().getTime());
        values.put(DbField.LASTEDITDATE, n.getLastEditDate().getTime());
        values.put(DbField.EXPIRATIONDATE, n.getExpirationDate().getTime());

        SQLiteDatabase db = this.getWritableDatabase();
        int res = (int)db.insert(DbField.TABLE_NOTE, null, values);
        n.setId(res);

        return res;
    }

    public int edit(Note n){
        String whereClause = DbField.ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(n.getId())};

        ContentValues values = new ContentValues();
        values.put(DbField.TITLE, n.getTitle());
        values.put(DbField.DESCRIPTION, n.getDescription());
        values.put(DbField.COLOR, n.getColor());
        values.put(DbField.SPECIAL, setSpecial(n.isSpecialNote()));
        values.put(DbField.CREATIONDATE, n.getCreationDate().getTime());
        values.put(DbField.LASTEDITDATE, n.getLastEditDate().getTime());
        values.put(DbField.EXPIRATIONDATE, n.getExpirationDate().getTime());

        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.update(DbField.TABLE_NOTE, values, whereClause, whereArgs);
        db.close();

        return res;
    }

    public int delete(int id){
        String whereClause = DbField.ID + " = ?";
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete(DbField.TABLE_NOTE, whereClause, new String[]{String.valueOf(id)});
        db.close();

        return res;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DbField.TABLE_NOTE);
        onCreate(db);
    }

    public ArrayList<Note> getAllNotes() {
        ArrayList<Note> notes = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + DbField.TABLE_NOTE, null);

        if(c.moveToFirst()){
            do{
                notes.add(new Note.BuilderNote()
                        .set_title(c.getString(c.getColumnIndex(DbField.TITLE)))
                        .set_specialNote(setSpecial(Integer.parseInt(c.getString(c.getColumnIndex(DbField.SPECIAL)))))
                        .set_description(c.getString(c.getColumnIndex(DbField.DESCRIPTION)))
                        .set_color(Integer.parseInt(c.getString(c.getColumnIndex(DbField.COLOR))))
                        .set_creationDate(new Date(Long.parseLong(c.getString(c.getColumnIndex(DbField.CREATIONDATE)))))
                        .set_expirationDate(new Date(Long.parseLong(c.getString(c.getColumnIndex(DbField.EXPIRATIONDATE)))))
                        .set_lastEditDate(new Date(Long.parseLong(c.getString(c.getColumnIndex(DbField.LASTEDITDATE)))))
                        .set_id(Integer.parseInt(c.getString(c.getColumnIndex(DbField.ID))))
                        .build()
                );
            }while(c.moveToNext());
        }

        db.close();

        return notes;
    }

    public ArrayList<Note> getAllNotesByText(String text) {
        ArrayList<Note> notes = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + DbField.TABLE_NOTE + " WHERE " + DbField.TITLE + " LIKE '%" + checkEscape(text) + "%'", null);
        System.out.println(text);

        if(c.moveToFirst()){
            do{
                notes.add(new Note.BuilderNote()
                        .set_title(c.getString(c.getColumnIndex(DbField.TITLE)))
                        .set_specialNote(setSpecial(Integer.parseInt(c.getString(c.getColumnIndex(DbField.SPECIAL)))))
                        .set_description(c.getString(c.getColumnIndex(DbField.DESCRIPTION)))
                        .set_color(Integer.parseInt(c.getString(c.getColumnIndex(DbField.COLOR))))
                        .set_creationDate(new Date(Long.parseLong(c.getString(c.getColumnIndex(DbField.CREATIONDATE)))))
                        .set_expirationDate(new Date(Long.parseLong(c.getString(c.getColumnIndex(DbField.EXPIRATIONDATE)))))
                        .set_lastEditDate(new Date(Long.parseLong(c.getString(c.getColumnIndex(DbField.LASTEDITDATE)))))
                        .set_id(Integer.parseInt(c.getString(c.getColumnIndex(DbField.ID))))
                        .build()
                );
            }while(c.moveToNext());
        }

        db.close();

        return notes;
    }

    private String checkEscape(String text) {
        return text.replaceAll("'", "''");
    }

    private int setSpecial(boolean special){
        return special ? 1 : 0;
    }

    private boolean setSpecial(int special){
        return special == 1;
    }
}
