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
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
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

import com.example.booktracker.MainActivity;
import com.example.booktracker.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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
    private String img1;
    private ImageView imgv1;
    private Bitmap bitmap;
    private String currentIsbn;
    private String originalTitle;
    private String originalAuthor;
    private String originalImg;
    private FirebaseFirestore db;
    private Button addEditedbook;
    private byte[] imageInfo;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_edit_page, container, false);
        final ImageView imageView = view.findViewById(R.id.edit_backArrow);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.editPageFragment_to_bookPageFragment);
            }
        });

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
        if(img1 != null && !img1.isEmpty()){
            //load in the image
            Log.d(TAG, "onBindViewHolder: pic exists in fragment");
            byte [] encodeByte= Base64.decode(img1, Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            imgv1.setImageBitmap(bitmap);
        }
        author1.setText(originalAuthor);
        title1.setText(originalTitle);
        isbn1.setText(currentIsbn);
        ImageButton editImage = view.findViewById(R.id.editImageButton);
        editImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Allow user to pick an image from gallery
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
        addEditedbook = view.findViewById(R.id.edit_add_button);
        addEditedbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Updates the fields of the edited book in the database
                String authorz = author1.getText().toString();
                String titlez = title1.getText().toString();
                String isbnz = isbn1.getText().toString();
                String imgString = null;
                if(imageInfo != null  && imageInfo.length > 0){
                    Log.d(TAG, "addNewBook: book image is empty");
                    imgString = Base64.encodeToString(imageInfo, Base64.DEFAULT);
                }
                else{
                    imgString = "";
                }
                db = FirebaseFirestore.getInstance();
                db.collection("Users").document(MainActivity.current_user)
                        .collection("Books")
                        .document(currentIsbn)
                        .update(
                                "book.title", titlez,
                                "book.author", authorz,
                                "book.isbn", isbnz,
                                "book.image", imgString
                        );
                Toast toast = Toast.makeText(getContext(), "Book Successfully Edited", Toast.LENGTH_SHORT);
                toast.show();
                Navigation.findNavController(view).navigate(R.id.navigation_dashboard);
           }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Activity to retrieve image from gallery
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), selectedImage);
                imgv1.setImageBitmap(bitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
                imageInfo = stream.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}