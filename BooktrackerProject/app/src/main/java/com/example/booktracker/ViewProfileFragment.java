package com.example.booktracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
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

public class ViewProfileFragment extends Fragment {

    private String title;
    private String author;
    private String isbn;
    private Boolean status;
    private String bookImg;
    RecyclerView profileRecyclerview;
    ArrayList<Book> profileBookList;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View profile = inflater.inflate(R.layout.fragment_view_profile,container,false);
        final Button back = profile.findViewById(R.id.viewProfile_backButton);

        profileBookList = new ArrayList<>();
        profileRecyclerview = (RecyclerView) profile.findViewById(R.id.viewProfile_recyclerview_id);
        final RecyclerViewAdapter profileAdapter = new RecyclerViewAdapter(getActivity(), profileBookList);
        profileRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(),3));
        profileRecyclerview.setAdapter(profileAdapter);

        db = FirebaseFirestore.getInstance();
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
                                //status = (Boolean)book.get("status");
                                //bookImg = (String) book.get("image");
                                profileBookList.add(new Book(title, author, isbn,"false", HomeFragment.searchUser ));

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        profileAdapter.notifyDataSetChanged();
                    }
                });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(profile).navigate(R.id.viewProfileFragment_to_navigation_home);
            }
        });

        /*
        final ListView bookList = profile.findViewById(R.id.viewProfile_recyclerview_id);
        bookList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(profile).navigate(R.id.viewProfileFragment_to_bookPageFragment);
            }
        });
        */

        return profile;


    }
}
