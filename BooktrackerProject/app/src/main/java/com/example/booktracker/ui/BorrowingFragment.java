/**
 * BorrowingFragment
 * Fragment that displays the books the user is currently borrowing
 * From here, users can scan a book in order to start the returning process
 */
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

public class BorrowingFragment extends Fragment {
    private ArrayList<Book> bookList;
    private FirebaseFirestore db;
    private String title;
    private String author;
    private String isbn;
    private String owner;
    private BorrowingListAdapter myAdapter;


    public BorrowingFragment() {
        // Required empty public constructor
    }


    public static BorrowingFragment newInstance() {
        BorrowingFragment fragment = new BorrowingFragment();
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
        final View view = inflater.inflate(R.layout.fragment_borrowing_page, container, false);
        bookList = new ArrayList<>();
        ListView listView = view.findViewById(R.id.borrowingPageListView);
        myAdapter = new BorrowingListAdapter(getActivity(), R.layout.borrowing_list_item, bookList);
        listView.setAdapter(myAdapter);
        getInfoFromDB();
        return view;

    }


    //BorrowingListAdapter used for displaying borrowed books in a listview
    private class BorrowingListAdapter extends ArrayAdapter<Book> {
        private int layout;
        public BorrowingListAdapter(@NonNull Context context, int resource, @NonNull List<Book> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
            final BorrowingViewHolder viewHolder = new BorrowingViewHolder();
            viewHolder.title = convertView.findViewById(R.id.title_borrowing_item);
            viewHolder.author = convertView.findViewById(R.id.author_borrowing_item);
            viewHolder.isbn = convertView.findViewById(R.id.isbn_borrowing_item);
            viewHolder.owner = convertView.findViewById(R.id.owner_borrowing_item);
            viewHolder.message = convertView.findViewById(R.id.confirm_message_borrowing_item);
            viewHolder.scan_return_btn = convertView.findViewById(R.id.scan_button_borrowing_item);
            viewHolder.scan_return_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Scans the book to start the return process
                    isbn = getItem(position).getIsbn();
                    owner = getItem(position).getOwner();
                    title = getItem(position).getTitle();
                    Intent intent = new Intent(getActivity(), ScanBarcodeActivity.class);
                    startActivityForResult(intent, 103);
                }
            });
            if (getItem(position).getStatus().equals("Returned (Pending)")){
                //Hide scan button if already scanned.
                viewHolder.message.setVisibility(View.VISIBLE);
                viewHolder.scan_return_btn.setVisibility(View.GONE);
            }
            viewHolder.title.setText("Title: "+ getItem(position).getTitle());
            viewHolder.author.setText("Author: "+getItem(position).getAuthor());
            viewHolder.isbn.setText("ISBN: "+getItem(position).getIsbn());
            viewHolder.owner.setText("Owner: "+getItem(position).getOwner());
            convertView.setTag(viewHolder);

            return convertView;
        }
    }

    //ViewHolder to hold and set the values of each item in the list
    private class BorrowingViewHolder{
        TextView title;
        TextView author;
        TextView isbn;
        TextView owner;
        TextView message;
        Button scan_return_btn;
    }

    private void getInfoFromDB(){
        //clears the bookList then adds books to the bookList that are borrowed for the current user
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
        //Scans the book to begin the returning process
        if (requestCode == 103){
            if (resultCode == CommonStatusCodes.SUCCESS){
                if (data!=null){
                    final Barcode barcode = data.getParcelableExtra("barcode");
                    if (barcode.displayValue.equals(isbn)){
                        db = FirebaseFirestore.getInstance();
                        //Set the status to "Returned (Pending)" for the requester and the owner
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
                        Map<String, NotificationMessage>  notification = new HashMap<>();
                        Date newDate = new Date();
                        NotificationMessage newNotificationMessage = new NotificationMessage(title+" Scanned by "+MainActivity.current_user+" to be Returned!", "Please Scan The Following Book Upon Receival: \n"+title+"\n"+"isbn: "+isbn, newDate.toString());
                        notification.put("notification", newNotificationMessage);
                        //Give the owner a notification to indicate starting of return process
                        db.collection("Users")
                                .document(owner)
                                .collection("Notifications")
                                .document(newDate.toString()).set(notification);
                        Toast.makeText(getActivity(),"Success!\nWaiting For Owner to Scan the Book",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getActivity(),"ISBN does not match!!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

}

