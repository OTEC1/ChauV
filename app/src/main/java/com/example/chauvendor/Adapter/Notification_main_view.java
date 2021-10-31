package com.example.chauvendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chauvendor.R;
import com.example.chauvendor.UI.Inner_notification;
import com.example.chauvendor.UI.Issues_submit;
import com.example.chauvendor.util.utils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.chauvendor.util.Constants.IMGURL;

public class Notification_main_view extends RecyclerView.Adapter<Notification_main_view.MyHolder> {


    private List<Map<String, Object>> items;
    private Context context;
    private AlertDialog alertDialog;
    private RecyclerView recyclerView;
    private TextView sub_totals_, report, send;
    private String c, TAG = "Notification_main_view";
    private Inner_self_serve pop_up;
    private ProgressBar progerss;
    private List<Map<String, Object>> data, e;
    private Set<Object> set;
    private List<String> option;
    private long total;

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
        holder.mphone.setText(" " + new utils().Stringnify(items.get(position).get("phone")));
        holder.mvendor_name.setText(" " + new utils().Stringnify(items.get(position).get("name")));
        holder.mvendor_id.setText(" " + new utils().Stringnify(items.get(position).get("order_id")));
        holder.mtime_stamp.setText(" " + new utils().Stringnify(items.get(position).get("TimeStamp")));
        holder.muser_name.setText(" " + new utils().Stringnify(items.get(position).get("users")));
        holder.tracker_id.setText(" " + new utils().Stringnify(items.get(position).get("cart_tracker")));


