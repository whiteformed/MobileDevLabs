package com.example.mobiledevlabs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentSavedCountries extends Fragment implements RecyclerViewItemClickListener {
    private RecyclerViewAdapter adapter;
    private ArrayList<Country> countriesArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SqlDatabaseHelper sqlDatabaseHelper;
    private String tableSavedCountries = SqlDatabaseHelper.getSavedCountriesTableName();

    private static final String TAG = "FragmentSavedCountries";

    private FragmentActivity getNonNullActivity() {
        if (super.getActivity() != null) {
            return super.getActivity();
        } else {
            throw new RuntimeException("Null returned from getActivity() method");
        }
    }

    @Override
    public void onItemClickListener(int pos) {
        FragmentCountryInfo fragment = new FragmentCountryInfo();

        Bundle bundle = new Bundle();
        bundle.putString("country", countriesArrayList.get(pos).getName());
        bundle.putString("capital", countriesArrayList.get(pos).getCapital());
        bundle.putString("square", countriesArrayList.get(pos).getSquare());
        bundle.putInt("imageID", countriesArrayList.get(pos).getFlag());
        fragment.setArguments(bundle);

        ((ActivityMain) getNonNullActivity()).setFragment(fragment);
    }

    @Override
    public void onItemLongClickListener(final int pos) {
        final Dialog dialog = new Dialog(getNonNullActivity());
        dialog.setContentView(R.layout.dialog_delete_save);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setLayout(getResources().getDisplayMetrics().widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tv_message = dialog.findViewById(R.id.message_tv);

        TextView tv_country = dialog.findViewById(R.id.tv_country);
        TextView tv_capital = dialog.findViewById(R.id.tv_capital);
        ImageView iv_flag = dialog.findViewById(R.id.iv_flag);

        tv_message.setText(R.string.tv_message_question);

        tv_country.setText(countriesArrayList.get(pos).getName());
        tv_capital.setText(countriesArrayList.get(pos).getCapital());
        iv_flag.setImageResource(countriesArrayList.get(pos).getFlag());

        Button button_delete = dialog.findViewById(R.id.button_delete);
        Button button_update = dialog.findViewById(R.id.button_edit);
        Button button_save = dialog.findViewById(R.id.button_save);

        View.OnClickListener onButtonDeleteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteItemButtonClicked(pos);
                dialog.cancel();
            }
        };

        View.OnClickListener onButtonEditClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateItemButtonClicked(pos);
                dialog.cancel();
            }
        };

        if (sqlDatabaseHelper.hasCountry(tableSavedCountries, countriesArrayList.get(pos))) {
            button_save.setText(R.string.btn_text_saved);
            button_save.setTextColor(getResources().getColor(R.color.colorInactiveButtonText, null));
            button_save.setBackgroundColor(getResources().getColor(R.color.colorInactiveButtonBackground, null));
            button_save.setEnabled(false);
        } else {
            View.OnClickListener onButtonSaveClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSaveItemButtonClicked(pos);
                    dialog.cancel();
                }
            };

            button_save.setOnClickListener(onButtonSaveClickListener);
        }

        button_delete.setOnClickListener(onButtonDeleteClickListener);
        button_update.setOnClickListener(onButtonEditClickListener);

        dialog.show();
    }

    private void onDeleteItemButtonClicked(int pos) {
        boolean result = sqlDatabaseHelper.deleteCountry(tableSavedCountries, countriesArrayList.get(pos));
        accessDatabase(1);
        inform(2, result);
    }

    private void onUpdateItemButtonClicked(int pos) {
        final Dialog dialog = new Dialog(getNonNullActivity());
        dialog.setContentView(R.layout.dialog_insert_update);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setLayout(getResources().getDisplayMetrics().widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tv_message = dialog.findViewById(R.id.message_tv);
        tv_message.setText(R.string.tv_message_update);

        final Country oldCountry = countriesArrayList.get(pos);

        final EditText et_country = dialog.findViewById(R.id.et_country);
        final EditText et_capital = dialog.findViewById(R.id.et_capital);
        final EditText et_square = dialog.findViewById(R.id.et_square);

        et_country.setText(oldCountry.getName());
        et_capital.setText(oldCountry.getCapital());
        et_square.setText(oldCountry.getSquare());

        Button button_confirm = dialog.findViewById(R.id.button_confirm);
        button_confirm.setText(R.string.btn_text_update);

        View.OnClickListener onButtonUpdateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Country newCountry = new Country(
                        et_country.getText().toString(),
                        et_capital.getText().toString(),
                        et_square.getText().toString());

                if (newCountry.getName().trim().equals("") || newCountry.getCapital().trim().equals("") || newCountry.getSquare().trim().equals("")) {
                    makeToast("No empty fields allowed!");
                } else if (sqlDatabaseHelper.hasCountry(tableSavedCountries, newCountry)) {
                    makeToast("This item already exists!");
                } else {
                    boolean result = sqlDatabaseHelper.updateCountry(tableSavedCountries, oldCountry, newCountry);
                    accessDatabase(1);
                    inform(1, result);

                    dialog.cancel();
                }
            }
        };

        button_confirm.setOnClickListener(onButtonUpdateClickListener);

        dialog.show();
    }

    private void onSaveItemButtonClicked(int pos) {
        boolean result = sqlDatabaseHelper.insertCountry(tableSavedCountries, countriesArrayList.get(pos));
        accessDatabase(1);
        inform(3, result);
    }

    private void inform(int opType, boolean result) {
        // 0 - insert, 1 - update, 2 - delete, 3 - save;
        switch (opType) {
            case 0:
                if (result) {
                    Log.i(TAG, "onAddItemButtonClicked: Successfully added");
                    makeToast("Successfully added");
                } else {
                    Log.i(TAG, "onAddItemButtonClicked: Adding failed");
                    makeToast("Adding failed");
                }

                break;

            case 1:
                if (result) {
                    Log.i(TAG, "onUpdateItemButtonClicked: Successfully updated");
                    makeToast("Successfully updated");
                } else {
                    Log.i(TAG, "onUpdateItemButtonClicked: Updating failed");
                    makeToast("Updating failed");
                }

                break;

            case 2:
                if (result) {
                    Log.i(TAG, "onDeleteItemButtonClicked: Successfully deleted ");
                    Toast.makeText(getActivity(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "onDeleteItemButtonClicked: Deleting failed");
                    Toast.makeText(getActivity(), "Deleting failed", Toast.LENGTH_SHORT).show();
                }

                break;

            case 3:
                if (result) {
                    Log.i(TAG, "onSaveItemButtonClicked: Successfully saved");
                    makeToast("Successfully saved");
                } else {
                    Log.i(TAG, "onSaveItemButtonClicked: Saving failed");
                    makeToast("Saving failed");
                }

                break;
        }
    }

    private void makeToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_countries, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_saved);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_saved);

        sqlDatabaseHelper = new SqlDatabaseHelper(getActivity());
        countriesArrayList = new ArrayList<>();
        adapter = new RecyclerViewAdapter(getActivity(), countriesArrayList);
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
        AsynchronousTask accessDatabase = new AsynchronousTask(adapter, countriesArrayList, sqlDatabaseHelper, tableSavedCountries);
        accessDatabase.execute(ops[i]);
    }
}
