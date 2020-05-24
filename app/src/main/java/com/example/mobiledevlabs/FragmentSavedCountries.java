package com.example.mobiledevlabs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FragmentSavedCountries extends Fragment implements RecyclerViewItemClickListener {
    private RecyclerViewAdapter adapter;
    private ArrayList<Country> savedCountriesArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SqlDBHelper sqlDBHelper;
    private String tableSavedCountries = SqlDBHelper.getTableSavedCountries();

    @Override
    public void onDeleteItemButtonClicked(int pos) {
        boolean success = sqlDBHelper.deleteData(tableSavedCountries, savedCountriesArrayList.get(pos));
        accessDatabase(1);

        if (success) {
            Log.i(TAG, "onDeleteItemButtonClicked: Successfully deleted " + savedCountriesArrayList.get(pos).getName() + " from DB");
            Toast.makeText(getActivity(), "Successfully deleted", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "onDeleteItemButtonClicked: Failed deleting " + savedCountriesArrayList.get(pos).getName() + " from DB");
            Toast.makeText(getActivity(), "Deleting failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpdateItemButtonClicked(int pos) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_new_item);
        dialog.setCancelable(true);

        TextView tv_message = dialog.findViewById(R.id.message_tv);
        tv_message.setText(R.string.message_update);

        final Country oldCountry = savedCountriesArrayList.get(pos);

        final EditText et_country = dialog.findViewById(R.id.et_country);
        final EditText et_capital = dialog.findViewById(R.id.et_capital);
        final EditText et_square = dialog.findViewById(R.id.et_square);

        et_country.setText(oldCountry.getName());
        et_capital.setText(oldCountry.getCapital());
        et_square.setText(oldCountry.getSquare());

        final Animation animAlpha = AnimationUtils.loadAnimation(dialog.getContext(), R.anim.anim_button_alpha);
        Button button_confirm = dialog.findViewById(R.id.button_confirm);
        button_confirm.setText(R.string.button_text_update);

        View.OnClickListener onButtonUpdateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_country.getText().toString().equals("") || et_capital.getText().toString().equals("") || et_square.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "No empty fields allowed!", Toast.LENGTH_SHORT).show();
                }
                else {
                    v.startAnimation(animAlpha);
                    Country newCountry = new Country(et_country.getText().toString(), et_capital.getText().toString(), et_square.getText().toString());

                    boolean success = sqlDBHelper.updateData(tableSavedCountries, oldCountry, newCountry);
                    accessDatabase(1);

                    if (success) {
                        Log.i(TAG, "onUpdateItemButtonClicked: Successfully updated " + oldCountry.getName() + " to " + newCountry.getName() + " at DB");
                        Toast.makeText(getActivity(), "Successfully updated", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.i(TAG, "onUpdateItemButtonClicked: Failed updating " + oldCountry.getName() + " to " + newCountry.getName() + " at DB");
                        Toast.makeText(getActivity(), "Updating failed", Toast.LENGTH_SHORT).show();
                    }

                    dialog.cancel();
                }
            }
        };

        button_confirm.setOnClickListener(onButtonUpdateClickListener);

        dialog.show();
    }

    @Override
    public void onSaveItemButtonClicked(int pos) {
        Toast.makeText(getActivity(), "Already in Saved Countries", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_countries, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_saved);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_saved);

        sqlDBHelper = new SqlDBHelper(getActivity());
        savedCountriesArrayList = new ArrayList<>();
        adapter = new RecyclerViewAdapter(getActivity(), getParentFragmentManager(), savedCountriesArrayList);
        adapter.setRecyclerViewItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        accessDatabase(1); // get

        SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                accessDatabase(1);
                adapter.notifyDataSetChanged();
            }
        };

        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        return view;
    }

    private void accessDatabase(int i) {
        Integer[] ops = {0, 1};
        AsynchronousTask accessDatabase = new AsynchronousTask(adapter, savedCountriesArrayList, sqlDBHelper, tableSavedCountries);
        accessDatabase.execute(ops[i]);
    }
}
