package com.example.personale.noteexam.model;

import java.util.Comparator;

/**
 * Created by personale on 13/03/2017.
 */

public class CompareNoteDesc implements Comparator<Note> {
    @Override
    public int compare(Note o1, Note o2) {
        return o1.getTitle().compareTo(o2.getTitle());
    }
}
