/**
 * RequestPageFragment
 * Users are brought to this Fragment once they click on an available book
 * From here, the user can view the details of a book and
 * request the book if desired.
 */
package com.example.booktracker.ui;

import android.app.Notification;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.booktracker.Book;
import com.example.booktracker.NotificationMessage;
import com.example.booktracker.MainActivity;
import com.example.booktracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.booktracker.App.CHANNEL_1_ID;

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

    public RequestPageFragment() {
        // Required empty public constructor
    }

    public static BookPageFragment newInstance() {
        BookPageFragment fragment = new BookPageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_request_page, container, false);
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
        if(img != null && !img.isEmpty()){
            //Set image for the book
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
        if(!status.equals("available") || MainActivity.current_user.equals(owner)){
            requestBtn.setVisibility(View.GONE);
        }
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
                                    //Checks if user has already requested this book
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData().get("book"));
                                            Map<String, Object> book = (Map<String, Object>) document.getData().get("book");
                                            String requestStatus = (String) book.get("requestStatus");
                                            String current_isbn = (String)book.get("isbn");
                                            if (current_isbn.equals(isbn) && (requestStatus.equals("Accepted")||requestStatus.equals("Pending Request"))){
                                                isAccepted = true;
                                            }
                                        }
                                }

                                if (!isAccepted){
                                    //If user has not already requested this book and this book is not accepted
                                    Map<String, Book> book = new HashMap<>();
                                    Book newBook = new Book(title, author, isbn, status, owner);
                                    newBook.setRequestStatus("Pending Request");
                                    newBook.setRequestDate(new Date());
                                    book.put("book",newBook);

                                    Map<String, NotificationMessage>  notification = new HashMap<>();
                                    Date newDate = new Date();
                                    NotificationMessage newNotificationMessage = new NotificationMessage("New Book Request", "New Request Has Been Received For\n"+title+"\n"+"isbn: "+isbn, newDate.toString());
                                    notification.put("notification", newNotificationMessage);
                                    db = FirebaseFirestore.getInstance();
                                    //Adds book to requester's Requested Books collection
                                    db.collection("Users").document(MainActivity.current_user)
                                            .collection("Requested Books")
                                            .document(isbn).set(book);
                                    //Gives a notification to the owner to notify them that a book has been requested
                                    db.collection("Users").document(owner)
                                            .collection("Notifications")
                                            .document(newDate.toString()).set(notification);
                                }
                            }
                        });
                Toast.makeText(getContext(), "Requested", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}


