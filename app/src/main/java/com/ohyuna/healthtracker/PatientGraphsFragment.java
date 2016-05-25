package com.ohyuna.healthtracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PatientGraphsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PatientGraphsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientGraphsFragment extends Fragment {

    private DBManager db;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PatientGraphsFragment() {

    }

    ImageView iv, iv1, iv2;
    Bitmap bmap, bmap1, bmap2;
    Canvas cv, cv1, cv2;
    Paint paint;
    int patientid;


    public static PatientGraphsFragment newInstance(String param1, String param2) {
        PatientGraphsFragment fragment = new PatientGraphsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_patient_graphs, container, false);
        patientid = ((PatientView) getActivity()).patientid;
        db = new DBManager(getActivity());
        db.start();
        Patient forGender = db.getPatientMostRecent(patientid);
        boolean gender = forGender.gender;
        db.close();
        startGraphs(gender);
        iv = (ImageView) view.findViewById(R.id.iv);
        iv1 = (ImageView) view.findViewById(R.id.iv1);
        iv2 = (ImageView) view.findViewById(R.id.iv2);
        Bitmap temp = Bitmap.createBitmap(bmap.getWidth(), bmap.getHeight(), bmap.getConfig());
        Bitmap temp1 = Bitmap.createBitmap(bmap1.getWidth(), bmap1.getHeight(), bmap1.getConfig());
        Bitmap temp2 = Bitmap.createBitmap(bmap2.getWidth(), bmap2.getHeight(), bmap2.getConfig());
        Canvas cv = new Canvas(temp);
        Canvas cv1 = new Canvas(temp1);
        Canvas cv2 = new Canvas(temp2);
        cv.drawBitmap(bmap,0,0,null);
        cv1.drawBitmap(bmap1, 0, 0, null);
        cv2.drawBitmap(bmap2, 0, 0, null);
        paint = new Paint();
        paint.setColor(Color.rgb(0,0,0));
        paint.setStrokeWidth(20);
        drawPoints(db, cv, cv1, cv2, gender, paint);
        iv.setImageDrawable(new BitmapDrawable(getActivity().getResources(), temp));
        iv1.setImageDrawable(new BitmapDrawable(getActivity().getResources(), temp1));
        iv2.setImageDrawable(new BitmapDrawable(getActivity().getResources(), temp2));

        return view;
    }
    public void drawPoints(DBManager db, Canvas cv, Canvas cv1, Canvas cv2, boolean gender, Paint paint) {
        db.start();
        Patient forBirth = db.getPatientMostRecent(patientid);
        String[] birthDate = forBirth.birth.split("-");
        int bDay = Integer.parseInt(birthDate[0]);
        int bMonth = Integer.parseInt(birthDate[1]);
        int bYear = Integer.parseInt(birthDate[2]);
        AgeCalc calc = new AgeCalc();
        ArrayList<GHEntry> ghEntries = db.getPatientGH(patientid);
        for (int i = 0; i < ghEntries.size(); i++) {
            GHEntry gh = ghEntries.get(i);
            String[] rDate = gh.date.split("-");
            int rDay = Integer.parseInt(rDate[0]);
            int rMonth = Integer.parseInt(rDate[1]);
            int rYear = Integer.parseInt(rDate[2]);
            double height = gh.height;
            double weight = gh.weight;
            int[] ageArray = calc.calculateAgeToDate(bDay, bMonth, bYear, rDay, rMonth, rYear);
            int ageinMonths = ageArray[1] + ageArray[2] * 12;
            float x1 = (float)(ageinMonths * 22.5 + 120);
            float y1 = (float)((height-40) * -10.2353) + 910;
            float zwamult = -30.0f;
            if (gender) {
                zwamult = -28.0645f;
            }
            float x2 = x1;

            float y2 = (float)(weight * zwamult) + 910;
            System.out.println(x2);
            System.out.println(y2);
            float y3 = (float)(weight * -25.147) + 900;
            float x3 = (float)((height - 45) * 17.4) + 135;
            cv.drawPoint(x1,y1, paint);
            cv1.drawPoint(x2,y2, paint);
            cv2.drawPoint(x3,y3, paint);
        }
        db.close();
    }

    public void startGraphs(boolean gender) {
        if (gender) {
            bmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.zhagirls);
            bmap1 = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.zwagirls);
            bmap2 = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.zwhgirls);
        } else {
            bmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.zhaboys);
            bmap1 = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.zwaboys);
            bmap2 = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.zwhboys);
        }
    }


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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
