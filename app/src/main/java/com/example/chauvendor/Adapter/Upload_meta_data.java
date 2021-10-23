package com.example.chauvendor.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chauvendor.R;
import com.example.chauvendor.UI.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Upload_meta_data extends RecyclerView.Adapter<Upload_meta_data.MyHolder> {

    private String TAG = "Upload_meta_data";
    private long money;
    private Context context;
    private List<Map<String, Object>> data_s, list = new ArrayList<>();
    private Map<String, Object> member;
    public static  Set<Map<String,Object>> member2;

    public Upload_meta_data(){

    }
    public Upload_meta_data(Context context, List<Map<String, Object>> data_s) {
        this.context = context;
        this.data_s = data_s;
    }

    @NonNull
    @NotNull
    @Override
    public Upload_meta_data.MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_pop_values, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Upload_meta_data.MyHolder holder, int position) {

        member = new HashMap<>();
        holder.info.setText(data_s.get(position).get("name").toString() + " price");
        holder.minus.setOnClickListener(u -> {
            if (money != 0) {
                money -= 100;
                holder.text.setText(String.valueOf(money));
                SUM_IN(holder, position);
            }
        });

        holder.plus.setOnClickListener(u -> {
            money += 100;
            holder.text.setText(String.valueOf(money));
            SUM_IN(holder, position);
        });
    }

    private void SUM_IN(MyHolder holder, int position) {
        member.put(data_s.get(position).get("name").toString(), holder.text.getText().toString());
        list.add(member);
        member2 = new HashSet<>(list);

    }

    @Override
    public int getItemCount() {
        return data_s.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView text, info, plain;
        private Button plus, minus;

        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.food_quantity);
            plain = itemView.findViewById(R.id.plain);
            info = itemView.findViewById(R.id.info);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
        }
    }


}
