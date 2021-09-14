package com.example.chauvendor.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chauvendor.R;
import com.example.chauvendor.UI.Reviews;
import com.example.chauvendor.model.Category;
import com.example.chauvendor.util.utils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;


public class home_apdater extends FirestoreRecyclerAdapter<Category, home_apdater.MyHolder> {

    public home_apdater(@NonNull FirestoreRecyclerOptions options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyHolder holder, int position, @NonNull Category model) {

        holder.user_tag.setText("Category: "+model.getCategory());
        holder.item_name.setText("Dish: "+model.getFood_name());
        new utils().img_load(holder.poster_value.getContext(),model.getImg_url(),holder.progressBar,holder.poster_value);



        holder.poster_value.setOnClickListener(view -> {
            Bundle b = new Bundle();

        });


        holder.views.setOnClickListener(k->{
            k.getContext().startActivity(new Intent(k.getContext(), Reviews.class).putExtra("data",model.getDoc()));
        });


    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_post, parent, false);
        return new MyHolder(view);
    }


    class MyHolder extends RecyclerView.ViewHolder {
        private CircleImageView poster_value;
        private TextView user_tag,item_name,views;
        private ProgressBar progressBar;

        public MyHolder(View view) {
            super(view);
            poster_value = (CircleImageView) view.findViewById(R.id.cat_img);
            user_tag = (TextView) view.findViewById(R.id.cat_name);
            item_name = (TextView) view.findViewById(R.id.item_name);
            views = (TextView) view.findViewById(R.id.view_review);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        }
    }
}
