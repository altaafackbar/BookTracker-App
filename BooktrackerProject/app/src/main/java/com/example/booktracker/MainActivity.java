/**
*MainActivity
* Handles account creation using CreateAccount class
* Handles sign in
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private Button signInButton;
    private Button create_acc_btn;
    private FirebaseFirestore db;
    public static String current_user;

    /**
     * Sets up the layout
     * as well as listeners for the buttons
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Sets up buttons that handles account creation and sign in
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signInButton = findViewById(R.id.sign_in);
        create_acc_btn = findViewById(R.id.create_acc);

        create_acc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create_account();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    /**
     * Calls CreateAccount class to create a new user account
     */
    private void create_account(){
        Intent new_user = new Intent(this, CreateAccount.class);
        new_user.setFlags(new_user.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
        startActivity(new_user);
    }
    /**
     *Attempts to sign user in using the email and pass entered
     * Checks Firestore to ensure user has been registered
     */
    private void signIn() {
        EditText email = findViewById(R.id.email2);
        EditText pass = findViewById(R.id.password);
        final String email_s = email.getText().toString();
        final String passw = pass.getText().toString();
        if(email_s.isEmpty() || passw.isEmpty()){
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter login info", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            final HashMap<String, String> data = new HashMap<>();
            data.put("UserEmail", email_s);
            data.put("UserPass", passw);
            db = FirebaseFirestore.getInstance();
            final DocumentReference docIdRef = db.collection("Users").document(email_s);
            final Intent sign_in = new Intent(this, MainScreen.class);
            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String pass = document.getData().get("UserPass").toString();
                            Log.d(TAG, "Document exists!");
                            Log.d(TAG, "Password is: " + document.getData().get("UserPass"));
                            if(pass.equals(passw)){
                                current_user = document.getData().get("UserEmail").toString();
                                startActivity(sign_in);
                            }else{
                                Toast toast = Toast.makeText(getApplicationContext(), "Password is wrong", Toast.LENGTH_SHORT);
                                toast.show();
                            }

                        } else {
                            Log.d(TAG, "Document does not exist!");
                            Toast toast = Toast.makeText(getApplicationContext(), "User does not exist, please make an account", Toast.LENGTH_SHORT);
                            toast.show();

                        }
                    } else {
                        Log.d(TAG, "Failed with: ", task.getException());
                    }
                }
            });
        }

    }
}

