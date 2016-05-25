package com.ohyuna.healthtracker;

public class Note {
    public int patientid;
    public String message;
    public String author;
    public String date;
    public int cat;
    public int utime;
    public Note(int patientid, String message, String author, String date, int cat, int utime) {
        this.patientid = patientid;
        this.message = message;
        this.author = author;
        this.date = date;
        this.cat = cat;
        this.utime = utime;
    }
}
