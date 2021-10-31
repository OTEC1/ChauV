package com.example.chauvendor.Adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chauvendor.R;
import com.example.chauvendor.util.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.chauvendor.util.Constants.*;

public class Inner_self_serve extends RecyclerView.Adapter<Inner_self_serve.Myholder> {


    private TextView sub_totals_;
    private View u;


    private List<Map<String, Object>> obj;
    private int last_index;
    private String TAG = "Inner_self_serve";


    public Inner_self_serve(List<Map<String, Object>> obj, View u) {
        this.obj = obj;
        this.u = u;
        sub_totals_ = u.findViewById(R.id.sub_totals);
    }

    @NonNull
    @NotNull
    @Override
    public Inner_self_serve.Myholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_order_upload, parent, false);
        return new Myholder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull Inner_self_serve.Myholder holder, int position) {

        if (position != 0) {
            last_index = position - 1;
            if (!obj.get(last_index).get("cart_session").toString().equals(obj.get(position).get("cart_session").toString()))
                holder.another_plate.setText("ANOTHER PLATE");
            else
                holder.another_plate.setText("");
        }

        List<Long> amount = new ArrayList<>();
        for(Map<String,Object> price : obj){
            amount.add(Long.parseLong(price.get("food_price").toString()));
        }
        int s=0;
        for(Long item : amount)
           s += item;
        sub_totals_.setText(holder.another_plate.getContext().getString(R.string.NAIRA) + s);

        holder.food_quantity.setText(obj.get(position).get("food_price").toString());
        holder.food_name.setText(obj.get(position).get("food").toString());
        new utils().img_load(holder.image.getContext(), IMG_URL + JOIN + obj.get(position).get("food") + ".png", holder.progressBar, holder.image);

    }

    @Override
    public int getItemCount() {
        return obj.size();
    }


    public class Myholder extends RecyclerView.ViewHolder {

        private TextView food_quantity, food_name, another_plate;
        private ImageView image;
        private ProgressBar progressBar;

        public Myholder(@NonNull @NotNull View itemView) {
            super(itemView);
            food_quantity = itemView.findViewById(R.id.food_quantity);
            another_plate = itemView.findViewById(R.id.another_plate);
            food_name = itemView.findViewById(R.id.food_name);
            image = itemView.findViewById(R.id.food_view);
            progressBar = itemView.findViewById(R.id.progressBar2);
        }
    }
}
