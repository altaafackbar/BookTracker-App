/**
 * LentOutFragment
 * This fragment displays a list of books lent out
 * From here the owner can scan a book to confirm receival
 * once books are returned.
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

public class LentOutFragment extends Fragment {
    private ArrayList<Book> bookList;
    private FirebaseFirestore db;
    private String title;
    private String author;
    private String isbn;
    private String borrower;
    private LentOutListAdapter myAdapter;


    public LentOutFragment() {
        // Required empty public constructor
    }

    public static LentOutFragment newInstance() {
        LentOutFragment fragment = new LentOutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Sets up the layout of the LentOutFragment
     * Where owners can see books they've lent out and
     * scan books that have been returned
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_lent_out_page, container, false);
        bookList = new ArrayList<>();
        ListView listView = view.findViewById(R.id.lentOutPageListView);
        myAdapter = new LentOutListAdapter(getActivity(), R.layout.lent_out_list_item, bookList);
        listView.setAdapter(myAdapter);
        getInfoFromDB();
        return view;

    }

    /**
     * LentOutListAdapter helps to display the Book inside of this list view
     */
    private class LentOutListAdapter extends ArrayAdapter<Book> {
        private int layout;
        public LentOutListAdapter(@NonNull Context context, int resource, @NonNull List<Book> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        /**
         * Allows us to determine what happens when a user clicks a button on the
         * selected position.
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
            final LentViewHolder viewHolder = new LentViewHolder();
            viewHolder.title = convertView.findViewById(R.id.title_lent_item);
            viewHolder.author= convertView.findViewById(R.id.author_lent_item);
            viewHolder.isbn = convertView.findViewById(R.id.isbn_lent_item);
            viewHolder.borrower = convertView.findViewById(R.id.borrower_lent_item);
            viewHolder.scan_received_btn = convertView.findViewById(R.id.scan_button_lent_item);
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

            return convertView;
        }
    }

    /**
     * LentViewHolder helps to hold and set data for each of the items in the list
     */
    private class LentViewHolder{
        TextView title;
        TextView author;
        TextView isbn;
        TextView borrower;
        Button scan_received_btn;
    }

    /**
     * Clears the list and re-adds all books lent out of the current user
     */
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

    /**
     * Implements the scan activity
     * Allows owner to scan their books to have it returned
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        //Allows the owner to scan the book in order to have to book available again
        if(requestCode == 103){
            if (resultCode == CommonStatusCodes.SUCCESS){
                if (data!=null){
                    final Barcode barcode = data.getParcelableExtra("barcode");
                    if (barcode.displayValue.equals(isbn)){
                        //Delete the book from Books Lent Out of the owner
                        db = FirebaseFirestore.getInstance();
                        db.collection("Users")
                                .document(MainActivity.current_user)
                                .collection("Books Lent Out")
                                .document(isbn)
                                .delete();
                        //Sets the status of the book to available
                        db.collection("Users")
                                .document(MainActivity.current_user)
                                .collection("Books")
                                .document(isbn)
                                .update("book.status","available");
                        //Deletes the book from Borrowed Books of borrower
                        db.collection("Users")
                                .document(borrower)
                                .collection("Borrowed Books")
                                .document(isbn)
                                .delete();
                        Map<String, NotificationMessage>  notification = new HashMap<>();
                        Date newDate = new Date();
                        NotificationMessage newNotificationMessage = new NotificationMessage("Book Returned!", MainActivity.current_user+" has Received and Scanned the Following Book: \n"+title+"\n"+"isbn: "+isbn, newDate.toString());
                        notification.put("notification", newNotificationMessage);
                        //Gives the borrower a notification that the book has been returned
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


