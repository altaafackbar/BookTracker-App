package com.example.booktracker.ui.add;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.booktracker.Book;
import com.example.booktracker.FetchBook;
import com.example.booktracker.MainActivity;
import com.example.booktracker.R;
import com.example.booktracker.ScanBarcodeActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddFragment extends Fragment {
    private AddViewModel addViewModel;
    private Button addBook;
    private EditText author;
    private EditText title;
    private  EditText isbn;
    private FirebaseFirestore db;
    private ImageView scanButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addViewModel =
                ViewModelProviders.of(this).get(AddViewModel.class);
       View root = inflater.inflate(R.layout.fragment_add, container,false);
        final TextView textView = root.findViewById(R.id.titleText);
        addViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        author = root.findViewById(R.id.authorText);
        title = root.findViewById(R.id.titleText);
        isbn = root.findViewById(R.id.isbnText);
        addBook = root.findViewById(R.id.add_button);
        scanButton = root.findViewById(R.id.imageViewScan);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScanBarcodeActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewBook();
            }
        });

        return root;
    }
    public void addNewBook(){
        String authorS = author.getText().toString();
        String titleS = title.getText().toString();
        int isbnS = Integer.parseInt(isbn.getText().toString());
        Map<String, Book> book = new HashMap<>();
        book.put("book", new Book(titleS, authorS, isbnS,false));
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(MainActivity.current_user)
                .collection("Books")
                .document(String.valueOf(isbnS)).set(book);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==0) {
            if (resultCode== CommonStatusCodes.SUCCESS) {
                if(data!=null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    String queryString = barcode.displayValue;
                    isbn.setText(barcode.displayValue);
                    new FetchBook(title, author, isbn).execute(queryString);


                }
                else {
                    isbn.setText("Barcode not found");
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
