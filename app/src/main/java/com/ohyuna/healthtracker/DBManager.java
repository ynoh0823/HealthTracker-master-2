package com.ohyuna.healthtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class DBManager {
    private final String _INITPATIENTS = "CREATE TABLE IF NOT EXISTS Patients(id INTEGER, first TEXT, second TEXT, last TEXT, birth TEXT, gender INTEGER);";
    private final String _INITGH = "CREATE TABLE IF NOT EXISTS GH(id INTEGER, height REAL, weight REAL, head REAL, recumbent INTEGER, zha REAL, zwa REAL, zwh REAL, date TEXT, utime INTEGER);";
    private final String _INITNOTES = "CREATE TABLE IF NOT EXISTS Notes(id INTEGER, message TEXT, author TEXT, date TEXT, cat INTEGER, utime INT);";
    private String pathString;
    private File db;
    private SQLiteDatabase htdb;
    public DBManager(Context context) {
        pathString = context.getFilesDir().getPath() + "/healthTracker.sqlite";
        db = new File(pathString);
    }
    public void delete() {
        htdb.deleteDatabase(db);
    }
    public void start() {
        htdb = SQLiteDatabase.openOrCreateDatabase(db, null);
        htdb.execSQL(_INITPATIENTS);
        htdb.execSQL(_INITGH);
        htdb.execSQL(_INITNOTES);
    }
    public int newPatient(String first, String second, String last, String birth, double height, double weight, double head, int recumbent, double zha, double zwa, double zwh, boolean gender) {
        Random rand = new Random();
        int newid = rand.nextInt(10000000);
        Cursor checkidExists = null;
        while (checkidExists == null) {
            newid = rand.nextInt(10000000);
            checkidExists = htdb.rawQuery("Select * from Patients where id=" + newid, null);
        }
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        int uTime = (int) (System.currentTimeMillis() /1000L);
        ContentValues cvPatients, cvGH, cvNotes;
        cvPatients = new ContentValues(); cvGH = new ContentValues(); cvNotes = new ContentValues();
        cvPatients.put("id", newid);
        cvPatients.put("first", first);
        cvPatients.put("second", second);
        cvPatients.put("last", last);
        cvPatients.put("birth", birth);
        cvPatients.put("gender", gender);
        cvGH.put("id", newid);
        cvGH.put("height", height);
        cvGH.put("weight", weight);
        cvGH.put("head", head);
        cvGH.put("recumbent", recumbent);
        cvGH.put("zha", zha);
        cvGH.put("zwa", zwa);
        cvGH.put("zwh", zwh);
        cvGH.put("date", date);
        cvGH.put("utime", uTime);
        cvNotes.put("id", newid);
        cvNotes.put("message", "PATIENT CREATED");
        cvNotes.put("author", "SYSTEM");
        cvNotes.put("date", date);
        cvNotes.put("cat", 0);
        cvNotes.put("utime", uTime);
        htdb.insert("Patients", null, cvPatients);
        htdb.insert("GH", null, cvGH);
        htdb.insert("Notes", null, cvNotes);
        System.out.println("patient created with id " + newid);
        return newid;
    }
    public void updatePatient(int patientid, double height, double weight, double head, int recumbent, double zha, double zwa, double zwh) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        int uTime = (int) (System.currentTimeMillis() /1000L);
        ContentValues cvGH = new ContentValues();
        cvGH.put("id", patientid);
        cvGH.put("height", height);
        cvGH.put("weight", weight);
        cvGH.put("head", head);
        cvGH.put("recumbent", recumbent);
        cvGH.put("zha", zha);
        cvGH.put("zwa", zwa);
        cvGH.put("zwh", zwh);
        cvGH.put("date", date);
        cvGH.put("utime", uTime);
        htdb.insert("GH", null, cvGH);
    }
    public String[] pillsMostRecent(int patientid) {
        Cursor c = htdb.query("Notes", null, "id=" + patientid + " AND cat=1", null, null, null, "utime DESC", null);
        String dateChispitas = null;
        String dateAlbendazole = null;
        if (c!=null) {
            c.moveToFirst();
            dateChispitas = c.getString(c.getColumnIndex("date"));
            c.close();
        }
        c = htdb.query("Notes", null, "id=" + patientid + " AND cat=2", null, null, null, "utime DESC", null);
        if (c!=null) {
            c.moveToFirst();
            dateAlbendazole = c.getString(c.getColumnIndex("date"));
            c.close();
        }
        return new String[]{dateChispitas, dateAlbendazole};
    }
    public boolean[] needPills(int patientid) {
        String[] dates = pillsMostRecent(patientid);
        AgeCalc aCalc = new AgeCalc();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date lastDateC = null;
        Date lastDateA = null;
        Calendar todayCal = Calendar.getInstance();
        int tDay = todayCal.get(Calendar.DAY_OF_MONTH);
        int tMonth = todayCal.get(Calendar.MONTH);
        int tYear = todayCal.get(Calendar.YEAR);
        Patient checkPatient = getPatientMostRecent(patientid);
        int[] age = checkPatient.getAge();
        int ageinMonths = age[1] + age[2] * 12;
        boolean needChispitas = false;
        boolean needAlbendazole = false;
        if (dates[0] != null) {
            try {
                lastDateC = df.parse(dates[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cRecent = Calendar.getInstance();
            cRecent.setTime(lastDateC);
            int cDay = cRecent.get(Calendar.DAY_OF_MONTH);
            int cMonth = cRecent.get(Calendar.MONTH);
            int cYear = cRecent.get(Calendar.YEAR);
            int[] cDuration = aCalc.calculateAgeToDate(cDay, cMonth, cYear, tDay, tMonth, tYear);
            int cMonthDuration = cDuration[1] + cDuration[2]*12;
            needChispitas = cMonthDuration >= 3;
        } else {
            if (ageinMonths >= 6 && ageinMonths <= 60) {
                needChispitas = true;
            }
        }
        if (dates[1] != null) {
            try {
                lastDateA = df.parse(dates[1]);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar aRecent = Calendar.getInstance();
            aRecent.setTime(lastDateA);

            int aDay = aRecent.get(Calendar.DAY_OF_MONTH);
            int aMonth = aRecent.get(Calendar.MONTH);
            int aYear = aRecent.get(Calendar.YEAR);

            int[] aDuration = aCalc.calculateAgeToDate(aDay, aMonth, aYear, tDay, tMonth, tYear);
            int aMonthDuration = aDuration[1] + aDuration[2]*12;
            needAlbendazole = aMonthDuration >= 6;
        } else {
            if (ageinMonths >= 12 && ageinMonths <= 60) {
                needAlbendazole = true;
            }
        }

        return new boolean[]{needChispitas, needAlbendazole};

    }
    public void newNote(int patientid, String message, String author, int cat, String inputDate) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String date = inputDate;
        if (date==null) {
            date = df.format(Calendar.getInstance().getTime());
        }
        Calendar forU = Calendar.getInstance();
        try {
            forU.setTime(df.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int uTime = (int) (forU.getTimeInMillis() /1000L);
        ContentValues cv = new ContentValues();
        cv.put("id", patientid);
        cv.put("message", message);
        cv.put("author", author);
        cv.put("cat", cat);
        cv.put("date", date);
        cv.put("utime", uTime);
        htdb.insert("Notes", null, cv);
    }
    public void addChispitas(int patientid, String date) {
        if (date!=null) {
            newNote(patientid, "Chispitas", "SYSTEM", 1, date);
        }
    }
    public void addAlbendazole(int patientid, String date) {
        if (date!=null) {
            newNote(patientid, "Albendazoles", "SYSTEM", 2, date);
        }
    }
    public Patient getPatientMostRecent (int patientid) {
        System.out.println(patientid);
        Cursor c = htdb.query("Patients", null, "id=" + patientid, null, null, null, null, null);
        DatabaseUtils.dumpCursor(c);
        System.out.println(c.getCount());
        c.moveToFirst();
        String first = c.getString(c.getColumnIndex("first"));
        String second = c.getString(c.getColumnIndex("second"));
        String last = c.getString(c.getColumnIndex("last"));
        String birth = c.getString(c.getColumnIndex("birth"));
        boolean gender = c.getInt(c.getColumnIndex("gender"))==1;
        c.close();
        c = htdb.query("GH", null, "id=" + patientid, null, null, null, "utime DESC", null);
        c.moveToFirst();
        double height = c.getDouble(c.getColumnIndex("height"));
        double weight = c.getDouble(c.getColumnIndex("weight"));
        double head = c.getDouble(c.getColumnIndex("head"));
        int recumbent = c.getInt(c.getColumnIndex("recumbent"));
        c.close();
        Patient patient = new Patient(patientid, first, second, last, birth, height, weight, head, recumbent, gender);
        return patient;
    }
    public ArrayList<GHEntry> getPatientGH (int patientid) {
        ArrayList<GHEntry> ghEntries = new ArrayList<>();
        Cursor c = htdb.query("GH", null, "id=" + patientid, null, null, null, "utime DESC", null);
        if (c!=null) {
            if (c.moveToFirst()) {
                do {
                    double height = c.getDouble(c.getColumnIndex("height"));
                    double weight = c.getDouble(c.getColumnIndex("weight"));
                    double head = c.getDouble(c.getColumnIndex("head"));
                    int recumbent = c.getInt(c.getColumnIndex("recumbent"));
                    double zha = c.getDouble(c.getColumnIndex("zha"));
                    double zwa = c.getDouble(c.getColumnIndex("zwa"));
                    double zwh = c.getDouble(c.getColumnIndex("zwh"));
                    String date = c.getString(c.getColumnIndex("date"));
                    int uTime = c.getInt(c.getColumnIndex("utime"));
                    GHEntry ghEntry = new GHEntry(patientid, height, weight, head, recumbent, zha, zwa, zwh, date, uTime);
                    ghEntries.add(ghEntry);
                } while (c.moveToNext());
            }
            c.close();
        }
        return ghEntries;
    }
    public ArrayList<Note> getPatientNotes (int patientid) {
        ArrayList<Note> notes = new ArrayList<>();
        Cursor c = htdb.query("Notes", null, "id=" + patientid, null, null, null, "utime DESC", null);
        if (c!= null) {
            if (c.moveToFirst()) {
                do {
                    String message = c.getString(c.getColumnIndex("message"));
                    String author = c.getString(c.getColumnIndex("author"));
                    String date = c.getString(c.getColumnIndex("date"));
                    int cat = c.getInt(c.getColumnIndex("cat"));
                    int uTime = c.getInt(c.getColumnIndex("utime"));
                    Note note = new Note(patientid, message, author, date, cat, uTime);
                    notes.add(note);
                } while (c.moveToNext());
            }
            c.close();
        }
        return notes;
    }
    public ArrayList<Patient> searchPatients (String first, String second, String last) {
        ArrayList<Patient> patients = new ArrayList<>();
        String where = "";
        if (first != null) {
            where = where + "first like '" + first + "' ";
        }
        if (second != null) {
            if (first!=null) {
                where += "or ";
            }
            where = where + "second like '" + second + "' ";
        }
        if (last != null) {
            if (first!=null || second!=null) {
                where += "or ";
            }
            where = where + "last like '" + last + "' ";
        }
        Cursor c = htdb.query("Patients", null, where, null, null, null, null, null);

        if (c!=null) {
            if (c.moveToFirst()) {
                do {
                    String firstName = c.getString(c.getColumnIndex("first"));
                    String secondName = c.getString(c.getColumnIndex("second"));
                    String lastName = c.getString(c.getColumnIndex("last"));
                    String birth = c.getString(c.getColumnIndex("birth"));
                    int id = c.getInt(c.getColumnIndex("id"));
                    Patient patient = new Patient(id, firstName, secondName, lastName, birth);
                    patients.add(patient);
                } while (c.moveToNext());
            }
            c.close();
        }
        return patients;
    }
    public void close() {
        htdb.close();
    }

}
