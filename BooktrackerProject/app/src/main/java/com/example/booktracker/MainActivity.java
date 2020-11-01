package com.example.booktracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG = "MainActivity";
    private Button signInButton;
    private Button create_acc_btn;
    private int RC_SIGN_IN = 1;
    private FirebaseFirestore db;
    public static String current_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        signInButton = findViewById(R.id.sign_in);
        create_acc_btn = findViewById(R.id.create_acc);

/*
// Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
// Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

 */
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
        Intent new_user = new Intent(this, CreateAccount.class);
        new_user.setFlags(new_user.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
        startActivity(new_user);
    }
    private void signOut(){
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }
    private void signIn() {
        /*
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

         */
        EditText email = findViewById(R.id.email2);
        EditText pass = findViewById(R.id.password);
        final String email_s = email.getText().toString();
        final String passw = pass.getText().toString();
        final HashMap<String, String> data = new HashMap<>();
        data.put("UserEmail", email_s);
        data.put("UserPass", passw);
        db = FirebaseFirestore.getInstance();
        final DocumentReference docIdRef = db.collection("Users").document(email_s);
        final Intent sign_in = new Intent(this, MainScreen.class);
        sign_in.setFlags(sign_in.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
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
                            current_user = document.getData().get("UserPass").toString();
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
                            //Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
    public void updateUI(FirebaseUser user){
        if (user != null) {
            Log.d(TAG, "updateUI: good user");
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            final String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

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
                                final CollectionReference collectionReference = db.collection("Users");
                                db.collection("Users").document(uid).set(data);

                                Log.d(TAG, "userid:" + idToken);
                            } else {
                                // Handle error -> task.getException();
                            }
                        }
                    });

            Log.d(TAG, "user name:" + name);
            Log.d(TAG, "user email:" + email);

            Intent intent = new Intent(this, MainScreen.class);
            //intent.putExtra("name", name);
            //intent.putExtra("email", email);
            startActivity(intent);
        }else{
            Log.d(TAG, "updateUI: null user");
        }



    }


    //TextView descriptionBox = (TextView) (findViewById(R.id.descriptionTV));
    //TextView ownerBox = (TextView) (findViewById(R.id.ownerTV));
    //TextView statusBox = (TextView) (findViewById(R.id.statusTV));

    public void searchKeyword() {
        //get keyword from editText
        //iterate through list of books whose description contains the word with

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

