package com.example.booktracker.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booktracker.Book;
import com.example.booktracker.CreateAccount;
import com.example.booktracker.MainActivity;
import com.example.booktracker.R;
import com.example.booktracker.RecyclerViewAdapter;
import com.example.booktracker.ScanBarcodeActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class DashboardFragment extends Fragment {
    private String title;
    private String author;
    private String isbn;
    private String status;
    RecyclerViewAdapter myAdapter;
    private String bookImg;
    RecyclerView myRecyclerview;
    ArrayList<Book> bookList;
    private FirebaseFirestore db;


    private DashboardViewModel dashboardViewModel;
    Button returnButton;
    Button filterAllBtn;
    Button filterAvailableBtn;
    Button filterAcceptedBtn;
    Button filterBorrowedBtn;
    ListView tempListView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.username);
        returnButton = root.findViewById(R.id.returnButton);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        bookList = new ArrayList<>();
        myRecyclerview = (RecyclerView) root.findViewById(R.id.recyclerview_id);
        myAdapter = new RecyclerViewAdapter(getActivity(), bookList);
        myRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3 ));
        myRecyclerview.setAdapter(myAdapter);
        filterAllBtn = root.findViewById(R.id.filter_all_btn);
        filterAvailableBtn = root.findViewById(R.id.filter_available_btn);
        filterAcceptedBtn = root.findViewById(R.id.filter_accepted_btn);
        filterBorrowedBtn = root.findViewById(R.id.filter_borrowed_btn);
        filter("All");

        myAdapter.notifyDataSetChanged();

        filterAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter("All");
                filterAllBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
                filterAvailableBtn.setBackgroundColor(Color.parseColor("#C0C0C0"));
                filterAcceptedBtn.setBackgroundColor(Color.parseColor("#C0C0C0"));
                filterBorrowedBtn.setBackgroundColor(Color.parseColor("#C0C0C0"));

            }
        });
        filterAvailableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter("available");
                filterAllBtn.setBackgroundColor(Color.parseColor("#C0C0C0"));
                filterAvailableBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
                filterAcceptedBtn.setBackgroundColor(Color.parseColor("#C0C0C0"));
                filterBorrowedBtn.setBackgroundColor(Color.parseColor("#C0C0C0"));

            }
        });
        filterAcceptedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter("Accepted");
                filterAllBtn.setBackgroundColor(Color.parseColor("#C0C0C0"));
                filterAvailableBtn.setBackgroundColor(Color.parseColor("#C0C0C0"));
                filterAcceptedBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
                filterBorrowedBtn.setBackgroundColor(Color.parseColor("#C0C0C0"));

            }
        });
        filterBorrowedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter("Borrowed");
                filterAllBtn.setBackgroundColor(Color.parseColor("#C0C0C0"));
                filterAvailableBtn.setBackgroundColor(Color.parseColor("#C0C0C0"));
                filterAcceptedBtn.setBackgroundColor(Color.parseColor("#C0C0C0"));
                filterBorrowedBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));

            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScanBarcodeActivity.class);
                startActivityForResult(intent, 0);

            }
        });
        Button editProfile = root.findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateAccount.class);
                intent.putExtra("task", "edit");
                startActivity(intent);
            }
        });
        return root;
    }
    private void filter(final String filterStatus){
        bookList.clear();
        db = FirebaseFirestore.getInstance();
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
                                isbn = document.getId();
                                title = (String) book.get("title");
                                author = (String) book.get("author");
                                status = (String)book.get("status");
                                bookImg = (String) book.get("image");
                                Book newBook = new Book(title, author, isbn,status, MainActivity.current_user);
                                newBook.setRequester((String)book.get("requester"));
                                newBook.setImage(bookImg);
                                if (filterStatus.equals("All")){
                                    bookList.add(newBook);
                                }else if (filterStatus.equals(status)){
                                    bookList.add(newBook);
                                }
                                myAdapter.notifyDataSetChanged();

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==0) {
            if (resultCode== CommonStatusCodes.SUCCESS) {
                if(data!=null) {
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
                                                                        db.collection("Users").document(document.getId()).collection("Books")
                                                                                .document(barcode.displayValue).update("book.status", "available(pending)");
                                                                        Toast toast1 = Toast.makeText(getContext(), "Successfully Returned!!", Toast.LENGTH_SHORT);
                                                                        toast1.show();
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
                else {
                    System.out.println("No barcode found");
                }
            }
        }
        else {

            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
