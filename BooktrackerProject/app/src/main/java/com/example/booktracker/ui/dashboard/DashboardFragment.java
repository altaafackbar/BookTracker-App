package com.example.booktracker.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booktracker.Book;
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
    private String bookImg;
    RecyclerView myRecyclerview;
    ArrayList<Book> bookList;
    private FirebaseFirestore db;


    private DashboardViewModel dashboardViewModel;
    Button returnButton;
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
        final RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(getActivity(), bookList);
        myRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3 ));
        myRecyclerview.setAdapter(myAdapter);


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
                                newBook.setImage(bookImg);
                                bookList.add(newBook);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        myAdapter.notifyDataSetChanged();
                    }
                });

//        tempListView = (ListView)root.findViewById(R.id.book_list);
//        final ArrayList<String> tempArray = new ArrayList<>();
//        tempArray.add("Book1");
//        tempArray.add("Book2");
//        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,tempArray);
//        tempListView.setAdapter(arrayAdapter);
//
//        tempListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(),tempArray.get(position), Toast.LENGTH_SHORT).show();
//                Navigation.findNavController(root).navigate(R.id.dashboard_to_bookPageFragment);
//            }
//        });
////
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(root).navigate(R.id.dashboard_to_bookPageFragment);
//            }
//        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScanBarcodeActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==0) {
            if (resultCode== CommonStatusCodes.SUCCESS) {
                if(data!=null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    System.out.println(barcode.displayValue);
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
