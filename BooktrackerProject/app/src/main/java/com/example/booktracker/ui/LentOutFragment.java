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
import com.example.booktracker.NotificationMessage;
import com.example.booktracker.R;
import com.example.booktracker.ScanBarcodeActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class LentOutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ArrayList<Book> bookList;
    private FirebaseFirestore db;
    private String title;
    private String author;
    private String isbn;
    private String borrower;
    LentOutListAdapter myAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LentOutFragment() {
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
    public static LentOutFragment newInstance(String param1, String param2) {
        LentOutFragment fragment = new LentOutFragment();
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
        final View view = inflater.inflate(R.layout.fragment_lent_out_page, container, false);
        bookList = new ArrayList<>();
        ListView listView = (ListView)view.findViewById(R.id.lentOutPageListView);
        myAdapter = new LentOutListAdapter(getActivity(), R.layout.lent_out_list_item, bookList);
        listView.setAdapter(myAdapter);
        getInfoFromDB();
        return view;

    }
    private class LentOutListAdapter extends ArrayAdapter<Book> {
        private int layout;
        public LentOutListAdapter(@NonNull Context context, int resource, @NonNull List<Book> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LentViewHolder mainViewHolder = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                final LentViewHolder viewHolder = new LentViewHolder();
                viewHolder.title = (TextView)convertView.findViewById(R.id.title_lent_item);
                viewHolder.author= (TextView)convertView.findViewById(R.id.author_lent_item);
                viewHolder.isbn = (TextView)convertView.findViewById(R.id.isbn_lent_item);
                viewHolder.borrower = (TextView)convertView.findViewById(R.id.borrower_lent_item);
                viewHolder.scan_received_btn = (Button)convertView.findViewById(R.id.scan_button_lent_item);
                viewHolder.scan_received_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isbn = getItem(position).getIsbn();
                        borrower = getItem(position).getRequester();
                        Intent intent = new Intent(getActivity(), ScanBarcodeActivity.class);
                        startActivityForResult(intent, 103);
                        getInfoFromDB();
                        myAdapter.notifyDataSetChanged();

               }
                });
                if (getItem(position).getStatus().equals("Returned (Pending)")){
                    viewHolder.scan_received_btn.setVisibility(View.VISIBLE);

                }
                viewHolder.title.setText("Title: "+ getItem(position).getTitle());
                viewHolder.author.setText("Author: "+getItem(position).getAuthor());
                viewHolder.isbn.setText("ISBN: "+getItem(position).getIsbn());
                viewHolder.borrower.setText("Borrowed by: "+getItem(position).getRequester());
                convertView.setTag(viewHolder);
            } else{
                mainViewHolder = (LentViewHolder) convertView.getTag();
            }
            return convertView;
        }
    }

    public class LentViewHolder{
        TextView title;
        TextView author;
        TextView isbn;
        TextView borrower;
        Button scan_received_btn;
    }

    private void getInfoFromDB(){
        bookList.clear();
        db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(MainActivity.current_user)
                .collection("Books Lent Out")
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
                                borrower = (String) book.get("requester");
                                Book newBook = new Book(title, author, isbn, (String)book.get("status"), "MainActivity.current_user");
                                newBook.setRequester(borrower);
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
        if(requestCode == 103){
            if (resultCode == CommonStatusCodes.SUCCESS){
                if (data!=null){
                    final Barcode barcode = data.getParcelableExtra("barcode");
                    if (barcode.displayValue.equals(isbn)){
                        db = FirebaseFirestore.getInstance();
                        db.collection("Users")
                                .document(MainActivity.current_user)
                                .collection("Books Lent Out")
                                .document(isbn)
                                .delete();
                        db.collection("Users")
                                .document(MainActivity.current_user)
                                .collection("Books")
                                .document(isbn)
                                .update("book.status","available");
                        db.collection("Users")
                                .document(borrower)
                                .collection("Borrowed Books")
                                .document(isbn)
                                .delete();
                        Map<String, NotificationMessage>  notification = new HashMap<>();
                        Date newDate = new Date();
                        NotificationMessage newNotificationMessage = new NotificationMessage("Book Returned!", MainActivity.current_user+" has Received and Scanned the Following Book: \n"+title+"\n"+"isbn: "+isbn, newDate.toString());
                        notification.put("notification", newNotificationMessage);
                        db.collection("Users")
                                .document(borrower)
                                .collection("Notifications")
                                .document(newDate.toString())
                                .set(notification);
                        Toast.makeText(getActivity(),"Success!!",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getActivity(),"ISBN does not match!!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

}


