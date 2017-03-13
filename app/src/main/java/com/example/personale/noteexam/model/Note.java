package com.example.personale.noteexam.model;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by personale on 13/03/2017.
 */

public class Note implements Parcelable {
    private int id, color = Color.WHITE;
    private boolean specialNote;
    private String title, description;
    private Date creationDate, expirationDate, lastEditDate;

    public Note(BuilderNote builderNote){
        id = builderNote._id;
        color = builderNote._color;
        title = builderNote._title;
        specialNote = builderNote._specialNote;
        description = builderNote._description;
        creationDate = builderNote._creationDate;
        expirationDate = builderNote._expirationDate;
        lastEditDate = builderNote._lastEditDate;
    }

    protected Note(Parcel in) {
        id = in.readInt();
        color = in.readInt();
        title = in.readString();
        description = in.readString();
        specialNote = in.readInt() == 1;
        creationDate = (Date)in.readSerializable();
        expirationDate = (Date)in.readSerializable();
        lastEditDate = (Date)in.readSerializable();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public boolean isSpecialNote() {
        return specialNote;
    }

    public Note setSpecialNote(boolean specialNote) {
        this.specialNote = specialNote;
        return this;
    }

    public int getColor() {
        return color;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getExpirationDate() {
        if(!expirationDate.equals(new Date()))
            return expirationDate;

        return new Date(System.currentTimeMillis());
    }

    public Date getLastEditDate() {
        return lastEditDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(color);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(specialNote ? 1 : 0);
        dest.writeSerializable(creationDate);
        dest.writeSerializable(expirationDate);
        dest.writeSerializable(lastEditDate);
    }

    public static class BuilderNote{
        private int _id;
        private int _color;
        private boolean _specialNote;
        private String _title, _description;
        private Date _creationDate, _expirationDate, _lastEditDate;

        public Note build(){
            return new Note(this);
        }

        public BuilderNote set_specialNote(boolean _specialNote) {
            this._specialNote = _specialNote;
            return this;
        }

        public BuilderNote set_color(int _color) {
            this._color = _color;
            return this;
        }

        public BuilderNote set_title(String _title) {
            if(_title == null){
                _title = "";
            }

            this._title = _title;
            return this;
        }

        public BuilderNote set_description(String _description) {
            if(_description == null){
                _description = "";
            }

            this._description = _description;
            return this;
        }

        public BuilderNote set_creationDate(Date _creationDate) {
            this._creationDate = _creationDate;
            return this;
        }

        public BuilderNote set_expirationDate(Date _expirationDate) {
            if(_expirationDate != null){
                this._expirationDate = _expirationDate;
            } else {
                this._expirationDate = new Date();
            }
            return this;
        }

        public BuilderNote set_lastEditDate(Date _lastEditDate) {
            this._lastEditDate = _lastEditDate;
            return this;
        }

        public BuilderNote set_id(int _id) {
            this._id = _id;
            return this;
        }
    }
}