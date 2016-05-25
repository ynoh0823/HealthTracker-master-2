package com.ohyuna.healthtracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Patient {
    public int id;
    public String first;
    public String second;
    public String last;
    public String birth;
    public double height;
    public double weight;
    public double head;
    public boolean recumbent;
    public boolean gender;
    public Patient(int id, String first, String second, String last, String birth) {
        this(id, first,second, last, birth, 0, 0, 0, 0, false);
    }
    public Patient(int id, String first, String second, String last, String birth, double height, double weight, double head, int recumbent, boolean gender){
        this.id = id;
        this.first = first;
        this.second = second;
        this.last = last;
        this.birth = birth;
        this.height = height;
        this.weight = weight;
        this.head = head;
        this.recumbent = recumbent==1?true:false;
        this.gender = gender;
    }
    public boolean equals(Patient patient) {
        return(patient.id == id);
    }
    public int[] getAge() {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        AgeCalc aCalc = new AgeCalc();
        Date date = null;
        try {
            date = df.parse(birth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date!=null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return aCalc.calculateAge(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        }
        return new int[]{-1,-1,-1};

    }
}
