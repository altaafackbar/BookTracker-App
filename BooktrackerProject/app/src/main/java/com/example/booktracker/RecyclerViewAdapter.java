/**
*RecyclerViewAdapter
* Used to create CardView of books owned by the user
* This is used in the display of books in the dashboard fragment
* On click, brings the user to Book Page Fragment where they can
* track, edit, delete, and view thier own book.
 */

package com.example.booktracker;

import android.content.Context;
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
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static android.content.ContentValues.TAG;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context myContext;
    private List<Book> myData;

    public RecyclerViewAdapter(Context myContext, List<Book> myData){
        this.myContext = myContext;
        this.myData = myData;
    }

    /**
     * Determines the layout of each item by referring to the cardview_book_item xml file
     * @param parent
     * @param viewType
     * @return ViewHolder
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater myInflater = LayoutInflater.from(myContext);
        view = myInflater.inflate(R.layout.cardview_book_item, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * Sets the image of the book
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.tv_book_title.setText(myData.get(position).getTitle());
        String encodedString = myData.get(position).getImage();
        if(encodedString != null){
            Log.d(TAG, "onBindViewHolder: pic exists");
            byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            holder.img_book_thumbnail.setImageBitmap(bitmap);
        }
        else{
            Log.d(TAG, "onBindViewHolder: pic doesnt exists");
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Brings user to book page fragment for the book selected
                Navigation.createNavigateOnClickListener(R.id.dashboard_to_bookPageFragment);
                Bundle args = new Bundle();
                args.putString("title", myData.get(position).getTitle());
                args.putString("author", myData.get(position).getAuthor());
                args.putString("status", myData.get(position).getStatus());
                if(myData.get(position).getStatus().equals("Borrowed")){
                    args.putString("borrower",myData.get(position).getRequester());
                }
               args.putString("isbn", myData.get(position).getIsbn());
               args.putString("img",myData.get(position).getImage());
                Navigation.findNavController(view).navigate(R.id.bookPageFragment, args);
            }
        });
    }



    @Override
    public int getItemCount() {
        return myData.size();
    }

    /**
     * ViewHolder to contain the details of each item in the list
     */
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