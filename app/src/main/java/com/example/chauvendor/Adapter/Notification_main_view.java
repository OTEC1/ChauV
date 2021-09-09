package com.example.chauvendor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chauvendor.R;
import com.example.chauvendor.UI.Inner_notification;
import com.example.chauvendor.util.utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Notification_main_view extends RecyclerView.Adapter<Notification_main_view.MyHolder> {


    private List<Map<String, Object>> items;
    private Context context;


    private String c, TAG = "Notification_main_view";

    public Notification_main_view(Context context, List<Map<String, Object>> items) {
        this.items = items;
        this.context = context;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Notification_main_view.MyHolder holder, int position) {

        Log.d(TAG, "onBindViewHolder: " + items);

        boolean obj = (boolean) items.get(position).get("OBJ");
        boolean bool = (boolean) items.get(position).get("dstatus");
        boolean bool2 = (boolean) items.get(position).get("vstatus");

        if (!obj && !bool && !bool2)
            c = "New";
        else if (obj && !bool)
            c = "Seen";
        else if (bool2 && obj)
            c = "Dispatched";


        holder.mStatus.setText(c);
        holder.mitem_count.setText((Integer.parseInt(new utils().Stringnify(items.get(position).get("item_count"))) == 1) ? new utils().Stringnify(items.get(position).get("item_count")) + " item" : new utils().Stringnify(items.get(position).get("item_count")) + " items");
        holder.mphone.setText("Phone: " + new utils().Stringnify(items.get(position).get("phone")));
        holder.mvendor_name.setText(new utils().Stringnify(items.get(position).get("name")));
        holder.mvendor_id.setText(new utils().Stringnify(items.get(position).get("order_id")));
        holder.mtime_stamp.setText("Date: " + new utils().Stringnify(items.get(position).get("TimeStamp")));
        holder.muser_name.setText("Customer: " + new utils().Stringnify(items.get(position).get("users")));


        holder.cardView.setOnClickListener(o -> {
            Intent intent = new Intent(context, Inner_notification.class);
            intent.putExtra("data_key", items.get(position).get("cart_tracker").toString());
            intent.putExtra("docs_key", items.get(position).get("current_doc").toString());
            intent.putExtra("docID", items.get(position).get("docs_id").toString());
            context.startActivity(intent);
            if (!holder.mStatus.getText().equals("Seen"))
                UPDATE_DOC(items.get(position).get("current_doc"), items.get(position).get("docs_id"), holder.mStatus.getContext(), Boolean.getBoolean("vstatus"));
        });

    }


    private void UPDATE_DOC(Object current_doc, Object doc, Context context, boolean status) {

        if (!status) {
            Map<String, Object> p = new HashMap<>();
            p.put("OBJ", true);
            FirebaseFirestore.getInstance().collection(context.getString(R.string.Paid_Vendors_Brand_Section)).document("Orders").collection(current_doc.toString()).document(doc.toString())
                    .update(p)
                    .addOnCompleteListener(i -> {
                        if (i.isSuccessful())
                            Log.d(TAG, "UPDATE_DOC: Viewed Order");
                        else
                            Log.d(TAG, "UPDATE_DOC: " + i, i.getException());
                    });
        }

    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {
        private TextView muser_name, mphone, mtime_stamp, mitem_count, mStatus, mvendor_name, mvendor_id;
        private CardView cardView;


        public MyHolder(View view) {
            super(view);
            muser_name = (TextView) view.findViewById(R.id.user_name);
            mphone = (TextView) view.findViewById(R.id.phone);
            mtime_stamp = (TextView) view.findViewById(R.id.time_stamp);
            mitem_count = (TextView) view.findViewById(R.id.item_count);
            mStatus = (TextView) view.findViewById(R.id.Status);
            mvendor_id = (TextView) view.findViewById(R.id.vendor_id);
            mvendor_name = (TextView) view.findViewById(R.id.vendor_name);
            cardView = (CardView) view.findViewById(R.id.card);

        }
    }
}
