package com.ohyuna.healthtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.text.Editable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PatientInfoFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private boolean editing = false;

    private int patientid;
    private Patient patient;
    private DBManager db;

    private String chispitasDate;
    private String albendazoleDate;
    private boolean recumbent;

    private int ageNum,heightNum,weightNum;
    private int ageinDays, ageinMonths;
    private String genderString;
    private boolean gen;
    private ZScore zscore;
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
    @Bind(R.id.editSave)
    Button editSave;
    @Bind(R.id.done)
    Button done;
    @Bind(R.id.image)
    CircleImageView pImage;
    @Bind(R.id.gender)
    Switch gender;
    @Bind(R.id.chispitasCheck)
    CheckBox chispitasCheck;
    @Bind(R.id.albendazoleCheck)
    CheckBox albendazoleCheck;
    @Bind(R.id.recumbentCheck)
    CheckBox recumbentCheck;
    private boolean haveImage;

    public PatientInfoFragment() {

    public static PatientInfoFragment newInstance(String param1, String param2) {
        PatientInfoFragment fragment = new PatientInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.patientid = ((PatientView) getActivity()).patientid;
        System.out.println("patientid is" + this.patientid);

        db = new DBManager(getActivity());
        db.start();
        View view = inflater.inflate(R.layout.fragment_patient_view_info, container, false);
        ButterKnife.bind(this, view);
        haveImage = false;
        zscore = new ZScore(super.getContext());
        ageNum = 0; heightNum=0; weightNum=0;
        ageinDays = 0; ageinMonths=0;
        gen=false;
        setImage();
        updateInfo();
        getAge();
        updateZ();


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
                if (!chispitasCheck.isChecked()) {
                    showPopup(getActivity(), 1);
                }
            }
        });
        albendazoleCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!albendazoleCheck.isChecked()) {
                    showPopup(getActivity(), 1);
                }
            }
        });
        editSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editing) {
                    int recumbInt = recumbentCheck.isChecked()?1:0;
                    db.updatePatient(patientid,
                            Double.parseDouble(height.getText().toString()),
                            Double.parseDouble(weight.getText().toString()),
                            Double.parseDouble(headCirc.getText().toString()),
                            recumbInt,
                            Double.parseDouble(heightAge.getText().toString()),
                            Double.parseDouble(weightAge.getText().toString()),
                            Double.parseDouble(weightHeight.getText().toString()));
                    db.addAlbendazole(patientid, albendazoleDate);
                    db.addChispitas(patientid, chispitasDate);
                    updateInfo();
                    BitmapDrawable bMap = (BitmapDrawable) pImage.getDrawable();
                    if (bMap != null && haveImage) {
                        savePicture(bMap.getBitmap(), String.valueOf(patientid));
                    }
                    firstName.setFocusableInTouchMode(false);
                    secondName.setFocusableInTouchMode(false);
                    lastName.setFocusableInTouchMode(false);
                    bDay.setFocusableInTouchMode(false);
                    bMonth.setFocusableInTouchMode(false);
                    bYear.setFocusableInTouchMode(false);
                    weight.setFocusableInTouchMode(false);
                    height.setFocusableInTouchMode(false);
                    headCirc.setFocusableInTouchMode(false);
                    pImage.setFocusableInTouchMode(false);
                    gender.setClickable(false);
                    recumbentCheck.setClickable(false);
                    albendazoleCheck.setClickable(false);
                    chispitasCheck.setClickable(false);
                    editSave.setText("Editar");
                    editing = false;
                } else {
                    editing = true;
                    firstName.setFocusableInTouchMode(true);
                    secondName.setFocusableInTouchMode(true);
                    lastName.setFocusableInTouchMode(true);
                    bDay.setFocusableInTouchMode(true);
                    bMonth.setFocusableInTouchMode(true);
                    bYear.setFocusableInTouchMode(true);
                    weight.setFocusableInTouchMode(true);
                    height.setFocusableInTouchMode(true);
                    headCirc.setFocusableInTouchMode(true);
                    pImage.setFocusableInTouchMode(true);
                    recumbentCheck.setClickable(true);
                    albendazoleCheck.setClickable(true);
                    chispitasCheck.setClickable(true);
                    gender.setClickable(true);
                    editSave.setText("Guardar");
                }
            }
        });
        pImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editing) {
                    dispatchTakePictureIntent();
                }
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        bDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

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
                if(isChecked) {
                    genderString = "F";
                    gen = true;
                    updateZ();
                } else {
                    genderString = "M";
                    gen = false;
                    updateZ();
                }
            }
        });
        return view;
    }

    public void updateInfo() {
        patient = db.getPatientMostRecent(patientid);
        boolean[] needPills = db.needPills(patientid);
        chispitasCheck.setChecked(!needPills[0]);
        albendazoleCheck.setChecked(!needPills[1]);
        gen = patient.gender;
        firstName.setText(patient.first);
        secondName.setText(patient.second);
        lastName.setText(patient.last);
        String[] bDayArray = patient.birth.split("-");
        bDay.setText(bDayArray[0]);
        bMonth.setText(bDayArray[1]);
        bYear.setText(bDayArray[2]);
        gender.setChecked(gen);
        recumbentCheck.setChecked(patient.recumbent);
        weight.setText(String.valueOf(patient.weight));
        height.setText(String.valueOf(patient.height));
        headCirc.setText(String.valueOf(patient.head));
    }
    public void setImage() {
        String strDirectory = Environment.getExternalStorageDirectory().toString();
        File imgFile = new File(strDirectory, String.valueOf(patientid));
        if (imgFile.exists()) {
            Bitmap bmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            pImage.setImageBitmap(bmap);
            haveImage = true;
        } else {
            pImage.setImageResource(R.drawable.cameraicon);
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1 && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            pImage.setImageBitmap(imageBitmap);
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
            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                    f.getAbsolutePath(), f.getName(), f.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateZ() {
        if(height.getText().toString().length()!=0 && ageinDays != -1) {
            String hA = String.format("%4.2f",zscore.getHA(Double.parseDouble(height.getText().toString()),ageinMonths, gen, recumbentCheck.isChecked()));
            heightAge.setText(hA);
        }
        if(weight.getText().toString().length()!=0 && ageinDays != -1) {
            String wA = String.format("%4.2f",zscore.getWA(Double.parseDouble(weight.getText().toString()),ageinDays, gen));
            weightAge.setText(wA);

        }
        if(weight.getText().toString().length()!=0 && height.getText().toString().length()!=0) {
            String wH = String.format("%4.2f",zscore.getWH(ageinMonths, Double.parseDouble(weight.getText().toString()),Double.parseDouble(height.getText().toString()),gen, recumbentCheck.isChecked()));
            weightHeight.setText(wH);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
    public double getAge() {
        if(!(bYear.getText().length()==0 || bMonth.getText().length()==0 || bDay.getText().length()==0)) {
            int y = Integer.parseInt(bYear.getText().toString());
            int m = Integer.parseInt(bMonth.getText().toString());
            int d = Integer.parseInt(bDay.getText().toString());
            AgeCalc calc = new AgeCalc();
            int[] ageArray = calc.calculateAge(d,m,y);
            age.setText(ageArray[0] + " days" + ageArray[1] + " months" + ageArray[2] + " years");
            ageinDays = 365 * ageArray[2] + (int)(30.5 * ageArray[1]) + ageArray[0];
            ageinMonths = 12 * ageArray[2] + ageArray[1] + (int) Math.round(ageArray[0]/30.5);
            if (ageinMonths < 36 && editing) {
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
    @Override
    public void onPause() {
        super.onPause();
        db.close();
    }
    @Override
    public void onResume() {
        super.onResume();
        db.start();
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
