package com.example.booktracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    TextView descriptionBox = (TextView) (findViewById(R.id.descriptionTV));
    TextView ownerBox = (TextView) (findViewById(R.id.ownerTV));
    TextView statusBox = (TextView) (findViewById(R.id.statusTV));

    public void searchKeyword() {
        //get keyword from editText
        //iterate through list of books whose description contains the word with

    }


}

