/**
 * EditPageFragment
 * This fragment allows the user to edit one of their books
 * User can change title, isbn, author, and image of the book
 */
package com.example.booktracker.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.booktracker.Book;
import com.example.booktracker.FetchBook;
import com.example.booktracker.MainActivity;
import com.example.booktracker.R;
import com.example.booktracker.ScanBarcodeActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditPageFragment extends Fragment {
    private EditText author1;
    private EditText title1;
    private EditText isbn1;
    private ImageView imgv1;
    private String currentIsbn;
    private String originalTitle;
    private String originalAuthor;
    private String originalImg;
    private FirebaseFirestore db;
    private Button addEditedbook;
    private byte[] imageInfo;
    private ImageView scanButton;
    private static final int GET_FROM_GALLERY = 1;


    public EditPageFragment() {
        // Required empty public constructor

    }


    public static EditPageFragment newInstance() {
        EditPageFragment fragment = new EditPageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    /**
     * Sets up the layout in the EditPageFragment
     * where users can edit descriptions of their book and change
     * pictures
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_edit_page, container, false);

        if (getArguments() != null){
            currentIsbn = getArguments().getString("editisbn");
            originalAuthor = getArguments().getString("editauthor");
            originalTitle= getArguments().getString("edittitle");
            originalImg = getArguments().getString("editImg");

        }
        author1 = view.findViewById(R.id.edit_authorText);
        title1 = view.findViewById(R.id.edit_titleText);
        isbn1 = view.findViewById(R.id.edit_isbnText);
        imgv1 = view.findViewById(R.id.edit_imageView);
        //Log.i(TAG, "originalTitle: " + originalTitle);
        author1.setText(originalAuthor);
        title1.setText(originalTitle);
        isbn1.setText(currentIsbn);
        String imgString = null;
        if(originalImg != null && !originalImg.isEmpty()){
            byte [] encodeByte= Base64.decode(originalImg, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            imgv1.setImageBitmap(bitmap);
        }
        ImageButton editImage = view.findViewById(R.id.editImageButton);
        scanButton = view.findViewById(R.id.edit_imageViewScan);

        editImage.setOnClickListener(new View.OnClickListener() {
            //starts activity to get an image from gallery
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
        scanButton.setOnClickListener(new View.OnClickListener() {
            //starts activity to scan for isbn
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScanBarcodeActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        addEditedbook = view.findViewById(R.id.edit_add_button);
        addEditedbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String authorS = author1.getText().toString();
                String titleS = title1.getText().toString();
                String isbnS = isbn1.getText().toString();
                if(authorS.isEmpty() || titleS.isEmpty() || isbnS.isEmpty()){
                    //If not all information is entered
                    Toast toast = Toast.makeText(getContext(), "Please enter missing information", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    addNewBook(titleS, authorS, isbnS);
                    Drawable resImg = ResourcesCompat.getDrawable(getResources(), R.drawable.image_needed, null);
                    imgv1.setImageDrawable(resImg);
                    Bundle args = new Bundle();
                    args.putString("key", title1.getText().toString());
                    Navigation.findNavController(view).navigate(R.id.navigation_dashboard, args);
                }
            }
        });

        return view;
    }


    /**
     * Attempts to add a new book using information entered
     * @param titleS
     * @param authorS
     * @param isbnS
     */
    public void addNewBook(String titleS, String authorS, String isbnS){
        String owner = MainActivity.current_user;
        String status = "available";
        //Add new book to the user's database if book is valid
        Map<String, Book> book = new HashMap<>();
        Book bookObj = new Book(titleS, authorS, isbnS, status, owner);
        String imgString = null;
        //if a new image is being added
        if(imageInfo != null  && imageInfo.length > 0){
            Log.d(TAG, "addNewBook: book image is empty");
            imgString = Base64.encodeToString(imageInfo, Base64.DEFAULT);
        }
        //if there was an existing image
        else if(originalImg != null && !originalImg.isEmpty()){
            imgString = originalImg;
        }
        else{
            imgString = "";
        }
        bookObj.setImage(imgString);
        book.put("book", bookObj);
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(MainActivity.current_user)
                .collection("Books")
                .document(isbnS).set(book);
        Toast toast = Toast.makeText(getContext(), "Book Successfully Added", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Implements the functions of the barcode scan and picking images from the gallery
     * based on the resultCode passed in
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Implements the result of scanning a barcode as well as selecting a book image from gallery
        if (requestCode==0) {
            //Scanning barcode: attempts to get an barcode and if successful, sets the fields for the new book
            if (resultCode== CommonStatusCodes.SUCCESS) {
                if(data!=null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    String queryString = barcode.displayValue;
                    isbn1.setText(barcode.displayValue);
                    new FetchBook(title1, author1, isbn1).execute(queryString);
                }
                else {
                    isbn1.setText("Barcode not found");
                }
            }
        }
        else if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            //Attempting to get an image from gallery
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), selectedImage);
                imgv1.setImageBitmap(bitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
                imageInfo = stream.toByteArray();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
