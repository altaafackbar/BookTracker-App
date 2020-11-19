package com.example.booktracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booktracker.ui.BookPageFragment;
import com.example.booktracker.ui.RequestPageFragment;

import java.util.List;

import static android.content.ContentValues.TAG;

public class BookRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context myContext;
    private List<Book> myData;

    public BookRecyclerViewAdapter(Context myContext, List<Book> myData){
        this.myContext = myContext;
        this.myData = myData;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater myInflater = LayoutInflater.from(myContext);
        view = myInflater.inflate(R.layout.cardview_book_item, parent, false);
        return new RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, final int position) {

        holder.tv_book_title.setText(myData.get(position).getTitle());
        String encodedString = myData.get(position).getImage();

        if(encodedString != null){
            Log.d(TAG, "onBindViewHolder: pic exists");
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            //holder.img_book_thumbnail.setImageResource(Integer.parseInt(myData.get(position).getImage()));
            holder.img_book_thumbnail.setImageBitmap(bitmap);
        }
        else{
            Log.d(TAG, "onBindViewHolder: pic doesnt exists");
        }




        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("title", myData.get(position).getTitle());
                args.putString("author", myData.get(position).getAuthor());
                args.putString("status", myData.get(position).getStatus());
                args.putString("isbn", myData.get(position).getIsbn());
                args.putString("img",myData.get(position).getImage());
                args.putString("owner",myData.get(position).getOwner());
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment myFragment = new RequestPageFragment();
                myFragment.setArguments(args);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, myFragment).addToBackStack(null).commit();

            }
        });
    }



    @Override
    public int getItemCount() {
        return myData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_book_title;
        ImageView img_book_thumbnail;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_book_title = (TextView) itemView.findViewById(R.id.book_title);
            img_book_thumbnail = (ImageView) itemView.findViewById(R.id.book_img);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
        }
    }
}
