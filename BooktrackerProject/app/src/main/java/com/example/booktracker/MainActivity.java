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
    private FirebaseAuth mAuth;
    private String TAG = "MainActivity";
    private Button signInButton;
    private Button create_acc_btn;
    private int RC_SIGN_IN = 1;
    private FirebaseFirestore db;
    public static String current_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Sets up buttons that handles account creation and sign in
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
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
    private void create_account(){
        //Calls CreateAccount class to create a new user account
        Intent new_user = new Intent(this, CreateAccount.class);
        new_user.setFlags(new_user.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
        startActivity(new_user);
    }
    private void signIn() {
        /*
        *Attempts to sign user in using the email and pass entered
        * Checks Firestore to ensure user has been registered
         */
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    public void updateUI(FirebaseUser user){
        if (user != null) {
            Log.d(TAG, "updateUI: good user");
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            final String email = user.getEmail();


            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            final String uid = user.getUid();
            user.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                final String idToken = task.getResult().getToken();
                                // Send token to your backend via HTTPS
                                // ...
                                final HashMap<String, String> data = new HashMap<>();
                                data.put("UserEmail", email);
                                db = FirebaseFirestore.getInstance();
                                db.collection("Users").document(uid).set(data);

                                Log.d(TAG, "userid:" + idToken);
                            } else {
                                task.getException();
                            }
                        }
                    });

            Log.d(TAG, "user name:" + name);
            Log.d(TAG, "user email:" + email);

            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
        }else{
            Log.d(TAG, "updateUI: null user");
        }



    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "onStart: signing in");
        updateUI(currentUser);
    }

}

