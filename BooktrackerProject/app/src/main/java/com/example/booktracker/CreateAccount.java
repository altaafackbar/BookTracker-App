package com.example.booktracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
        Button backBtn = findViewById(R.id.back_button);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        number = findViewById(R.id.number);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_user();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void new_user(){
        final String u_email = email.getText().toString();
        String u_pass = password.getText().toString();
        String u_num = number.getText().toString();
        if(u_email.isEmpty() || u_pass.isEmpty() || u_num.isEmpty()){
            Toast toast = Toast.makeText(getApplicationContext(), "Please fill out all required information", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            final HashMap<String, String> data = new HashMap<>();
            data.put("UserEmail", u_email);
            data.put("UserPass", u_pass);
            data.put("UserNum", u_num);
            db = FirebaseFirestore.getInstance();
            DocumentReference docIdRef = db.collection("Users").document(u_email);
            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("TAG", "Document exists!");
                            Toast toast = Toast.makeText(getApplicationContext(), "Username unavailable, please choose another", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            Log.d("TAG", "Document does not exist!");
                            db.collection("Users").document(u_email).set(data);
                            MainActivity.current_user = u_email;
                            Intent sign_in = new Intent(getApplicationContext(), MainScreen.class);
                            startActivity(sign_in);
                        }
                    } else {
                        Log.d("TAG", "Failed with: ", task.getException());
                    }
                }
            });
        }


    }
}