        holder.cardView.setOnClickListener(o -> {
            holder.pro.setVisibility(View.VISIBLE);
            Intent intent = new Intent(o.getContext(), Inner_notification.class);
            intent.putExtra("cart_tracker", items.get(position).get("cart_tracker").toString());
            intent.putExtra("order_id", items.get(position).get("order_id").toString());
            intent.putExtra("item_count", items.get(position).get("item_count").toString());
            intent.putExtra("docs_id", items.get(position).get("docs_id").toString());
            intent.putExtra("user_id", items.get(position).get("user_id").toString());
            intent.putExtra("user_img_url", IMGURL);


            if (items.get(position).get("star_boi").toString().equals("Vendor Option")) {
                o.getContext().startActivity(intent);holder.pro.setVisibility(View.INVISIBLE);
            } else
                DIALOG(o, items.get(position).get("cart_tracker").toString(), items.get(position).get("user_id").toString(), holder.pro, items.get(position).get("order_id").toString(), items.get(position).get("item_count").toString(), items.get(position).get("docs_id").toString());


            if (!holder.mStatus.getText().equals("Seen"))
                UPDATE_DOC(items.get(position).get("user_id"), items.get(position).get("docs_id"), holder.mStatus.getContext(), Boolean.getBoolean("vstatus"));


        });

    }


    private void DIALOG(View v, String cartTracker, String user_id, ProgressBar pro, String order_id,String item_count,String docs_id) {
        data = new ArrayList<>();
        View V = CONSTRUCT_DIALOG(v);
        FirebaseFirestore.getInstance().collection(v.getContext().getString(R.string.USER_PAID_ORDES)).document("Orders").collection(user_id)
                .get().addOnCompleteListener(h -> {
            List<DocumentSnapshot> snapshots = h.getResult().getDocuments();
            for (DocumentSnapshot snapshot : snapshots) {
                if (snapshot.get("Cart_tracker").toString().equals(cartTracker)) {
                    data.add(MAP(snapshot));
                    set_layout_self_serve(data, V, pro);
                }

            }

        });


        report.setOnClickListener(d -> {
            d.getContext().startActivity(new Intent(d.getContext(), Issues_submit.class).putExtra("cart_tracker", cartTracker));
            alertDialog.dismiss();

        });

        send.setOnClickListener(d -> {
            new Inner_notification().doc(cartTracker, order_id,item_count,docs_id,user_id, new ArrayList<>(), 0, send.getContext(), sub_totals_.getText().toString());
            alertDialog.dismiss();
        });


    }

    private View CONSTRUCT_DIALOG(View v) {
        LayoutInflater layoutInflater = LayoutInflater.from(v.getContext());
        v = layoutInflater.inflate(R.layout.dialog_promt, null);
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(v.getContext());
        alBuilder.setView(v);
        alertDialog = alBuilder.create();
        recyclerView = v.findViewById(R.id.main_recycler_view);
        send = v.findViewById(R.id.send_request);
        report = v.findViewById(R.id.report);
        sub_totals_ = v.findViewById(R.id.sub_totals);
        progerss = v.findViewById(R.id.progerss);
        return v;
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        private TextView muser_name, mphone, mtime_stamp, mitem_count, mStatus, mvendor_name, mvendor_id, tracker_id;
        private CardView cardView;
        private ProgressBar pro;

        public MyHolder(View view) {
            super(view);
            muser_name = (TextView) view.findViewById(R.id.user_name);
            mphone = (TextView) view.findViewById(R.id.phone);
            mtime_stamp = (TextView) view.findViewById(R.id.time_stamp);
            mitem_count = (TextView) view.findViewById(R.id.item_count);
            mStatus = (TextView) view.findViewById(R.id.Status);
            mvendor_id = (TextView) view.findViewById(R.id.vendor_id);
            tracker_id = (TextView) view.findViewById(R.id.mtracker_id);
            mvendor_name = (TextView) view.findViewById(R.id.vendor_name);
            cardView = (CardView) view.findViewById(R.id.card);
            pro = (ProgressBar) view.findViewById(R.id.pro);


        }
    }


    private Map<String, Object> MAP(DocumentSnapshot snapshot) {
        Map<String, Object> map = new HashMap<>();
        map.put("cart_tracker", snapshot.get("Cart_tracker"));
        map.put("TimeStamp", snapshot.get("TimeStamp"));
        map.put("doc", snapshot.get("doc"));
        map.put("doc_id", snapshot.get("doc_id"));
        map.put("food", snapshot.get("food"));
        map.put("food_name", snapshot.get("food_name"));
        map.put("food_price", snapshot.get("food_price"));
        map.put("img_url", snapshot.get("img_url"));
        map.put("item_id", snapshot.get("item_id"));
        map.put("star_boi", snapshot.get("star_boi"));
        map.put("vendor_token", snapshot.get("vendor_token"));
        map.put("user_id", snapshot.get("user_id"));
        map.put("cart_session", snapshot.get("cart_session"));

        return map;
    }


    private void UPDATE_DOC(Object current_doc, Object doc, Context context, boolean status) {
        if (!status) {
            Map<String, Object> p = new HashMap<>();
            p.put("OBJ", true);
          DocumentReference  s = FirebaseFirestore.getInstance().collection(context.getString(R.string.Paid_Vendors_Brand_Section)).document("Orders").collection(current_doc.toString()).document(doc.toString());
               s.update(p)
                    .addOnCompleteListener(i -> {
                        if (i.isSuccessful())
                            Log.d(TAG, "UPDATE_DOC: Viewed Order");
                        else
                            Log.d(TAG, "UPDATE_DOC: " + i, i.getException());
                    });
        }

    }


    @SuppressLint("SetTextI18n")
    private void set_layout_self_serve(List<Map<String, Object>> array, View u, ProgressBar pro) {
        option = new ArrayList<>();
        e = new ArrayList<>();


        for (Map<String, Object> x : array) {
            if (x.get("cart_session") != null) {
                option.add(x.get("cart_session").toString());
                set = new HashSet<>(option);
            }
        }
        if (set != null) {
            for (Object s : set) {
                for (Map<String, Object> x : array) {
                    if (x.get("cart_session") != null && x.get("star_boi").toString().equals("Self serve")) {
                        if (s.toString().equals(x.get("cart_session").toString())) {
                            e.add(x);
                        } } } } }


        LinearLayoutManager manager = new LinearLayoutManager(u.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        pop_up = new Inner_self_serve(e,u);
        recyclerView.setAdapter(pop_up);
        alertDialog.show();
        pro.setVisibility(View.INVISIBLE);
        progerss.setVisibility(View.GONE);
    }









}
