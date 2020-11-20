package com.example.booktracker.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

public class AcceptRequestFragment extends Fragment {
    public TextView back;
    private String title;
    private String author;
    ArrayList<String> requesterList;
    private String owner;
    private String requestStatus;

    private String status;
    private String current_isbn;
    private String isbn;
    private String img;
    private Bitmap bitmap;
    private FirebaseFirestore db;

    private boolean delete;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AcceptRequestFragment() {
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
    public static AcceptRequestFragment newInstance(String param1, String param2) {
        AcceptRequestFragment fragment = new AcceptRequestFragment();
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
        View view = inflater.inflate(R.layout.fragment_accept_request, container, false);
        requesterList = new ArrayList<>();
        isbn = getArguments().getString("isbn");
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");


        ListView listView = (ListView)view.findViewById(R.id.requests_accept_list);
        final RequestListAdapter myAdapter = (new RequestListAdapter(getActivity(),R.layout.request_list_item, requesterList));
        listView.setAdapter(myAdapter);


        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                requesterList.clear();
                final ArrayList<String> userEmailList = new ArrayList<>();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    userEmailList.add((String) doc.getData().get("UserEmail"));
                }
                for(final String uEmail:userEmailList){
                    if (!uEmail.equals(MainActivity.current_user)) {
                        db.collection("Users").document(uEmail)
                                .collection("Requested Books")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d(TAG, document.getId() + " => " + document.getData().get("book"));
                                                Map<String, Object> book = (Map<String, Object>) document.getData().get("book");
                                                requestStatus = (String) book.get("requestStatus");
                                                owner = (String)book.get("owner");
                                                current_isbn = (String)book.get("isbn");
                                                if (owner != null && requestStatus != null && requestStatus.equals("Pending Request") && owner.equals(MainActivity.current_user) && current_isbn.equals(isbn)) {
                                                    requesterList.add(uEmail);
                                                }
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                        myAdapter.notifyDataSetChanged();
                                    }
                                });
                    }
                }
            }
        });


        return view;
    }

    private class RequestListAdapter extends ArrayAdapter<String> {
        private int layout;
        public RequestListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewHolder = null;
            if(convertView==null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.profile_pic = (ImageView)convertView.findViewById(R.id.requester_profile_pic);
                viewHolder.r_username = (TextView)convertView.findViewById(R.id.requester_username);
                viewHolder.accept_btn = (Button)convertView.findViewById(R.id.request_accept_btn);
                viewHolder.decline_btn = (Button)convertView.findViewById(R.id.request_decline_btn);

                viewHolder.accept_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(),"Accepted",Toast.LENGTH_SHORT).show();
                    }
                });

                viewHolder.decline_btn.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(),"Declined",Toast.LENGTH_SHORT).show();
                    }
                }));
                viewHolder.r_username.setText(getItem(position));

                convertView.setTag(viewHolder);
            }
            else{
                mainViewHolder = (ViewHolder)convertView.getTag();
                mainViewHolder.r_username.setText(getItem(position));
            }
            return convertView;
        }
    }
    public class ViewHolder{
        ImageView profile_pic;
        TextView r_username;
        Button accept_btn;
        Button decline_btn;
    }
}
