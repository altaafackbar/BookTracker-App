package com.example.booktracker.ui.notifications;

import android.content.Context;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class NotificationRequestedTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db;
    ArrayList<Book> bookList;
    private String title;
    private String author;
    private String isbn;
    private String status;
    private String bookImg;
    private String owner;
    RequestedListAdapter myAdapter;

    //ArrayList<String> tempUnames = new ArrayList<String>(); //Temporary Usernames to add to list, to be replaced with requesters

    public NotificationRequestedTabFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationRequestedTabFragment newInstance(String param1, String param2) {
        NotificationRequestedTabFragment fragment = new NotificationRequestedTabFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bookList = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_notification_requested_tab, container, false);
        ListView listView = (ListView)view.findViewById(R.id.notificationRequestedListView);
        myAdapter = new RequestedListAdapter(getActivity(), R.layout.requested_list_item, bookList);
        listView.setAdapter(myAdapter);
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(MainActivity.current_user)
                .collection("Requested Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData().get("book"));
                                Map<String, Object> book = (Map<String, Object>) document.getData().get("book");
                                isbn = document.getId();
                                title = (String) book.get("title");
                                author = (String) book.get("author");
                                status = (String)book.get("status");
                                bookImg = (String) book.get("image");
                                owner = (String) book.get("owner");
                                Book newBook = new Book(title, author, isbn, status, owner);
                                newBook.setImage(bookImg);
                                bookList.add(newBook);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        myAdapter.notifyDataSetChanged();
                    }
                });
        return view;
    }

    private class RequestedListAdapter extends ArrayAdapter<Book>{
        private int layout;
        public RequestedListAdapter(@NonNull Context context, int resource, @NonNull List<Book> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            RequestedViewHolder mainViewHolder = null;
            if(convertView ==null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                RequestedViewHolder viewHolder = new RequestedViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.requested_book_title);
                viewHolder.description = (TextView) convertView.findViewById(R.id.requested_book_description);
                viewHolder.status = (TextView) convertView.findViewById(R.id.requested_book_status);
                viewHolder.owner = (TextView) convertView.findViewById(R.id.requested_book_owner);
                viewHolder.book_pickup_button = (Button) convertView.findViewById(R.id.requested_book_pickup_button);
                viewHolder.book_pickup_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Button Clicked", Toast.LENGTH_SHORT).show();
                    }
                });
                viewHolder.title.setText("Title: "+getItem(position).getTitle());
                viewHolder.owner.setText("Owner: " + getItem(position).getOwner());
                viewHolder.description.setText("Description: ");
                viewHolder.status.setText("Status: ");

                convertView.setTag(viewHolder);
            }
            else{
                mainViewHolder = (RequestedViewHolder) convertView.getTag();
            }
            return convertView;
        }
    }
    public class RequestedViewHolder{
        TextView title;
        TextView description;
        TextView status;
        TextView owner;
        Button book_pickup_button;
    }
}
