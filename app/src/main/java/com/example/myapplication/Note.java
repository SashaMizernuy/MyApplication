package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName="note_table")
public class Note {

    @PrimaryKey(autoGenerate=true)
    private int id;

    private String lat;

    private String lng;


    private int priority;

    public Note(String lat, String lng, int priority) {
        this.lat = lat;
        this.lng = lng;
        this.priority = priority;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public int getPriority() {
        return priority;
    }
}
