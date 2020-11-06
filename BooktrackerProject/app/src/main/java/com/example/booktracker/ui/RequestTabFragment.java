package com.example.booktracker.ui;

import android.content.Context;
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

import com.example.booktracker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestTabFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //ArrayList<String> tempUnames = new ArrayList<String>(); //Temporary Usernames to add to list, to be replaced with requesters

    public RequestTabFragment() {
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
    public static RequestTabFragment newInstance(String param1, String param2) {
        RequestTabFragment fragment = new RequestTabFragment();
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

        ArrayList<String> tempUnames = new ArrayList<String>(); //Temporary Usernames to add to list, to be replaced with requesters
        tempUnames.add("User1"); //Temporary Usernames to add to list, to be replaced with requesters
        tempUnames.add("User2"); //Temporary Usernames to add to list, to be replaced with requesters
        View view = inflater.inflate(R.layout.fragment_request_tab, container, false);
        ListView listView = (ListView)view.findViewById(R.id.requestListView);
        listView.setAdapter(new RequestListAdapter(getActivity(),R.layout.request_list_item,tempUnames));
        return view;
    }


    private class RequestListAdapter extends ArrayAdapter<String>{
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