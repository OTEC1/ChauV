package com.example.chauvendor.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.chauvendor.R;
import com.example.chauvendor.Retrofit_.Base_config;
import com.example.chauvendor.Retrofit_.Calls;
import com.example.chauvendor.util.utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Business_details extends Fragment {


    private List<Map<String, Object>> banks;
    private List<String> arrays;
    private String bank_selected;


    private Spinner spinner;
    private ArrayAdapter adapter1;
    private ProgressBar bank_loader, progress;
    private Button mbtn_register;
    private EditText account_number, name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_details, container, false);
        spinner = (Spinner) view.findViewById(R.id.bank_select);
        bank_loader = (ProgressBar) view.findViewById(R.id.bank_load);
        progress = (ProgressBar) view.findViewById(R.id.progressBar);
        mbtn_register = (Button) view.findViewById(R.id.btn_register);
        account_number = (EditText) view.findViewById(R.id.account_number);
        name = (EditText) view.findViewById(R.id.account_name);
        banks = new ArrayList<>();
        arrays = new ArrayList<>();
        getBank_list();

        mbtn_register.setOnClickListener(s -> {
            if (!bank_selected.equals("Select Bank")) {
                progress.setVisibility(View.VISIBLE);
                finish_up(name.getText().toString(), account_number.getText().toString());
            } else
                new utils().message2("Pls select your bank", requireActivity());

        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bank_selected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }


    private void finish_up(String names, String accounts) {
        FirebaseFirestore.getInstance().collection(getString(R.string.Vendor_reg_payment)).document(FirebaseAuth.getInstance().getUid())
                .set(new utils().MAP(names, accounts, bank_selected)).addOnCompleteListener(f -> {
            if (f.isSuccessful())
                onComplete_registration();
            else
                new utils().message2("Error occurred " + f.getException(), requireActivity());
        });
    }


    private void getBank_list() {
        Calls calls = Base_config.getConnection().create(Calls.class);
        Call<List<Map<String, Object>>> bank_list = calls.get_list_of_bank();
        bank_list.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {

                banks = response.body();
                for (Map<String, Object> z : banks)
                    arrays.add(z.get("name").toString());
                if (arrays != null)
                    pop_out(arrays);
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                message2(t.getLocalizedMessage());
            }
        });

    }


    private void pop_out(List<String> arrays) {
        adapter1 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, arrays);
        adapter1.setDropDownViewResource(R.layout.text_pad);
        adapter1.notifyDataSetChanged();
        spinner.setAdapter(adapter1);
        bank_loader.setVisibility(View.GONE);
    }


    private void message2(String s) {
        new utils().message2(s, requireActivity());
    }



    private void onComplete_registration() {
        new utils().message2("Register successfully ", requireActivity());
        new utils().init(requireActivity()).edit().putString(getString(R.string.VENDOR), null).apply();
        startActivity(new Intent(getContext(), Login.class).putExtra("check_view", String.valueOf(1)));

    }
}