package com.example.booktracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class CreateAccount extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private EditText number;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Button sign_up = findViewById(R.id.sign_up);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        number = findViewById(R.id.number);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_user();
            }
        });
    }
    private void new_user(){
        String u_email = email.getText().toString();
        String u_pass = password.getText().toString();
        String u_num = number.getText().toString();
        HashMap<String, String> data = new HashMap<>();
        data.put("UserEmail", u_email);
        data.put("UserPass", u_pass);
        data.put("UserNum", u_num);
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(u_email).set(data);
        MainActivity.current_user = u_email;
        Intent sign_in = new Intent(this, MainScreen.class);


        startActivity(sign_in);
    }
}