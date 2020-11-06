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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.booktracker.MainActivity;
import com.example.booktracker.R;
import com.example.booktracker.ScanBarcodeActivity;
import com.example.booktracker.ViewProfileFragment;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FirebaseFirestore db;
    private EditText searchText;
    private String owner;
    public static String searchUser;
    private String status;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
         final View root = inflater.inflate(R.layout.fragment_home, container, false);
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
        Button lendButton = root.findViewById(R.id.LendButton);
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
            public void onClick(View view) { searchUser(); }
        });

        lendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScanBarcodeActivity.class);
                startActivityForResult(intent, 0);
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
                       searchUser = searchText.getText().toString();


                       //Call to ViewProfileFragment
                       ViewProfileFragment nextFrag= new ViewProfileFragment();
                       getActivity().getSupportFragmentManager().beginTransaction()
                               .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                               .addToBackStack(null)
                               .commit();


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        db = FirebaseFirestore.getInstance();
        if (requestCode == 0) { //If the owner wants to lend a book.
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    final Barcode barcode = data.getParcelableExtra("barcode"); //Contains barcode
                    db.collection("Users").document(MainActivity.current_user)
                            .collection("Books")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData().get("book"));
                                            Map<String, Object> book = (Map<String, Object>) document.getData().get("book");
                                                //Update as borrowed
                                                owner = (String) book.get("owner");
                                                status = (String) book.get("status");
                                            //Check if the Owner is the one lending the book.
                                            if (owner != null && owner.equals(MainActivity.current_user) && status!= null && status.equals("available")) {
                                                    //Change status to borrowed
                                                    db.collection("Users").document(MainActivity.current_user).collection("Books")
                                                            .document(barcode.displayValue).update("book.status", "borrowed");
                                                    Toast toast1 = Toast.makeText(getContext(), "Successfully lent!", Toast.LENGTH_SHORT);
                                                    toast1.show();
                                                }
                                            //If owner is accepting a returned book
                                            else if (owner != null && owner.equals(MainActivity.current_user) && status!= null && status.equals("available(pending)")) {
                                                db.collection("Users").document(MainActivity.current_user).collection("Books")
                                                        .document(barcode.displayValue).update("book.status", "available");
                                                Toast toast1 = Toast.makeText(getContext(), "Successfully Accepted!", Toast.LENGTH_SHORT);
                                                toast1.show();
                                            }
                                        }
                                    }
                                }
                            });
                }
            }
        }
    }
}