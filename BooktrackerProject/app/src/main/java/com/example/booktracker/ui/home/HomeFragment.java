package com.example.booktracker.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.booktracker.MainActivity;
import com.example.booktracker.MainScreen;
import com.example.booktracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FirebaseFirestore db;
    private EditText searchText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView email = root.findViewById(R.id.email);
        /*
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

         */
        searchText = root.findViewById(R.id.userSearch);
        Button sign_out_button = root.findViewById(R.id.sign_out_button2);
        sign_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        email.setText("Welcome back " + MainActivity.current_user);

        Button search = root.findViewById(R.id.searchUserButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchUser();
            }
        });




        return root;
    }
    public void searchUser(){
        db = FirebaseFirestore.getInstance();
        String searchTerm = searchText.getText().toString();
        DocumentReference docIdRef = db.collection("Users").document(searchTerm);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "Document exists!");
                        Toast toast = Toast.makeText(getContext(), "Username exists", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Log.d("TAG", "Document does not exist!");
                        Toast toast = Toast.makeText(getContext(), "Username does not exist", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    Log.d("TAG", "Failed with: ", task.getException());
                }
            }
        });
    }

    private void signOut(){
        MainActivity.current_user = null;
        this.getActivity().finish();
    }
}