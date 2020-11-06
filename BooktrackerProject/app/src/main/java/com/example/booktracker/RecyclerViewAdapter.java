/*package com.example.booktracker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context myContext;
    private List<Book> myData;

    public RecyclerViewAdapter(Context myContext, List<Book> myData){
        this.myContext = myContext;
        this.myData = myData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater myInflater = LayoutInflater.from(myContext);
        view = myInflater.inflate(R.layout.cardview_book_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.tv_book_title.setText(myData.get(position).getTitle());
        //holder.img_book_thumbnail.setImageResource(Integer.parseInt(myData.get(position).getImage()));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.createNavigateOnClickListener(R.id.dashboard_to_bookPageFragment);
                Bundle args = new Bundle();
                args.putString("title", myData.get(position).getTitle());
                args.putString("author", myData.get(position).getAuthor());
                args.putString("status", myData.get(position).getStatus());
               args.putString("isbn", myData.get(position).getIsbn());
                Navigation.findNavController(view).navigate(R.id.bookPageFragment, args);
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
*/