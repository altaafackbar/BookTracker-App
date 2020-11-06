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
import android.widget.ListView;
import android.widget.TextView;

import com.example.booktracker.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryTabFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryTabFragment newInstance(String param1, String param2) {
        HistoryTabFragment fragment = new HistoryTabFragment();
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
        View view = inflater.inflate(R.layout.fragment_history_tab, container, false);

        ArrayList<String> tempUnames = new ArrayList<String>(); //Temporary Usernames to add to list, to be replaced with requesters
        tempUnames.add("User1"); //Temporary Usernames to add to list, to be replaced with requesters
        tempUnames.add("User2"); //Temporary Usernames to add to list, to be replaced with requesters

        ListView historyListView = (ListView)view.findViewById(R.id.history_list_view);
        historyListView.setAdapter(new HistoryTabFragment.HistoryListAdapter(getActivity(),R.layout.history_list_item,tempUnames));
        return view;
    }

    private class HistoryListAdapter extends ArrayAdapter<String> {
        private int layout;
        public HistoryListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            HistoryViewHolder mainViewholder = null;
            if(convertView==null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                HistoryViewHolder viewHolder = new HistoryViewHolder();
                viewHolder.borrower_name = (TextView) convertView.findViewById(R.id.borrower_text);
                viewHolder.return_date = (TextView) convertView.findViewById(R.id.return_date_text);
                viewHolder.borrower_name.setText("Borrowed by: Username"); //Temporary to test list view
                viewHolder.return_date.setText("Returned on: March 20, 2020"); //Temporary to test list view
                convertView.setTag(viewHolder);
            }
            else{
                mainViewholder = (HistoryViewHolder) convertView.getTag();
                mainViewholder.borrower_name.setText("Borrowed by: Username1"); //Temporary hardcoded string to test, to be replaced with users
                mainViewholder.return_date.setText("Returned on: March 20, 2020"); //Temporary hardcoded string, to be replaced with dates

            }
            return convertView;
        }

    }

    public class HistoryViewHolder{
        TextView borrower_name;
        TextView return_date;
    }
}