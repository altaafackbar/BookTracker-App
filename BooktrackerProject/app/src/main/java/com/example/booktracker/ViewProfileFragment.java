package com.example.booktracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class ViewProfileFragment extends Fragment {
    private String searchTerm;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getActivity().getIntent().getExtras();

        if(bundle.getString("search")!= null)
        {
            searchTerm = bundle.getString("search");
        }
        TextView username = getActivity().findViewById(R.id.view_profule_username);
        username.setText(searchTerm);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View profile = inflater.inflate(R.layout.fragment_view_profile,container,false);
        final Button back = profile.findViewById(R.id.view_profile_back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(profile).navigate(R.id.action_viewProfileFragment_to_navigation_home);
            }
        });

        final ListView bookList = profile.findViewById(R.id.view_profile_book_list);
        bookList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(profile).navigate(R.id.action_viewProfileFragment_to_bookPageFragment);
            }
        });

        return profile;
    }
}
