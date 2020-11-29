/**
*DashboardFragment
* Implements the functions of the dashboard fragment
* Includes the display and filtering of an user's own books
* Branches to other activities including, fragment_book_page,
* books borrowed page, books lent out page, and edit profile page.
 */
package com.example.booktracker.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booktracker.Book;
import com.example.booktracker.CreateAccount;
import com.example.booktracker.MainActivity;
import com.example.booktracker.R;
import com.example.booktracker.RecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private String filterStatus;
    private RecyclerViewAdapter myAdapter;
    private String bookImg;
    private RecyclerView myRecyclerview;
    private ArrayList<Book> bookList;
    private FirebaseFirestore db;

    private DashboardViewModel dashboardViewModel;
    private Button filterAllBtn;
    private Button filterAvailableBtn;
    private Button filterAcceptedBtn;
    private Button filterBorrowedBtn;
    private Button btnLendPage;
    private Button btnBorrowedPage;

    /**
     * This sets up the layout for the dashboard fragment
     * Sets up on click listeners for buttons in this page
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.username);
        textView.setText(MainActivity.current_user);

        bookList = new ArrayList<>();
        myRecyclerview = root.findViewById(R.id.recyclerview_id);
        myAdapter = new RecyclerViewAdapter(getActivity(), bookList);
        myRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3 ));
        myRecyclerview.setAdapter(myAdapter);
        btnLendPage = root.findViewById(R.id.lent_out_page_btn);
        btnBorrowedPage = root.findViewById(R.id.borrowing_page_btn);
        filterAllBtn = root.findViewById(R.id.filter_all_btn);
        filterAvailableBtn = root.findViewById(R.id.filter_available_btn);
        filterAcceptedBtn = root.findViewById(R.id.filter_accepted_btn);
        filterBorrowedBtn = root.findViewById(R.id.filter_borrowed_btn);
        filterStatus = "All";
        filter();

        myAdapter.notifyDataSetChanged();

        filterAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Shows all books; filters off, changes button color to show which filter is selected
                filterStatus = "All";
                filter();
                filterAllBtn.setTextColor(Color.parseColor("#733BD6"));
                filterAvailableBtn.setTextColor(Color.parseColor("#BF676767"));
                filterAcceptedBtn.setTextColor(Color.parseColor("#BF676767"));
                filterBorrowedBtn.setTextColor(Color.parseColor("#BF676767"));

            }
        });
        filterAvailableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Shows available books, changes button color to show which filter is selected
                filterStatus = "available";
                filter();
                filterAllBtn.setTextColor(Color.parseColor("#BF676767"));
                filterAvailableBtn.setTextColor(Color.parseColor("#733BD6"));
                filterAcceptedBtn.setTextColor(Color.parseColor("#BF676767"));
                filterBorrowedBtn.setTextColor(Color.parseColor("#BF676767"));

            }
        });
        filterAcceptedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Shows accepted books, changes button color to show which filter is selected
                filterStatus = "Accepted";
                filter();
                filterAllBtn.setTextColor(Color.parseColor("#BF676767"));
                filterAvailableBtn.setTextColor(Color.parseColor("#BF676767"));
                filterAcceptedBtn.setTextColor(Color.parseColor("#733BD6"));
                filterBorrowedBtn.setTextColor(Color.parseColor("#BF676767"));

            }
        });
        filterBorrowedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Shows borrowed, changes button color to show which filter is selected
                filterStatus = "Borrowed";
                filter();
                filterAllBtn.setTextColor(Color.parseColor("#BF676767"));
                filterAvailableBtn.setTextColor(Color.parseColor("#BF676767"));
                filterAcceptedBtn.setTextColor(Color.parseColor("#BF676767"));
                filterBorrowedBtn.setTextColor(Color.parseColor("#733BD6"));

            }
        });
        Button editProfile = root.findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Edit button transitions user to edit profile page
                Intent intent = new Intent(getActivity(), CreateAccount.class);
                intent.putExtra("task", "edit");
                startActivity(intent);
            }
        });

        btnLendPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Transitions user to page showing books lent out where owners can scan to confirm returned
                Navigation.findNavController(root).navigate(R.id.dashboard_to_lentOutFragment);
            }
        });
        btnBorrowedPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Transitions user to page showing books borrowed where borrowers can scan to return books
                Navigation.findNavController(root).navigate(R.id.dashboard_to_borrowingFragment);
            }
        });
        return root;
    }

    /**
     * This filters the bookList according to the value of filterStatus
     * Clears the bookList then add books to the bookList from the
     * user's books in the database that matches the filter status.
     */
    private void filter(){
        /*Filters the bookList according to the value of filterStatus
        * Clears the bookList then add books to the bookList from the user's books
        * in the database matching the filter
        */
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
                                if (status.equals("Borrowed (Pending)")){
                                    status = "Accepted";
                                }else if(status.equals("Returned (Pending)")){
                                    status = "Borrowed";
                                }
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

}
