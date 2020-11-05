package com.example.booktracker.ui.add;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class AddFragment extends Fragment {
    private static final int GET_FROM_GALLERY = 1;
    private AddViewModel addViewModel;
    private Button addBook;
    private EditText author;
    private EditText title;
    private  EditText isbn;
    private FirebaseFirestore db;
    private ImageView scanButton;
    private byte[] imageInfo;
    private ImageView imgV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addViewModel =
                ViewModelProviders.of(this).get(AddViewModel.class);
       View root = inflater.inflate(R.layout.fragment_add, container,false);
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

        ImageButton image = root.findViewById(R.id.addImage);
        imgV = root.findViewById(R.id.imageView);

        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
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
                author.setText("");
                title.setText("");
                isbn.setText("");
                Drawable resImg = ResourcesCompat.getDrawable(getResources(), R.drawable.image_needed, null);
                imgV.setImageDrawable(resImg);
            }
        });

        return root;
    }
    public void addNewBook(){
        String authorS = author.getText().toString();
        String titleS = title.getText().toString();
        String isbnS = isbn.getText().toString();
        String owner = MainActivity.current_user;
        String status = "available";
        Map<String, Book> book = new HashMap<>();
        Book bookObj = new Book(titleS, authorS, isbnS, status, owner);
        String imgString = null;
        if(imageInfo != null  && imageInfo.length > 0){
            Log.d(TAG, "addNewBook: book image is empty");
            imgString = Base64.encodeToString(imageInfo, Base64.DEFAULT);
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
        else if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), selectedImage);
                imgV.setImageBitmap(bitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
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
