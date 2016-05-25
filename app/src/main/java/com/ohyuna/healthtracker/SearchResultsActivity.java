package com.ohyuna.healthtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;


public class SearchResultsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    DBManager db;
    ListView listview;
    patientAdapter adapter;
    EditText firstName;
    EditText secondName;
    EditText lastName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        adapter = new patientAdapter(this);
        db = new DBManager(SearchResultsActivity.this);
        db.start();
        Bundle b = this.getIntent().getExtras();
        String[] firstSearch = b.getStringArray("searchparams");
        ArrayList<Patient> firstSearchResults = db.searchPatients(firstSearch[0], firstSearch[1], firstSearch[2]);
        adapter.setList(firstSearchResults);
        listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
        adapter.notifyDataSetChanged();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(SearchResultsActivity.this);
            }
        });
    }
    public void showPopup(final Activity context) {

        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.searchPopup);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.search_entry, viewGroup);

        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popup.setElevation(10);
        popup.setFocusable(true);
        popup.showAtLocation(layout, Gravity.CENTER, 0,0);


        Button search2 = (Button) layout.findViewById(R.id.search2);
        firstName = (EditText) layout.findViewById(R.id.firstNameSearch);
        secondName = (EditText) layout.findViewById(R.id.secondNameSearch);
        lastName = (EditText) layout.findViewById(R.id.lastNameSearch);
        search2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first = null;
                String second = null;
                String last = null;
                if(!isEmpty(firstName)) {
                    first = firstName.getText().toString();
                }
                if(!isEmpty(secondName)) {
                    second = secondName.getText().toString();
                }
                if(!isEmpty(lastName)) {
                    last = lastName.getText().toString();
                }
                ArrayList<Patient> searchResults = db.searchPatients(first, second, last);
                adapter.setList(searchResults);
                adapter.notifyDataSetChanged();
                popup.dismiss();


            }
        });
    }
    public boolean isEmpty(EditText editText) {
        return (editText.getText().toString().trim().length()==0);
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

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent i;
        int patientid = (int)adapter.getItemId(arg2);
        Bundle toPatientView = new Bundle();
        toPatientView.putInt("patientid", patientid);
        toPatientView.putString("patientname", adapter.getName(arg2));
        i = new Intent(SearchResultsActivity.this, PatientView.class);
        i.putExtras(toPatientView);
        startActivity(i);
    }



}
class patientAdapter extends BaseAdapter {
    Context context;
    ArrayList<Patient> patientResults;
    private static LayoutInflater inflater = null;

    public patientAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void setList(ArrayList<Patient> patientResults) {
        this.patientResults = patientResults;

    }
    @Override
    public int getCount() {
        return patientResults.size();
    }
    @Override
    public Object getItem(int position) {
        return patientResults.get(position);
    }
    public String getName(int position) {
        return patientResults.get(position).first + " " + patientResults.get(position).second + " " + patientResults.get(position).last;
    }
    @Override
    public long getItemId(int position) {
        return patientResults.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.patient_search_result, null);
        }
        TextView text = (TextView) vi.findViewById(R.id.pID);
        String name = patientResults.get(position).first + " " + patientResults.get(position).second + " " + patientResults.get(position).last;
        text.setText(name);
        CircleImageView image = (CircleImageView) vi.findViewById(R.id.pImage);
        String strDirectory = Environment.getExternalStorageDirectory().toString();
        File imgFile = new File(strDirectory, String.valueOf(patientResults.get(position).id));
        if (imgFile.exists()) {
            Bitmap bmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            image.setImageBitmap(bmap);
        } else {
            image.setImageDrawable(null);
        }
        return vi;
    }


}
