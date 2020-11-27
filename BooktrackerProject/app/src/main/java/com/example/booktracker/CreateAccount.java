/**
*CreateAccount
* Used in process of creating accounts and editing profiles.
 */
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
    private EditText confirmPass;
    private EditText number;
    private FirebaseFirestore db;
    private String intentTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        * Creates the display, sets all fields to the
        * current values in the database if user is
        * editing, otherwise set to instruction messages
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Button sign_up = findViewById(R.id.sign_up);
        Button backBtn = findViewById(R.id.back_button);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPass = findViewById(R.id.confirmPassword);
        number = findViewById(R.id.number);
        Bundle editB = getIntent().getExtras();
        //check if this is a edit task or create new account task
        if(editB != null){
            intentTask = getIntent().getExtras().getString("task");
        }
        else{
            intentTask = "create";
        }

        if(intentTask != null && intentTask.equals("edit")){
            //If this is an edit task
            db = FirebaseFirestore.getInstance();
            DocumentReference docIdRef = db.collection("Users").document(MainActivity.current_user);
            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("TAG", "DocumentSnapshot data: " + document.getData().get("UserEmail"));
                            email.setText(document.getData().get("UserEmail").toString());
                            password.setText(document.getData().get("UserPass").toString());
                            confirmPass.setText(document.getData().get("UserPass").toString());
                            number.setText(document.getData().get("UserNum").toString());
                            //grey out everything except number
                            email.setEnabled(false);
                            email.setFocusable(false);
                            password.setEnabled(false);
                            password.setFocusable(false);
                            confirmPass.setEnabled(false);
                            confirmPass.setFocusable(false);
                        }
                    }
                }
            });


        }
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
        /*
        * Takes care of account creation
        * Ensures all fields are properly filled.
         */
        final String u_email = email.getText().toString();
        final String u_pass = password.getText().toString();
        final String u_num = number.getText().toString();
        if(u_email.isEmpty() || u_pass.isEmpty() || u_num.isEmpty()){
            //if any fields are empty
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
                        if (document.exists() && !intentTask.equals("edit")) {
                            //if we are creating an account and the usernamme is taken
                            Log.d("TAG", "Document exists!");
                            Toast toast = Toast.makeText(getApplicationContext(), "Username unavailable, please choose another", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            Log.d("TAG", "Document does not exist!");
                            if(intentTask.equals("edit")){
                                db.collection("Users").document(u_email).update("UserNum", u_num);
                                finish();
                            }
                            else{
                                if(u_pass.equals(confirmPass.getText().toString())){
                                    db.collection("Users").document(u_email).set(data);
                                    MainActivity.current_user = u_email;
                                    Intent sign_in = new Intent(getApplicationContext(), MainScreen.class);
                                    startActivity(sign_in);
                                }
                                else{
                                    Toast toast = Toast.makeText(getApplicationContext(), "The passwords do not match", Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                            }

                        }
                    } else {
                        Log.d("TAG", "Failed with: ", task.getException());
                    }
                }
            });
        }
    }
}