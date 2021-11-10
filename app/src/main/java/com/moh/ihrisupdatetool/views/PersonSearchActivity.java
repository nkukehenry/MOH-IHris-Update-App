package com.moh.ihrisupdatetool.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.moh.ihrisupdatetool.R;

import java.util.TreeMap;
import java.util.TreeSet;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PersonSearchActivity extends AppCompatActivity {

    Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_search);

        searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(v->{
            Intent intent = new Intent(this, FormsActivity.class);
            v.getContext().startActivity(intent);
        });

    }

}