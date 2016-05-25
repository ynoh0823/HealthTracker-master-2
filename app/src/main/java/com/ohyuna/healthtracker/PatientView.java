package com.ohyuna.healthtracker;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

public class PatientView extends AppCompatActivity
        implements PatientInfoFragment.OnFragmentInteractionListener,
        PatientGraphsFragment.OnFragmentInteractionListener, GrowthHistoryFragment.OnFragmentInteractionListener, PatientNotesFragment.OnFragmentInteractionListener{


    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle b = this.getIntent().getExtras();
        patientid = b.getInt("patientid");
        patientname = b.getString("patientname");
        setSupportActionBar(toolbar);
        toolbar.setTitle(patientname);
        getSupportActionBar().setTitle(patientname);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_patient_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PatientInfoFragment();
                case 1:
                    return new PatientGraphsFragment();
                case 2:
                    return new GrowthHistoryFragment();
                case 3:
                    return new PatientNotesFragment();
            }
            return null;
        }

        @Override
        public int getCount() {

            return NUM_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "INFO";
                case 1:
                    return "GRAFICAS";
                case 2:
                    return "TABLAS";
                case 3:
                    return "NOTAS";
            }
            return null;
        }
    }
    public void onFragmentInteraction(Uri uri) {

    }
    public void onGHInteraction(Uri uri) {

    }

}
