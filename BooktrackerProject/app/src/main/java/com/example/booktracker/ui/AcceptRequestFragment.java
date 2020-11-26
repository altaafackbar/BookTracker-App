package com.example.booktracker.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.example.booktracker.Book;
import com.example.booktracker.MainActivity;
import com.example.booktracker.NotificationMessage;
import com.example.booktracker.R;
import com.example.booktracker.ScanBarcodeActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.MapType;
import com.sucho.placepicker.PlacePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.sucho.placepicker.Constants.GOOGLE_API_KEY;

public class AcceptRequestFragment extends Fragment {
    public TextView back;
    SimpleDateFormat dateFor;
    private String owner;
    private String requestStatus;
    ArrayList<RequestInfo> requestInfoList;
    RequestListAdapter myAdapter;
    private String title;
    private String current_isbn;
    private String isbn;
    private FirebaseFirestore db;
    private String requester_s;

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
        requestInfoList = new ArrayList<>();
        isbn = getArguments().getString("isbn");
        title = getArguments().getString("title");


        ListView listView = (ListView)view.findViewById(R.id.requests_accept_list);
        myAdapter = (new RequestListAdapter(getActivity(),R.layout.request_list_item, requestInfoList));
        listView.setAdapter(myAdapter);
        getInfoFromDB();
        myAdapter.notifyDataSetChanged();
        return view;
    }

    private class RequestListAdapter extends ArrayAdapter<RequestInfo> {
        private int layout;
        public RequestListAdapter(@NonNull Context context, int resource, @NonNull List<RequestInfo> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
            ViewHolder mainViewHolder = null;

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.profile_pic = (ImageView)convertView.findViewById(R.id.requester_profile_pic);
            viewHolder.r_username = (TextView)convertView.findViewById(R.id.requester_username);
            viewHolder.accept_btn = (Button)convertView.findViewById(R.id.request_accept_btn);
            viewHolder.decline_btn = (Button)convertView.findViewById(R.id.request_decline_btn);
            viewHolder.request_date = (TextView)convertView.findViewById(R.id.request_date);
            viewHolder.scan_btn = (Button)convertView.findViewById(R.id.request_scan_btn);

            viewHolder.accept_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requester_s = getItem(position).requester;
                    Intent intent = new PlacePicker.IntentBuilder().setLatLong(53.5461, -113.4938)  // Initial Latitude and Longitude the Map will load into
                            .showLatLong(true)  // Show Coordinates in the Activity
                            .setMapZoom(20.0f)  // Map Zoom Level. Default: 14.0
                            .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
                            .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
                            .setMarkerDrawable(R.drawable.ic_map_marker) // Change the default Marker Image
                            .setMarkerImageImageColor(R.color.colorPrimary)
                            .setFabColor(R.color.quantum_bluegrey200)
                            .setPrimaryTextColor(R.color.quantum_black_text) // Change text color of Shortened Address
                            .setSecondaryTextColor(R.color.white) // Change text color of full Address
                            .setBottomViewColor(R.color.quantum_bluegrey200) // Change Address View Background Color (Default: White)
                            .setMapRawResourceStyle(R.raw.places_keep)  //Set Map Style (https://mapstyle.withgoogle.com/)
                            .setMapType(MapType.NORMAL)
                            .onlyCoordinates(true)  //Get only Coordinates from Place Picker
                            .hideLocationButton(true)   //Hide Location Button (Default: false)
                            .disableMarkerAnimation(false)   //Disable Marker Animation (Default: false)
                            .build(getActivity());
                    startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);
                    doAccept(getItem(position).requester);

                    Toast.makeText(getContext(),"Accepted",Toast.LENGTH_SHORT).show();
                    getInfoFromDB();

                }
            });

            viewHolder.decline_btn.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doDecline(getItem(position).requester);
                    Toast.makeText(getContext(),"Declined",Toast.LENGTH_SHORT).show();
                    getInfoFromDB();
                    myAdapter.notifyDataSetChanged();
                }
            }));

            viewHolder.scan_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requester_s = getItem(position).requester;
                    Intent intent = new Intent(getActivity(), ScanBarcodeActivity.class);
                    startActivityForResult(intent, 103);
                    myAdapter.notifyDataSetChanged();
                }
            });
            viewHolder.r_username.setText("Requested By: "+getItem(position).requester);
            dateFor = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            viewHolder.request_date.setText("Requested on: "+getItem(position).request_date.toString());
            if(getItem(position).status.equals("Accepted")){
                viewHolder.accept_btn.setText("View/Change Location");
                viewHolder.decline_btn.setVisibility(View.GONE);
                viewHolder.scan_btn.setVisibility(View.VISIBLE);
            }
            convertView.setTag(viewHolder);


            return convertView;
        }
    }
    public class ViewHolder{
        ImageView profile_pic;
        TextView r_username;
        Button accept_btn;
        Button decline_btn;
        Button scan_btn;
        TextView request_date;
    }
    private class RequestInfo{
        String requester;
        Date request_date;
        String status;
    }

    private void getInfoFromDB(){
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                requestInfoList.clear();
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
                                                if (owner != null && requestStatus != null && (requestStatus.equals("Pending Request")||requestStatus.equals("Accepted")) && owner.equals(MainActivity.current_user) && current_isbn.equals(isbn)) {
                                                    RequestInfo newinfo = new RequestInfo();
                                                    newinfo.requester = uEmail;
                                                    newinfo.request_date = ((Timestamp)book.get("requestDate")).toDate();
                                                    newinfo.status  = requestStatus;
                                                    requestInfoList.add(newinfo);
                                                }
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }

                                        Collections.sort(requestInfoList, new Comparator<RequestInfo>() {
                                            @Override
                                            public int compare(RequestInfo o1, RequestInfo o2) {
                                                return o1.request_date.compareTo(o2.request_date);
                                            }
                                        });
                                        myAdapter.notifyDataSetChanged();
                                    }
                                });
                    }
                }

            }

        });
    }
    private void doDecline(String requester){
        db = FirebaseFirestore.getInstance();
        Toast.makeText(getActivity(),requester,Toast.LENGTH_SHORT).show();

        Map<String, NotificationMessage>  notification = new HashMap<>();
        Date newDate = new Date();
        NotificationMessage newNotificationMessage = new NotificationMessage("Book Request Declined", "Request Has Been Declined For\n"+title+"\n"+"isbn: "+isbn, newDate.toString());
        notification.put("notification", newNotificationMessage);
        db.collection("Users")
                .document(requester)
                .collection("Requested Books")
                .document(isbn)
                .delete();
        db.collection("Users")
                .document(requester)
                .collection("Notifications")
                .document(newDate.toString()).set(notification);
    }

    private void doAccept(String requester){
        db = FirebaseFirestore.getInstance();
        Toast.makeText(getActivity(),requester,Toast.LENGTH_SHORT).show();
        db.collection("Users")
                .document(requester)
                .collection("Requested Books")
                .document(isbn)
                .update("book.requestStatus","Accepted");
        Map<String, NotificationMessage>  notification = new HashMap<>();
        Date newDate = new Date();
        NotificationMessage newNotificationMessage = new NotificationMessage("Book Request Accepted!", "Request Has Been Accepted For\n"+title+"\n"+"isbn: "+isbn, newDate.toString());
        notification.put("notification", newNotificationMessage);
        db.collection("Users")
                .document(requester)
                .collection("Notifications")
                .document(newDate.toString()).set(notification);
        //Modify the book property under the owner's collection
        db.collection("Users").document(MainActivity.current_user).collection("Books")
                .document(isbn).update("book.status", "Accepted");
        db.collection("Users").document(MainActivity.current_user).collection("Books")
                .document(isbn).update("book.requester", requester);


        final CollectionReference collectionReference = db.collection("Users");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
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
                                                    db.collection("Users")
                                                            .document(uEmail)
                                                            .collection("Requested Books")
                                                            .document(isbn)
                                                            .delete();
                                                    Map<String, NotificationMessage>  notification = new HashMap<>();
                                                    Date newDate = new Date();
                                                    NotificationMessage newNotificationMessage = new NotificationMessage("Book Request Declined", "Request Has Been Declined For\n"+title+"\n"+"isbn: "+isbn, newDate.toString());
                                                    notification.put("notification", newNotificationMessage);
                                                    db.collection("Users")
                                                            .document(uEmail)
                                                            .collection("Notifications")
                                                            .document(newDate.toString()).set(notification);
                                                    getInfoFromDB();

                                                }
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                        myAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 103) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    final Barcode barcode = data.getParcelableExtra("barcode"); //Contains barcode
                    if (barcode.displayValue.equals(isbn)){
                        db = FirebaseFirestore.getInstance();
                        db.collection("Users")
                                .document(MainActivity.current_user)
                                .collection("Books")
                                .document(isbn)
                                .update("book.status","Borrowed (Pending)");
                        db.collection("Users")
                                .document(requester_s)
                                .collection("Requested Books")
                                .document(isbn)
                                .update("book.status","Borrowed (Pending)");
                        Map<String, NotificationMessage>  notification = new HashMap<>();
                        Date newDate = new Date();
                        NotificationMessage newNotificationMessage = new NotificationMessage(title+" Scanned by \n"+MainActivity.current_user+" to be Lent!", "Please Scan The Following Book Upon Receival: \n"+title+"\n"+"isbn: "+isbn, newDate.toString());
                        notification.put("notification", newNotificationMessage);
                        db.collection("Users")
                                .document(requester_s)
                                .collection("Notifications")
                                .document(newDate.toString()).set(notification);
                        Toast.makeText(getActivity(),"Success!\nPlease Allow the Borrower to Scan the Book",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getActivity(),"ISBN does not match!!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        else if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
                String pickupLat = String.valueOf(addressData.getLatitude());
                String pickupLon = String.valueOf(addressData.getLongitude());
                db = FirebaseFirestore.getInstance();
                //set pickup location for owner
                db.collection("Users")
                        .document(MainActivity.current_user)
                        .collection("Book Requests Received")
                        .document(requester_s)
                        .update("book.pickupLat",pickupLat);
                db.collection("Users")
                        .document(MainActivity.current_user)
                        .collection("Book Requests Received")
                        .document(requester_s)
                        .update("book.pickupLon",pickupLon);

                //set pickup location for borrower
                db.collection("Users")
                        .document(requester_s)
                        .collection("Requested Books")
                        .document(isbn)
                        .update("book.pickupLat",pickupLat);
                db.collection("Users")
                        .document(requester_s)
                        .collection("Requested Books")
                        .document(isbn)
                        .update("book.pickupLon",pickupLon);

                Toast.makeText(getActivity(),addressData.toString(),Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}