package com.ohyuna.healthtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Create extends AppCompatActivity {
    @Bind(R.id.image)
    CircleImageView image;
    @Bind(R.id.firstName)
    EditText firstName;
    @Bind(R.id.secondName)
    EditText secondName;
    @Bind(R.id.lastName)
    EditText lastName;
    @Bind(R.id.bDay)
    EditText bDay;
    @Bind(R.id.bMonth)
    EditText bMonth;
    @Bind(R.id.bYear)
    EditText bYear;
    @Bind(R.id.age)
    TextView age;
    @Bind(R.id.height)
    EditText height;
    @Bind(R.id.weight)
    EditText weight;
    @Bind(R.id.headCirc)
    EditText headCirc;
    @Bind(R.id.heightAge)
    TextView heightAge;
    @Bind(R.id.weightAge)
    TextView weightAge;
    @Bind(R.id.weightHeight)
    TextView weightHeight;
    @Bind(R.id.done)
    Button cancel;
    @Bind(R.id.save)
    Button save;
    @Bind(R.id.gender)
    Switch gender;
    @Bind(R.id.albendazoleCheck)
    CheckBox albendazoleCheck;
    @Bind(R.id.chispitasCheck)
    CheckBox chispitasCheck;
    @Bind(R.id.recumbentCheck)
    CheckBox recumbentCheck;
    boolean recumbent;
    String albendazoleDate;
    String chispitasDate;
    boolean haveImage;
    private int ageNum, heightNum, weightNum;
    private int ageinDays, ageinMonths;
    private String genderString;
    private boolean gen;
    private ZScore zscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        ButterKnife.bind(this);
        haveImage = false;
        ageNum = 0;
        heightNum = 0;
        weightNum = 0;
        ageinDays = 0;
        ageinMonths = 0;
        gen = false;
        recumbent = false;
        albendazoleDate = null;
        chispitasDate = null;
        zscore = new ZScore(this);
        image.setImageResource(R.drawable.cameraicon);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writePatient();
                finish();
            }
        });
        recumbentCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                recumbent = isChecked;
                updateZ();
            }
        });

        chispitasCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(Create.this, 0);
            }
        });
        albendazoleCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(Create.this, 1);
            }
        });
        bDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getAge();
                updateZ();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        bMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getAge();
                updateZ();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        bYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getAge();
                updateZ();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        height.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateZ();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateZ();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        gender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                gen = isChecked;
                updateZ();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
            haveImage = true;
        }
    }

    private void savePicture(Bitmap bm, String imgName) {
        OutputStream fOut = null;
        String strDirectory = Environment.getExternalStorageDirectory().toString();

        File f = new File(strDirectory, imgName);
        try {
            fOut = new FileOutputStream(f);

            /**Compress image**/
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

            /**Update image to gallery**/
            MediaStore.Images.Media.insertImage(getContentResolver(),
                    f.getAbsolutePath(), f.getName(), f.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateZ() {
        if (height.getText().toString().length() != 0 && ageinDays != -1) {
            String hA = String.format("%4.2f", zscore.getHA(Double.parseDouble(height.getText().toString()), ageinMonths, gen, recumbent));
            heightAge.setText(hA);
        }
        if (weight.getText().toString().length() != 0 && ageinDays != -1) {
            String wA = String.format("%4.2f", zscore.getWA(Double.parseDouble(weight.getText().toString()), ageinDays, gen));
            weightAge.setText(wA);

        }
        if (weight.getText().toString().length() != 0 && height.getText().toString().length() != 0) {
            String wH = String.format("%4.2f", zscore.getWH(ageinMonths, Double.parseDouble(weight.getText().toString()), Double.parseDouble(height.getText().toString()), gen, recumbent));
            weightHeight.setText(wH);
        }
    }

    public void writePatient() {
        DBManager db = new DBManager(Create.this);
        db.start();
        String birthdate = bDay.getText().toString() + "-" + bMonth.getText().toString() + "-" + bYear.getText().toString();
        Double hCirc;
        int recumbInt=recumbent?1:0;
        if (headCirc.getText().toString().trim().length() > 0) {
            hCirc = Double.parseDouble(headCirc.getText().toString());
        } else {
            hCirc = 0.0;
        }
        int patientid = db.newPatient(firstName.getText().toString(),
                secondName.getText().toString(),
                lastName.getText().toString(),
                birthdate,
                Double.parseDouble(height.getText().toString()),
                Double.parseDouble(weight.getText().toString()),
                hCirc,
                recumbInt,
                Double.parseDouble(heightAge.getText().toString()),
                Double.parseDouble(weightAge.getText().toString()),
                Double.parseDouble(weightHeight.getText().toString()),
                gen);
        BitmapDrawable bMap = (BitmapDrawable) image.getDrawable();
        if (bMap != null && haveImage) {
            savePicture(bMap.getBitmap(), String.valueOf(patientid));
        }
        db.addAlbendazole(patientid, albendazoleDate);
        db.addChispitas(patientid, chispitasDate);
        db.close();
    }

    public double getAge() {
        if (!(bYear.getText().length() == 0 || bMonth.getText().length() == 0 || bDay.getText().length() == 0)) {
            int y = Integer.parseInt(bYear.getText().toString());
            int m = Integer.parseInt(bMonth.getText().toString());
            int d = Integer.parseInt(bDay.getText().toString());
            AgeCalc calc = new AgeCalc();
            int[] ageArray = calc.calculateAge(d, m, y);
            age.setText(ageArray[0] + " days" + ageArray[1] + " months" + ageArray[2] + " years");
            ageinDays = 365 * ageArray[2] + (int) (30.5 * ageArray[1]) + ageArray[0];
            ageinMonths = 12 * ageArray[2] + ageArray[1] + (int) Math.round(ageArray[0] / 30.5);
            if (ageinMonths < 36) {
                headCirc.setFocusableInTouchMode(true);
                headCirc.setAlpha(1.0f);
            } else {
                headCirc.setFocusableInTouchMode(false);
                headCirc.setAlpha(0.5f);
            }
            return ageArray[0];
        } else {
            ageinDays = -1;
            headCirc.setFocusableInTouchMode(false);
            headCirc.setAlpha(0.5f);
            age.setText("");
            return -1;
        }
    }

    //chispitas = pillType 0, albendazole = pillType 1
    public void showPopup(final Activity context, int pillType) {
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.pilldateEntry);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.pill_date_entry, viewGroup);
        final int pType = pillType;
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popup.setElevation(10);
        popup.setFocusable(true);
        popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
        Button ok = (Button) layout.findViewById(R.id.enter);
        Button cancel = (Button) layout.findViewById(R.id.cancelDate);
        final EditText day = (EditText) layout.findViewById(R.id.day);
        final EditText month = (EditText) layout.findViewById(R.id.month);
        final EditText year = (EditText) layout.findViewById(R.id.year);
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (pType == 0) {
                    if (chispitasDate == null) {
                        chispitasCheck.setChecked(false);
                    }
                    if (albendazoleDate == null) {
                        albendazoleCheck.setChecked(false);
                    }
                }
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (day.getText().length() > 0 && month.getText().length() > 0 && year.getText().length() > 0) {
                    if (pType == 0) {
                        chispitasDate = day.getText().toString() + "-" + month.getText().toString() + "-" + year.getText().toString();
                        chispitasCheck.setChecked(true);
                    } else {
                        albendazoleDate = day.getText().toString() + "-" + month.getText().toString() + "-" + year.getText().toString();
                        albendazoleCheck.setChecked(true);
                    }
                    popup.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pType == 0) {
                    chispitasDate = null;
                    chispitasCheck.setChecked(false);
                } else {
                    albendazoleDate = null;
                    albendazoleCheck.setChecked(false);
                }
                popup.dismiss();
            }
        });
    }
}
