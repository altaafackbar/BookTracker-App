/**
 * NotificationsFragment
 * Helps to setup the layout of fragments in the notifications tab
 * with the help of NotificationPageAdapter
 */
package com.example.booktracker.ui.notifications;

import android.app.Notification;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.booktracker.MainActivity;
import com.example.booktracker.NotificationMessage;
import com.example.booktracker.R;
import com.example.booktracker.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.booktracker.App.CHANNEL_1_ID;
import static com.example.booktracker.ui.home.HomeFragment.newLength;
import static com.example.booktracker.ui.notifications.NotificationMessagesTabFragment.newNotification;

public class NotificationsFragment extends Fragment {
    private NotificationManagerCompat notificationManager;
    ArrayList<NotificationMessage> notificationList;
    private String notificationTitle;
    private FirebaseFirestore db2;
    private String message;
    private String date;


    private TabLayout tabLayout;
    private ViewPager viewPager;
    public NotificationPageAdapter pagerAdapter;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Sets up the layout for the notifications fragment
     * Shows the tabs inside of the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        tabLayout = view.findViewById(R.id.notification_tab_layout);
        viewPager = view.findViewById(R.id.notification_viewpager);

        pagerAdapter = new NotificationPageAdapter(getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 0){
                    pagerAdapter.notifyDataSetChanged();
                }
                else if (tab.getPosition() == 1){
                    pagerAdapter.notifyDataSetChanged();
                }
                else if (tab.getPosition() == 2){
                    pagerAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        notificationList = new ArrayList<>();
        db2 = FirebaseFirestore.getInstance();
        db2.collection("Users")
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
                        if (newLength > HomeFragment.size){
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
                        HomeFragment.size = newLength;
                        Log.i(TAG, "SIZEed: "+ newLength + "length "+ HomeFragment.size);
                    }
                });
        newNotification = false;
        return view;
    }
}