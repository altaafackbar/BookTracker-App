/**
 * BookPageFragment
 * This fragment allows owners to view the details on their selected book
 * Owners can Edit and Delete the selected book if it is available
 * Owners can track the book to see who has requested this book
 */
package com.example.booktracker.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

import com.example.booktracker.MainActivity;
import com.example.booktracker.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookPageFragment extends Fragment {
    private String title;
    private String author;
    private String status;
    private String borrower;
    private String isbn;
    private String img;
    private Bitmap bitmap;
    private FirebaseFirestore db;

    public BookPageFragment() {
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
        if (getArguments() != null) {
        }
    }

    /**
     * Sets up the layout for the BookPageFragment
     * Sets up the function of the buttons
     * Also determines if certain buttons should be visible based on book status
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_book_page, container, false);
        final Button track_button = view.findViewById(R.id.track_button);
        track_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Brings the user to the track page for the book where they can see requesters of the book
                Bundle args = new Bundle();
                args.putString("isbn", isbn);
                args.putString("title",title);
                Navigation.findNavController(view).navigate(R.id.bookPageFragment_to_acceptRequestFragment,args);
            }
        });



        final ImageView popup = view.findViewById(R.id.viewImage);
        popup.setVisibility(View.INVISIBLE);
        if (getArguments() != null){
            title = getArguments().getString("title");
            author = getArguments().getString("author");
            status = getArguments().getString("status");
            isbn = getArguments().getString("isbn");
            img = getArguments().getString("img");
            borrower = getArguments().getString("borrower");
        }

        final ImageView bookCover = view.findViewById(R.id.book_cover);
        final ImageButton deleteButton = view.findViewById(R.id.deleteImage);
        final Drawable resImg = ResourcesCompat.getDrawable(getResources(), R.drawable.image_needed, null);
        //if image exists, set the book cover to user image
        if(img != null && !img.isEmpty()){
            Log.d(TAG, "onBindViewHolder: pic exists in fragment");
            byte [] encodeByte= Base64.decode(img, Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            bookCover.setImageBitmap(bitmap);
        }
        TextView titleView = view.findViewById(R.id.textView_title);
        titleView.setText(title);
        TextView authorView = view.findViewById(R.id.author_id);
        authorView.setText(author);
        TextView statusView = view.findViewById(R.id.textView_status);
        if (status.equals("Borrowed")){
            statusView.setText("Status: Borrowed By "+ borrower);
        }else {
            statusView.setText("Status: " + status);
        }
        TextView isbnView = view.findViewById(R.id.textView_isbn);
        isbnView.setText("ISBN: " + isbn);
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
        //deletes image from database
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteImage();
                bookCover.setImageDrawable(resImg);
                deleteButton.setVisibility(View.INVISIBLE);
            }
        });
        final Button deleteBook = view.findViewById(R.id.deleteBook_button);
        deleteBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Deletes the book in the database for the user
               db = FirebaseFirestore.getInstance();
               db.collection("Users").document(MainActivity.current_user).collection("Books")
                       .document(isbn)
                       .delete()
                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Log.d(TAG, "DocumentSnapshot successfully deleted!");
                           }
                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Log.w(TAG, "Error deleting document", e);
                           }
                       });
                Navigation.findNavController(view).navigate(R.id.navigation_dashboard);
                Toast toast = Toast.makeText(getContext(), "Book Successfully Deleted", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        final Button edit = view.findViewById(R.id.edit_button);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Brings user to a Fragment where they can edit the book
                Bundle args = new Bundle();
                args.putString("editisbn", isbn);
                args.putString("edittitle", title);
                args.putString("editauthor", author);
                if(img != null && !img.isEmpty()){
                    args.putString("editImg", img);
                }
                else{
                    args.putString("editImg", "");
                }
                db = FirebaseFirestore.getInstance();
                db.collection("Users").document(MainActivity.current_user).collection("Books")
                        .document(isbn)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
                Toast toast = Toast.makeText(getContext(), "Book is being edited", Toast.LENGTH_SHORT);
                toast.show();
                Navigation.findNavController(view).navigate(R.id.bookPageFragment_to_editPageFragment,args);
            }
        });
        if(!status.equals("available")){
            //Don't allow editing/deleting of books when book is not available
            edit.setVisibility(View.INVISIBLE);
            deleteBook.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
        }
        if(status.equals("Borrowed")|| status.equals("Returned (Pending)")){
            //Don't allow tracking requests of books already borrowed
            track_button.setVisibility(View.INVISIBLE);
        }
        return view;

    }

    /**
     * Deletes the image of the selected book
     */
    public void deleteImage(){
        Log.d(TAG, "deleteImage: " + isbn);
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(MainActivity.current_user).collection("Books")
                .document(isbn).update("book.image", "");
    }




}
