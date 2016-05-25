package com.ohyuna.healthtracker;


public class GHEntry {
    public int id;
    public double height;
    public double weight;
    public double head;
    public int recumbent;
    public double zha;
    public double zwa;
    public double zwh;
    public String date;
    public int uTime;
    public GHEntry(int id, double height, double weight, double head, int recumbent, double zha, double zwa, double zwh, String date, int uTime) {
        this.id = id;
        this.height = height;
        this.weight = weight;
        this.head = head;
        this.recumbent = recumbent;
        this.zha = zha;
        this.zwa = zwa;
        this.zwh = zwh;
        this.date = date;
        this.uTime = uTime;
    }
}
