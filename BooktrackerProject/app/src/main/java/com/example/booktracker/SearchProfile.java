/**
*SearchProfile
*Activity that displays the profile of a user and a list of their books
*as a result of searching.
 */
package com.example.booktracker;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booktracker.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class SearchProfile extends AppCompatActivity {
    private String title;
    private String author;
    private String isbn;
    private String status;
    private String bookImg;
    private TextView username;
    RecyclerView profileRecyclerview;
    ArrayList<Book> profileBookList;
    private FirebaseFirestore db;
    private BookRecyclerViewAdapter profileAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_profile);


        username = findViewById(R.id.viewProfile_username);
        username.setText(HomeFragment.searchUser);

        profileBookList = new ArrayList<>();
        profileRecyclerview = findViewById(R.id.viewProfile_recyclerview_id);
        profileAdapter = new BookRecyclerViewAdapter (this,profileBookList);
        profileRecyclerview.setLayoutManager(new GridLayoutManager(this,3));
        profileRecyclerview.setAdapter(profileAdapter);
        db = FirebaseFirestore.getInstance();
        //Retrieving the user's books from database
        db.collection("Users").document(HomeFragment.searchUser)
                .collection("Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData().get("book"));
                                Map<String, Object> book = (Map<String, Object>) document.getData().get("book");
                                isbn = document.getId();
                                title = (String) book.get("title");
                                author = (String) book.get("author");
                                status = (String)book.get("status");
                                bookImg = (String) book.get("image");
                                Book newBook = new Book(title, author, isbn,status, HomeFragment.searchUser);
                                newBook.setImage(bookImg);
                                profileBookList.add(newBook);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        profileAdapter.notifyDataSetChanged();
                    }
                });

    }
}
