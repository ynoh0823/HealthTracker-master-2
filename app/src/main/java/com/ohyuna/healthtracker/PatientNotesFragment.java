package com.ohyuna.healthtracker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;



public class PatientNotesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    notesAdapter adapter;
    DBManager db;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PatientNotesFragment() {

    }


    public static PatientNotesFragment newInstance(String param1, String param2) {
        PatientNotesFragment fragment = new PatientNotesFragment();
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
        View view = inflater.inflate(R.layout.fragment_patient_notes, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listview);
        db = new DBManager(this.getContext());
        db.start();
        ArrayList<Note> notes = db.getPatientNotes(((PatientView)getActivity()).patientid);
        db.close();
        adapter = new notesAdapter(this.getContext());
        adapter.setList(notes);
        listView.setAdapter(adapter);


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



    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}

class notesAdapter extends BaseAdapter {
    Context context;
    ArrayList<Note> notes;
    private static LayoutInflater inflater = null;

    public notesAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void setList(ArrayList<Note> notes) {
        this.notes = notes;

    }
    @Override
    public int getCount() {
        return notes.size();
    }
    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return notes.get(position).patientid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.patient_note, null);
        }
        TextView date = (TextView) vi.findViewById(R.id.date);
        TextView message = (TextView) vi.findViewById(R.id.message);
        TextView author = (TextView) vi.findViewById(R.id.author);
        TextView category = (TextView) vi.findViewById(R.id.category);
        date.setText(notes.get(position).date);
        message.setText(notes.get(position).message);
        author.setText(notes.get(position).author);
        category.setText(String.valueOf(notes.get(position).cat));
        return vi;
    }


}