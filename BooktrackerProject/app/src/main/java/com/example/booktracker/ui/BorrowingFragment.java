package com.example.booktracker.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.booktracker.Book;
import com.example.booktracker.MainActivity;
import com.example.booktracker.R;
import com.example.booktracker.ScanBarcodeActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class BorrowingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ArrayList<Book> bookList;
    private FirebaseFirestore db;
    private String title;
    private String author;
    private String isbn;
    private String owner;
    BorrowingListAdapter myAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BorrowingFragment() {
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
    public static BorrowingFragment newInstance(String param1, String param2) {
        BorrowingFragment fragment = new BorrowingFragment();
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
        final View view = inflater.inflate(R.layout.fragment_borrowing_page, container, false);
        bookList = new ArrayList<>();
        ListView listView = (ListView)view.findViewById(R.id.borrowingPageListView);
        myAdapter = new BorrowingListAdapter(getActivity(), R.layout.borrowing_list_item, bookList);
        listView.setAdapter(myAdapter);
        getInfoFromDB();
        return view;

    }


    private class BorrowingListAdapter extends ArrayAdapter<Book> {
        private int layout;
        public BorrowingListAdapter(@NonNull Context context, int resource, @NonNull List<Book> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            BorrowingViewHolder mainViewHolder = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                final BorrowingViewHolder viewHolder = new BorrowingViewHolder();
                viewHolder.title = (TextView)convertView.findViewById(R.id.title_borrowing_item);
                viewHolder.author = (TextView)convertView.findViewById(R.id.author_borrowing_item);
                viewHolder.isbn = (TextView)convertView.findViewById(R.id.isbn_borrowing_item);
                viewHolder.owner = (TextView)convertView.findViewById(R.id.owner_borrowing_item);
                viewHolder.message = (TextView)convertView.findViewById(R.id.confirm_message_borrowing_item);
                viewHolder.scan_return_btn = (Button)convertView.findViewById(R.id.scan_button_borrowing_item);
                viewHolder.scan_return_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isbn = getItem(position).getIsbn();
                        owner = getItem(position).getOwner();
                        Intent intent = new Intent(getActivity(), ScanBarcodeActivity.class);
                        startActivityForResult(intent, 103);
                        getInfoFromDB();
                        myAdapter.notifyDataSetChanged();
                    }
                });
                if (getItem(position).getStatus().equals("Returned (Pending)")){
                    viewHolder.message.setVisibility(View.VISIBLE);
                    viewHolder.scan_return_btn.setVisibility(View.GONE);
                }
                viewHolder.title.setText("Title: "+ getItem(position).getTitle());
                viewHolder.author.setText("Author: "+getItem(position).getAuthor());
                viewHolder.isbn.setText("ISBN: "+getItem(position).getIsbn());
                viewHolder.owner.setText("Owner: "+getItem(position).getOwner());
                convertView.setTag(viewHolder);

            }else{
                mainViewHolder = (BorrowingViewHolder) convertView.getTag();
            }
            return convertView;
        }
    }

    public class BorrowingViewHolder{
        TextView title;
        TextView author;
        TextView isbn;
        TextView owner;
        TextView message;
        Button scan_return_btn;
    }

    private void getInfoFromDB(){
        bookList.clear();
        db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(MainActivity.current_user)
                .collection("Borrowed Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document:task.getResult()){
                                Log.d(TAG, document.getId() + " => " + document.getData().get("book"));
                                Map<String, Object> book = (Map<String, Object>) document.getData().get("book");
                                isbn = document.getId();
                                title = (String) book.get("title");
                                author = (String) book.get("author");
                                owner = (String) book.get("owner");
                                Book newBook = new Book(title, author, isbn, (String)book.get("status"), owner);
                                bookList.add(newBook);
                            }
                            myAdapter.notifyDataSetChanged();
                        }else{
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if (requestCode == 103){
            if (resultCode == CommonStatusCodes.SUCCESS){
                if (data!=null){
                    final Barcode barcode = data.getParcelableExtra("barcode");
                    if (barcode.displayValue.equals(isbn)){
                        db = FirebaseFirestore.getInstance();
                        db.collection("Users")
                                .document(MainActivity.current_user)
                                .collection("Borrowed Books")
                                .document(isbn)
                                .update("book.status","Returned (Pending)");
                        db.collection("Users")
                                .document(owner)
                                .collection("Books Lent Out")
                                .document(isbn)
                                .update("book.status", "Returned (Pending)");
                        Toast.makeText(getActivity(),"Success!\nWaiting For Owner to Scan the Book",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getActivity(),"ISBN does not match!!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        getInfoFromDB();
    }

}
