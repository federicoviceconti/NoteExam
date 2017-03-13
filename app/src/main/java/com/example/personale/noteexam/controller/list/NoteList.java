package com.example.personale.noteexam.controller.list;

import android.content.Context;

import com.example.personale.noteexam.controller.database.DbHandler;
import com.example.personale.noteexam.model.Note;

import java.util.ArrayList;

/**
 * Created by personale on 13/03/2017.
 */

public class NoteList {
    private ArrayList<Note> notes;
    private final DbHandler dbHandler;

    public NoteList(Context c){
        notes = new ArrayList<>();
        dbHandler = new DbHandler(c);
    }

    public void add(Note n){
        notes.add(0, n);
        dbHandler.insert(n);
    }

    public void edit(int pos, Note n){
        notes.set(pos, n);
        dbHandler.edit(n);
    }

    public void delete(int pos){
        notes.remove(pos);
        dbHandler.delete(getNote(pos).getId());
    }

    public int getSize() {
        return notes.size();
    }

    public Note getNote(int pos) {
        return notes.get(pos);
    }

    public void loadNoteFromDb() {
        this.notes = dbHandler.getAllNotes();
    }
}
