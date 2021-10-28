package com.example.chauvendor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chauvendor.R;
import com.example.chauvendor.util.Constants;
import com.example.chauvendor.util.utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Notification_children_view extends RecyclerView.Adapter<Notification_children_view.MyHolder> {

    private List<Map<String, Object>> items;
    private Context context;

    public Notification_children_view(Context context, List<Map<String, Object>> list) {
        this.items = list;
        this.context = context;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_children, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Notification_children_view.MyHolder holder, int position) {

        if (!items.get(position).get("star_boi").toString().equals("Self serve")) {
            new utils().img_load(holder.poster_value.getContext(), new utils().Stringnify(items.get(position).get("img_url")), holder.progressBar, holder.poster_value);
            holder.ntime_stamp.setText("Date: " + new utils().Stringnify(items.get(position).get("Timestamp")));
            holder.nvendor_id.setText(new utils().Stringnify(items.get(position).get("doc_id")));
            holder.nfood_name.setText("Food: " + new utils().Stringnify(items.get(position).get("food_name")));
            holder.nfood_price.setText("Price: " + new utils().Stringnify(items.get(position).get("food_price")));
            holder.nitem_id.setText(new utils().Stringnify(items.get(position).get("item_id")));
            holder.nfood_quantity.setText("Quantity: " + new utils().Stringnify(items.get(position).get("quantity")));

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {
        private CircleImageView poster_value;
        private TextView nfood_name, nfood_price, nfood_quantity, nitem_id, nvendor_id, ntime_stamp;
        private ProgressBar progressBar;

        public MyHolder(View view) {
            super(view);
            poster_value = (CircleImageView) view.findViewById(R.id.food_img);
            nfood_name = (TextView) view.findViewById(R.id.food_name);
            nfood_price = (TextView) view.findViewById(R.id.food_price);
            nfood_quantity = (TextView) view.findViewById(R.id.food_quantity);
            nitem_id = (TextView) view.findViewById(R.id.item_id);
            nvendor_id = (TextView) view.findViewById(R.id.vendor_id);
            ntime_stamp = (TextView) view.findViewById(R.id.time_stamp);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);

        }
    }
}
