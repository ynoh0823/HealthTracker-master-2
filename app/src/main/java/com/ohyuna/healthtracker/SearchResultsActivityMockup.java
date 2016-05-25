package com.ohyuna.healthtracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Bind;

public class SearchResultsActivityMockup extends AppCompatActivity {
    @Bind(R.id.imageView2)
    CircleImageView pImage1;
    @Bind(R.id.imageView3)
    CircleImageView pImage2;
    @Bind(R.id.imageView4)
    CircleImageView pImage3;
    @Bind(R.id.textView13)
    TextView pName1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results_mockup);
        ButterKnife.bind(this);
        pImage1.setImageResource(R.drawable.child1);
        pImage2.setImageResource(R.drawable.child2);
        pImage3.setImageResource(R.drawable.child3);
        pImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchResultsActivityMockup.this, PatientViewMockup.class);
                startActivity(intent);
            }
        });
        pName1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v) {
                Intent intent = new Intent(SearchResultsActivityMockup.this, PatientViewMockup.class);
                startActivity(intent);
            }
        });
    }
}
