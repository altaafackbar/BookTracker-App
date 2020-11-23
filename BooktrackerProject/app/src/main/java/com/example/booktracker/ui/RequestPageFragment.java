package com.example.booktracker.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.booktracker.Book;
import com.example.booktracker.MainActivity;
import com.example.booktracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class RequestPageFragment extends Fragment {
    public TextView back;
    private String title;
    private String author;
    private String status;
    private String isbn;
    private String img;
    private Bitmap bitmap;
    private boolean isAccepted;
    private String owner;
    private FirebaseFirestore db;

    private boolean delete;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RequestPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookPageFragment newInstance(String param1, String param2) {
        BookPageFragment fragment = new BookPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_request_page, container, false);
        final TextView textView = view.findViewById(R.id.textView_back_request);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
                //  Navigation.findNavController(view).navigate(R.id.bookPageFragment_to_dashboard);
            }
        });
        final ImageView popup = view.findViewById(R.id.viewImage_request);
        popup.setVisibility(View.INVISIBLE);
        if (getArguments() != null){
            title = getArguments().getString("title");
            author = getArguments().getString("author");
            status = getArguments().getString("status");
            isbn = getArguments().getString("isbn");
            img = getArguments().getString("img");
            owner = getArguments().getString("owner");
        }
        final ImageView bookCover = view.findViewById(R.id.book_cover_request);
        final Drawable resImg = ResourcesCompat.getDrawable(getResources(), R.drawable.image_needed, null);
        //if image exists, set the book cover to user image
        if(img != null && !img.isEmpty()){
            Log.d(TAG, "onBindViewHolder: pic exists");
            byte [] encodeByte= Base64.decode(img, Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            bookCover.setImageBitmap(bitmap);
        }
        TextView titleView = view.findViewById(R.id.textView_title_request);
        titleView.setText(title);
        TextView ownerView = view.findViewById(R.id.textView_owner_request);
        ownerView.setText("Owner: "+owner);
        TextView authorView = view.findViewById(R.id.author_id_request);
        authorView.setText(author);
        TextView statusView = view.findViewById(R.id.textView_status_request);
        statusView.setText("Status: "+status);

        TextView isbnView = view.findViewById(R.id.textView_isbn_request);
        isbnView.setText("ISBN: " +isbn);
        //if user clicks book cover, blow up image
        bookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!img.isEmpty()){
                    popup.setImageBitmap(bitmap);
                    popup.bringToFront();
                    popup.setVisibility(View.VISIBLE);
                }
            }
        });
        //minimize image again when clicked
        popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.setVisibility(View.INVISIBLE);
            }
        });

        final Button requestBtn = view.findViewById(R.id.requestBook_button);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add book to Requested Books of current user
                isAccepted = false;
                db = FirebaseFirestore.getInstance();
                db.collection("Users")
                        .document(MainActivity.current_user)
                        .collection("Requested Books")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData().get("book"));
                                            Map<String, Object> book = (Map<String, Object>) document.getData().get("book");
                                            String requestStatus = (String) book.get("requestStatus");
                                            String current_isbn = (String)book.get("isbn");
                                            if (current_isbn.equals(isbn) && requestStatus.equals("Accepted")){
                                                isAccepted = true;
                                            }
                                        }
                                }
                                else{
                                    isAccepted = false;
                                }
                                if (!isAccepted){
                                    Map<String, Book> book = new HashMap<>();
                                    Book newBook = new Book(title, author, isbn, status, owner);
                                    newBook.setRequestStatus("Pending Request");
                                    newBook.setRequestDate(new Date());
                                    book.put("book",newBook);
                                    db = FirebaseFirestore.getInstance();
                                    db.collection("Users").document(MainActivity.current_user)
                                            .collection("Requested Books")
                                            .document(isbn).set(book);
                                }
                            }
                        });

                Toast.makeText(getContext(), "Requested", Toast.LENGTH_SHORT).show();

            }
        });


        return view;
    }
}


