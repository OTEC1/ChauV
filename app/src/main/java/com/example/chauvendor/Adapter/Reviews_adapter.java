package com.example.chauvendor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chauvendor.R;
import com.example.chauvendor.UI.Reviews;
import com.example.chauvendor.model.Review_models;
import com.example.chauvendor.util.utils;

import java.util.List;
import java.util.Map;

public class Reviews_adapter extends RecyclerView.Adapter<Reviews_adapter.Myholder> {


    private List<Review_models> second;


    private String TAG = "Open", c;
    private Context context;


    public Reviews_adapter(Context context, List<Review_models> second) {
        this.second = second;
        this.context = context;
    }


    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_tabs, parent, false);
        Myholder holder = new Myholder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {


        holder.textView1.setText("User: "+new utils().Stringify(second.get(position).getUser()));
        holder.textView2.setText(new utils().Stringify(second.get(position).getReview()));

    }


    @Override
    public int getItemCount() {
        return second.size();
    }




    class Myholder extends RecyclerView.ViewHolder {

        private TextView textView1, textView2;
        public Myholder(@NonNull View itemView) {
            super(itemView);
            textView1 = (TextView) itemView.findViewById(R.id.username);
            textView2 = (TextView) itemView.findViewById(R.id.reviews);

        }
    }
}
