package com.example.personale.noteexam.model;

import java.util.Comparator;

/**
 * Created by personale on 13/03/2017.
 */

public class CompareNoteAsc implements Comparator<Note> {
    @Override
    public int compare(Note o1, Note o2) {
        if(o1.getTitle().compareTo(o2.getTitle()) > 0){
            return -1;
        } else if( o1.getTitle().compareTo(o2.getTitle()) < 0){
            return 1;
        } else {
            return 0;
        }
    }
}
