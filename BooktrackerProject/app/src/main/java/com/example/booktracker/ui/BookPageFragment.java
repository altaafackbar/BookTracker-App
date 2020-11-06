package com.example.booktracker.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;

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

import com.example.booktracker.MainActivity;
import com.example.booktracker.R;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookPageFragment extends Fragment {
    public TextView back;
    private String title;
    private String author;
    private String status;
    private String isbn;
    private String img;
    private Bitmap bitmap;
    private FirebaseFirestore db;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BookPageFragment() {
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
        final View view = inflater.inflate(R.layout.fragment_book_page, container, false);
        final TextView textView = view.findViewById(R.id.textView_back);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.bookPageFragment_to_dashboard);
            }
        });
        final Button track_button = view.findViewById(R.id.track_button);
        track_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.bookPageFragment_to_trackPageFragment);
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
        }
        final ImageView bookCover = view.findViewById(R.id.book_cover);
        final ImageButton deleteButton = view.findViewById(R.id.deleteImage);
        final Drawable resImg = ResourcesCompat.getDrawable(getResources(), R.drawable.image_needed, null);
        //if image exists, set the book cover to user image
        if(img != null && !img.isEmpty()){
            Log.d(TAG, "onBindViewHolder: pic exists");
            byte [] encodeByte= Base64.decode(img, Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            bookCover.setImageBitmap(bitmap);
        }
        TextView titleView = view.findViewById(R.id.textView_title);
        titleView.setText(title);
        TextView authorView = view.findViewById(R.id.author_id);
        authorView.setText(author);
        TextView statusView = view.findViewById(R.id.textView_status);
        statusView.setText("Status: "+status);

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

        final Button edit = view.findViewById(R.id.edit_button);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("editisbn", isbn);
                Navigation.findNavController(view).navigate(R.id.bookPageFragment_to_editPageFragment,args);
            }
        });
        return view;

    }
    public void deleteImage(){
        Log.d(TAG, "deleteImage: " + isbn);
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(MainActivity.current_user).collection("Books")
                .document(isbn).update("book.image", "");
    }


}
