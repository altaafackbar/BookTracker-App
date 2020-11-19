package com.example.booktracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.booktracker.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class BookSearch extends AppCompatActivity {
    private String searchTerm;
    private TextView result;
    private String title;
    private String author;
    private String isbn;
    private String status;
    private String bookImg;
    private String owner;
    RecyclerView myRecyclerview;
    ArrayList<Book> bookList;
    private FirebaseFirestore db;
    private AvailableRecyclerViewAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        result = findViewById(R.id.resultCount);
        if(bundle!=null)
        {
            searchTerm =(String) bundle.get("searchTerm");
            result.setText("X results found for " + searchTerm);
        }
        bookList = new ArrayList<>();
        myRecyclerview = (RecyclerView) findViewById(R.id.bookSearch_recycler_view_id);
        myAdapter = new AvailableRecyclerViewAdapter(this, bookList);
        myRecyclerview.setLayoutManager(new GridLayoutManager(this, 3 ));
        myRecyclerview.setAdapter(myAdapter);
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
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
                                                isbn = document.getId();
                                                title = (String) book.get("title");
                                                author = (String) book.get("author");
                                                //if search term matches any of author, title, or isbn, add them to list
                                                if(author.contains(searchTerm) || title.contains(searchTerm) || isbn.contains(searchTerm)){
                                                    if (status.equals("available") || status.equals("requested")) {
                                                        bookImg = (String) book.get("image");
                                                        owner = (String)book.get("owner");
                                                        Book newBook = new Book(title, author, isbn, status, owner);
                                                        newBook.setImage(bookImg);
                                                        bookList.add(newBook);
                                                    }
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

    }

}