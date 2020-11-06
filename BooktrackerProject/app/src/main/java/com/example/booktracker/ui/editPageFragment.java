package com.example.booktracker.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.booktracker.MainActivity;
import com.example.booktracker.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link editPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class editPageFragment extends Fragment {
    private EditText author1;
    private EditText title1;
    private  EditText isbn1;
    private String currentIsbn;
    private FirebaseFirestore db;
    private Button addEditedbook;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public editPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment editPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static editPageFragment newInstance(String param1, String param2) {
        editPageFragment fragment = new editPageFragment();
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
        }

        author1 = view.findViewById(R.id.edit_authorText);
        title1 = view.findViewById(R.id.edit_titleText);
        isbn1 = view.findViewById(R.id.edit_isbnText);

        addEditedbook = view.findViewById(R.id.edit_add_button);
        addEditedbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String authorz = author1.getText().toString();
                String titlez = title1.getText().toString();
                String isbnz = isbn1.getText().toString();
                db = FirebaseFirestore.getInstance();
                db.collection("Users").document(MainActivity.current_user)
                        .collection("Books")
                        .document(currentIsbn)
                        .update(
                                "book.title", titlez,
                                "book.author", authorz,
                                "book.isbn", isbnz
                        );
           }
        });


        return view;
    }
}