package com.ohyuna.healthtracker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


public class GrowthHistoryFragment extends Fragment {
    DBManager db;
    ghAdapter adapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public GrowthHistoryFragment() {
    }


    @SuppressWarnings("unused")
    public static GrowthHistoryFragment newInstance(String param1, String param2) {
        GrowthHistoryFragment fragment = new GrowthHistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_growthhistory_list, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listview);
        db = new DBManager(this.getContext());
        db.start();
        ArrayList<GHEntry> ghEntries = db.getPatientGH(((PatientView)getActivity()).patientid);
        db.close();
        adapter = new ghAdapter(this.getContext());
        adapter.setList(ghEntries);
        listView.setAdapter(adapter);
        // Set the adapter

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) getActivity();

        } catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

 }