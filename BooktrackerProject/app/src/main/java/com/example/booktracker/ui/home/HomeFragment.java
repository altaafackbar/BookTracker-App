/**
*HomeFragment
* Sets up the layout and functions of the home screen
* Allows users to search for books, and other users
* Displays a list of available books
* Allows user to sign out
 */
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booktracker.AvailableRecyclerViewAdapter;
import com.example.booktracker.Book;
import com.example.booktracker.BookSearchActivity;
import com.example.booktracker.MainActivity;
import com.example.booktracker.R;
import com.example.booktracker.ScanBarcodeActivity;
import com.example.booktracker.SearchProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FirebaseFirestore db;
    private EditText searchText;
    private EditText bookSearchText;
    private String owner;
    public static String searchUser;
    private String status;
    private String title;
    private String author;
    private String isbn;
    private String bookImg;
    RecyclerView myRecyclerview;
    ArrayList<Book> bookList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView email = root.findViewById(R.id.email);
        searchText = root.findViewById(R.id.userSearch);
        Button sign_out_button = root.findViewById(R.id.sign_out_button2);
        Button acceptButton = root.findViewById(R.id.acceptButton);
        sign_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        email.setText("Welcome Back \n" + MainActivity.current_user);


        Button search = root.findViewById(R.id.searchUserButton);
        search.setOnClickListener(new View.OnClickListener() {
            //Searches for the user entered
            @Override
            public void onClick(View view) { searchUser(); }
        });


        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScanBarcodeActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        bookList = new ArrayList<>();
        myRecyclerview = root.findViewById(R.id.recyclerview_id_home);
        final AvailableRecyclerViewAdapter myAdapter = new AvailableRecyclerViewAdapter(getActivity(), bookList);
        myRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3 ));
        myRecyclerview.setAdapter(myAdapter);
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                //Add all available books in the database to bookList.
                bookList.clear();
                ArrayList<String> userEmailList = new ArrayList<>();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {

                    userEmailList.add((String) doc.getData().get("UserEmail"));
                }
                for(String uEmail:userEmailList){
                    if (!uEmail.equals(MainActivity.current_user)) {
                        db.collection("Users").document(uEmail)
                                .collection("Books")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d(TAG, document.getId() + " => " + document.getData().get("book"));
                                                Map<String, Object> book = (Map<String, Object>) document.getData().get("book");
                                                status = (String) book.get("status");
                                                if (status.equals("available")) {
                                                    isbn = document.getId();
                                                    title = (String) book.get("title");
                                                    author = (String) book.get("author");
                                                    bookImg = (String) book.get("image");
                                                    owner = (String)book.get("owner");
                                                    Book newBook = new Book(title, author, isbn, status, owner);
                                                    newBook.setImage(bookImg);
                                                    bookList.add(newBook);
                                                }
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                        myAdapter.notifyDataSetChanged();
                                    }
                                });
                    }
                }
            }
        });


        final Button BookSearchButton = root.findViewById(R.id.bookSearch);
        bookSearchText = root.findViewById(R.id.bookText);
        BookSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Searches and displays books with descriptions matching the entered terms
                String searchTerm = bookSearchText.getText().toString();
                Intent intent = new Intent(getActivity(), BookSearchActivity.class);
                intent.putExtra("searchTerm", searchTerm);
                startActivity(intent);
            }
        });

        return root;
    }



   public void searchUser(){
        /*
        *Searches for an user according to the term entered
        *Displays the user profile if search exists,
        *otherwise give the appropriate error messages.
         */
       db = FirebaseFirestore.getInstance();
       String searchTerm = searchText.getText().toString();
       if(!searchTerm.isEmpty()){
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
                           //Call to SearchProfile Activity
                           Intent intent = new Intent(getActivity(), SearchProfile.class);
                           startActivity(intent);
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
       else{
           Toast toast = Toast.makeText(getContext(), "Please enter a search term", Toast.LENGTH_SHORT);
           toast.show();
       }

    }


    private void signOut(){
        //Signs an user out
        MainActivity.current_user = null;
        this.getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        db = FirebaseFirestore.getInstance();
        if (requestCode == 1) {
            Toast toast1 = Toast.makeText(getContext(), "request1", Toast.LENGTH_SHORT);
            toast1.show();
            final Barcode barcode = data.getParcelableExtra("barcode");
            db = FirebaseFirestore.getInstance();
            db.collection("Users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => ");
                                    db.collection("Users").document(document.getId())
                                            .collection("Books")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                                    if (task1.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                                            Map<String, Object> book1 = (Map<String, Object>) document1.getData().get("book");
                                                            title = (String) book1.get("title");
                                                            Log.d(TAG, document.getId() + " ISBN:" + document1.getId() + " ==> " + title);
                                                            if (barcode.displayValue.equals(document1.getId())) {
                                                                db.collection("Users").document(document.getId())
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
                                                                                        Toast toast21 = Toast.makeText(getContext(), owner + status, Toast.LENGTH_SHORT);
                                                                                        toast21.show();
                                                                                        //Requester accepts and accepted book
                                                                                        if (status!= null && status.equals("borrowed(pending)")) {
                                                                                            //Change status to borrowed
                                                                                            db.collection("Users").document(owner).collection("Books")
                                                                                                    .document(barcode.displayValue).update("book.status", "borrowed");
                                                                                            Toast toast1 = Toast.makeText(getContext(), "Successfully accepted and borrowed!", Toast.LENGTH_SHORT);
                                                                                            toast1.show();
                                                                                        }
                                                                                        //Owner accepts a returned book
                                                                                        else if (owner.equals(MainActivity.current_user) && status.equals("available(pending)")) {
                                                                                            db.collection("Users").document(owner).collection("Books")
                                                                                                    .document(barcode.displayValue).update("book.status", "available");
                                                                                            Toast toast1 = Toast.makeText(getContext(), "Successfully accepted and returned!", Toast.LENGTH_SHORT);
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
                                            });
                                }
                            }
                        }
                    });
        }
    }
}