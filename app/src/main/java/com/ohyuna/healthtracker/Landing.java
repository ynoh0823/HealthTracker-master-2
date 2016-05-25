package com.ohyuna.healthtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Landing extends AppCompatActivity {
    @Bind(R.id.clear)
    Button clear;
    @Bind(R.id.search)
    Button search;
    @Bind(R.id.create)
    Button create;
    EditText firstName;
    EditText secondName;
    EditText lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.bind(this);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Landing.this, Create.class);
                startActivity(intent);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(Landing.this);
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBManager db = new DBManager(Landing.this);
                db.start();
                db.delete();
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
        firstName  = (EditText) layout.findViewById(R.id.firstNameSearch);
        secondName = (EditText) layout.findViewById(R.id.secondNameSearch);
        lastName = (EditText) layout.findViewById(R.id.lastNameSearch);
        Button search2 = (Button) layout.findViewById(R.id.search2);

        search2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first = null;
                String second = null;
                String last = null;
                if (!isEmpty(firstName)) {
                    first = firstName.getText().toString();
                }
                if (!isEmpty(secondName)) {
                    second = secondName.getText().toString();
                }
                if (!isEmpty(lastName)) {
                    last = lastName.getText().toString();
                }
                Bundle b = new Bundle();
                b.putStringArray("searchparams", new String[]{first, second, last});
                popup.dismiss();
                Intent intent = new Intent(Landing.this, SearchResultsActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

    }
    public boolean isEmpty(EditText editText) {
        return (editText.getText().toString().trim().length()==0);
    }
}
