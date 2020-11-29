/**
*HomeFragment
* Sets up the layout and functions of the home screen
* Allows users to search for books, and other users
* Displays a list of available books
* Allows user to sign out
 */
package com.example.booktracker.ui.home;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booktracker.AvailableRecyclerViewAdapter;
import com.example.booktracker.Book;
import com.example.booktracker.BookSearchActivity;
import com.example.booktracker.MainActivity;
import com.example.booktracker.NotificationMessage;
import com.example.booktracker.R;
import com.example.booktracker.SearchProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.booktracker.App.CHANNEL_1_ID;
import static com.example.booktracker.ui.notifications.NotificationMessagesTabFragment.newNotification;

public class HomeFragment extends Fragment {
    public static int size = 0;
    public static int newLength = 0;
    private NotificationManagerCompat notificationManager;
    private ArrayList<NotificationMessage> notificationList;
    private String notificationTitle;
    private String message;
    private String date;
    private FirebaseFirestore db2;

    private HomeViewModel homeViewModel;
    private FirebaseFirestore db;
    private EditText searchText;
    private EditText bookSearchText;
    private String owner;
    public static String searchUser;
    private String status;
    private String title;
    private String author;
    private String isbn;
    private String bookImg;
    private RecyclerView myRecyclerview;
    private ArrayList<Book> bookList;

    /**
     * This sets up the layout in the home fragment
     * Sets up all functions of the buttons on click
     * Sets up the initial texts for TextView and edit boxes
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView email = root.findViewById(R.id.email);
        searchText = root.findViewById(R.id.userSearch);
        Button sign_out_button = root.findViewById(R.id.sign_out_button2);

        sign_out_button.setOnClickListener(new View.OnClickListener() {
            //Signs the user out
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        email.setText("Welcome Back \n" + MainActivity.current_user);

        Button search = root.findViewById(R.id.searchUserButton);
        search.setOnClickListener(new View.OnClickListener() {
            //Searches for the user entered
            @Override
            public void onClick(View view) { searchUser(); }
        });

        bookList = new ArrayList<>();
        myRecyclerview = root.findViewById(R.id.recyclerview_id_home);
        final AvailableRecyclerViewAdapter myAdapter = new AvailableRecyclerViewAdapter(getActivity(), bookList);
        myRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3 ));
        myRecyclerview.setAdapter(myAdapter);
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                //Add all available books in the database to bookList.
                bookList.clear();
                ArrayList<String> userEmailList = new ArrayList<>();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {

                    userEmailList.add((String) doc.getData().get("UserEmail"));
                }
                for(String uEmail:userEmailList){
                    if (!uEmail.equals(MainActivity.current_user)) {
                        db.collection("Users").document(uEmail)
                                .collection("Books")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d(TAG, document.getId() + " => " + document.getData().get("book"));
                                                Map<String, Object> book = (Map<String, Object>) document.getData().get("book");
                                                status = (String) book.get("status");
                                                if (status.equals("available")) {
                                                    isbn = document.getId();
                                                    title = (String) book.get("title");
                                                    author = (String) book.get("author");
                                                    bookImg = (String) book.get("image");
                                                    owner = (String)book.get("owner");
                                                    Book newBook = new Book(title, author, isbn, status, owner);
                                                    newBook.setImage(bookImg);
                                                    bookList.add(newBook);
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


        final Button BookSearchButton = root.findViewById(R.id.bookSearch);
        bookSearchText = root.findViewById(R.id.bookText);
        BookSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Searches and displays books with descriptions matching the entered terms
                String searchTerm = bookSearchText.getText().toString();
                if (!searchTerm.isEmpty()) {
                    Intent intent = new Intent(getActivity(), BookSearchActivity.class);
                    intent.putExtra("searchTerm", searchTerm);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(),
                            "Please enter a search term", Toast.LENGTH_SHORT).show();
                }
            }
        });

        notificationList = new ArrayList<>();
        db2 = FirebaseFirestore.getInstance();
        db2.collection("Users")
                //Checks the db to see if new notifications has been added to sent push notifications
                .document(MainActivity.current_user)
                .collection("Notifications")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document:task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData().get("notification"));
                                Map<String, Object> notification = (Map<String, Object>) document.getData().get("notification");
                                notificationTitle = (String) notification.get("title");
                                message = (String) notification.get("message");
                                date = (String) notification.get("receiveDate");
                                NotificationMessage newMessage = new NotificationMessage(notificationTitle, message, date);
                                notificationList.add(newMessage);

                            }
                        }
                        else{
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        newLength = notificationList.size();
                        if (newLength > size){
                            newNotification = true;
                            notificationManager = NotificationManagerCompat.from(getActivity());
                            Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_1_ID)
                                    .setSmallIcon(R.drawable.ic_baseline_new)
                                    .setContentTitle(notificationList.get(newLength-1).getTitle())
                                    .setContentText(notificationList.get(newLength-1).getMessage())
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                    .build();

                            notificationManager.notify(1, notification);
                        }
                        size = newLength;
                        Log.i(TAG, "SIZEed: "+ newLength + "length "+ size);
                    }
                });
        newNotification = false;
        return root;
    }



    /**
     *Searches for an user according to the term entered
     *Displays the user profile if search exists,
     *otherwise give the appropriate error messages.
     */
   public void searchUser(){
       db = FirebaseFirestore.getInstance();
       String searchTerm = searchText.getText().toString();
       if(!searchTerm.isEmpty()){
           DocumentReference docIdRef = db.collection("Users").document(searchTerm);
           docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                   if (task.isSuccessful()) {
                       DocumentSnapshot document = task.getResult();
                       if (document.exists()) {
                           Log.d("TAG", "Document exists!");
                           Toast toast = Toast.makeText(getContext(), "Username exists", Toast.LENGTH_SHORT);
                           toast.show();
                           searchUser = searchText.getText().toString();
                           //Call to SearchProfile Activity
                           Intent intent = new Intent(getActivity(), SearchProfile.class);
                           startActivity(intent);
                       } else {
                           Log.d("TAG", "Document does not exist!");
                           Toast toast = Toast.makeText(getContext(), "Username does not exist", Toast.LENGTH_SHORT);
                           toast.show();
                       }
                   } else {
                       Log.d("TAG", "Failed with: ", task.getException());
                   }
               }
           });
       }
       else{
           Toast.makeText(getContext(),
                   "Please enter a search term", Toast.LENGTH_SHORT).show();

       }

    }


    /**
     * Brings an user back to the logged out page
     */
    private void signOut(){
        //Signs an user out
        MainActivity.current_user = null;
        this.getActivity().finish();
    }
}