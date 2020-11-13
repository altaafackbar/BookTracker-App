package com.example.booktracker.ui;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.booktracker.Book;
import com.example.booktracker.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookBorrowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookBorrowFragment extends Fragment {
    private String title;
    private String author;
    private String isbn;
    private String status;
    private String bookImg;
    private ArrayList<Book> bookList;
    private FirebaseFirestore db;
    private ArrayList<String> userEmailList = new ArrayList<>();
    BorrowListAdapter borrowAdapter;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BookBorrowFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookBorrowFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookBorrowFragment newInstance(String param1, String param2) {
        BookBorrowFragment fragment = new BookBorrowFragment();
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
        View view = inflater.inflate(R.layout.fragment_book_borrow, container, false);
        bookList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");

        ListView listView = (ListView)view.findViewById(R.id.available_borrow_list);
        borrowAdapter = new BorrowListAdapter(getActivity(),R.layout.borrow_list_item, userEmailList);
        listView.setAdapter(borrowAdapter);
        borrowAdapter.notifyDataSetChanged();


        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                bookList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {

                    userEmailList.add((String) doc.getData().get("UserEmail"));
                }
                borrowAdapter.notifyDataSetChanged();
            }
        });




        return view;
    }

    private class BorrowListAdapter extends ArrayAdapter<String>{
        private int layout;
        public BorrowListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewHolder = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout,parent,false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.owner_pic = (ImageView)convertView.findViewById(R.id.borrow_book_owner_pic);
                viewHolder.owner_name = (TextView)convertView.findViewById(R.id.borrow_book_owner_username);
                viewHolder.owner_location = (TextView)convertView.findViewById(R.id.borrow_book_owner_location);
                viewHolder.request_button = (Button)convertView.findViewById(R.id.borrow_request_button);
                viewHolder.request_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Requested", Toast.LENGTH_SHORT).show();
                    }
                });
                convertView.setTag(viewHolder);

                viewHolder.owner_name.setText(getItem(position));
            }
            else{
                mainViewHolder = (ViewHolder) convertView.getTag();
                mainViewHolder.owner_name.setText(getItem(position));


            }
            return convertView;
        }
    }
    public class ViewHolder{
        ImageView owner_pic;
        TextView owner_name;
        TextView owner_location;
        Button request_button;
    }
}