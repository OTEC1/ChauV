package com.example.chauvendor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chauvendor.R;
import com.example.chauvendor.UI.Edit_post;
import com.example.chauvendor.UI.Reviews;
import com.example.chauvendor.util.Constants;
import com.example.chauvendor.util.utils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class home_apdater extends RecyclerView.Adapter<home_apdater.MyHolder> {



    private List<Map<String, Object>> model;
    private Context context;

    public home_apdater(List<Map<String, Object>> model, Context context) {
        this.model = model;
        this.context = context;
    }



    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_post, parent, false);
        return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        holder.user_tag.setText("Category: " + model.get(position).get("category"));
        holder.item_name.setText("Dish: " + model.get(position).get("food_name"));
        holder.mprices.setText("Price: " + model.get(position).get("food_price"));
        new utils().img_load(holder.poster_value.getContext(), Constants.IMG_URL+String.valueOf(model.get(position).get("img_url")), holder.progressBar, holder.poster_value);


        holder.views.setOnClickListener(k -> {
            k.getContext().startActivity(new Intent(k.getContext(), Reviews.class).putExtra("data", model.get(position).get("doc").toString()));
        });


        holder.update_post.setOnClickListener(k -> {
            new utils().open_Fragment(new Edit_post(), "Edit_post", k.getContext(), BUNDEL(position), R.id.frameLayout);

        });

    }


    @Override
    public int getItemCount() {
        return model.size();
    }

    private Bundle BUNDEL(int position) {
        Bundle b = new Bundle();
        b.putStringArray("data", new String[]{model.get(position).get("doc").toString(), model.get(position).get("food_name").toString(), String.valueOf(model.get(position).get("food_price")), String.valueOf(model.get(position).get("img_url"))});
        return b;
    }




    class MyHolder extends RecyclerView.ViewHolder {
        private CircleImageView poster_value;
        private TextView user_tag, item_name, mprices;
        private ProgressBar progressBar;
        private Button views, update_post;

        public MyHolder(View view) {
            super(view);
            poster_value = (CircleImageView) view.findViewById(R.id.cat_img);
            user_tag = (TextView) view.findViewById(R.id.cat_name);
            item_name = (TextView) view.findViewById(R.id.item_name);
            mprices = (TextView) view.findViewById(R.id.prices);
            views = (Button) view.findViewById(R.id.view_review);
            update_post = (Button) view.findViewById(R.id.update_post);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        }
    }
}
