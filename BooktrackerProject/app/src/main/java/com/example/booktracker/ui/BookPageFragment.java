package com.example.booktracker.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.booktracker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookPageFragment extends Fragment {
    public TextView back;
    private String title;
    private String author;
    private String status;
    private String isbn;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BookPageFragment() {
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
    public static BookPageFragment newInstance(String param1, String param2) {
        BookPageFragment fragment = new BookPageFragment();
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
        final View view = inflater.inflate(R.layout.fragment_book_page, container, false);
        final TextView textView = view.findViewById(R.id.textView_back);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.bookPageFragment_to_dashboard);
            }
        });
        final Button edit = view.findViewById(R.id.edit_button);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.bookPageFragment_to_editPageFragment);
            }
        });
        final Button track_button = view.findViewById(R.id.track_button);
        track_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.bookPageFragment_to_trackPageFragment);
            }
        });
        if (getArguments() != null){
            title = getArguments().getString("title");
            author = getArguments().getString("author");
            status = getArguments().getString("status");
            isbn = getArguments().getString("isbn");
        }
        TextView titleView = view.findViewById(R.id.textView_title);
        titleView.setText(title);
        TextView authorView = view.findViewById(R.id.author_id);
        authorView.setText(author);
        TextView statusView = view.findViewById(R.id.textView_status);
        if (status != "true"){
            statusView.setText("Status: Not borrowed");
        }
        else {
            statusView.setText("Status: Borrowed");
        }
        TextView isbnView = view.findViewById(R.id.textView_isbn);
        isbnView.setText("ISBN: " + isbn);

        return view;

    }


}
