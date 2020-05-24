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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FragmentChooseCountry extends Fragment implements RecyclerViewItemClickListener {
    private RecyclerViewAdapter adapter;
    private ArrayList<Country> countriesArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseHelper databaseHelper;
    private String tableCountries = DatabaseHelper.getTableCountries();
    private String tableSavedCountries = DatabaseHelper.getTableSavedCountries();

    @Override
    public void onItemClickListener(int pos) {
        FragmentCountryInfo fragment = new FragmentCountryInfo();

        Bundle bundle = new Bundle();
        bundle.putString("country", countriesArrayList.get(pos).getName());
        bundle.putString("capital", countriesArrayList.get(pos).getCapital());
        bundle.putString("square", countriesArrayList.get(pos).getSquare());
        bundle.putInt("imageID", countriesArrayList.get(pos).getFlag());
        fragment.setArguments(bundle);

        ((ActivityMain) getActivity()).setFragment(fragment);
    }

    @Override
    public void onItemLongClickListener(final int pos) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_item);
        dialog.setCancelable(true);

        TextView tv_message = dialog.findViewById(R.id.message_tv);

        TextView tv_country = dialog.findViewById(R.id.tv_country);
        TextView tv_capital = dialog.findViewById(R.id.tv_capital);
        ImageView iv_flag = dialog.findViewById(R.id.image_view);

        tv_message.setText(R.string.message_question);

        tv_country.setText(countriesArrayList.get(pos).getName());
        tv_capital.setText(countriesArrayList.get(pos).getCapital());
        iv_flag.setImageResource(countriesArrayList.get(pos).getFlag());

        final Animation animAlpha = AnimationUtils.loadAnimation(dialog.getContext(), R.anim.anim_button_alpha);
        Button button_save = dialog.findViewById(R.id.button_save);
        Button button_update = dialog.findViewById(R.id.button_update);
        Button button_delete = dialog.findViewById(R.id.button_delete);

        View.OnClickListener onButtonSaveClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                onSaveItemButtonClicked(pos);
                dialog.cancel();
            }
        };

        View.OnClickListener onButtonEditClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                onUpdateItemButtonClicked(pos);
                dialog.cancel();
            }
        };

        View.OnClickListener onButtonDeleteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                onDeleteItemButtonClicked(pos);
                dialog.cancel();
            }
        };

        button_save.setOnClickListener(onButtonSaveClickListener);
        button_update.setOnClickListener(onButtonEditClickListener);
        button_delete.setOnClickListener(onButtonDeleteClickListener);

        dialog.show();
    }

    private void onDeleteItemButtonClicked(int pos) {
        boolean success = databaseHelper.deleteCountry(tableCountries, countriesArrayList.get(pos));
        accessDatabase(1);

        if (success) {
            Log.i(TAG, "onDeleteItemButtonClicked: Successfully deleted " + countriesArrayList.get(pos).getName() + " from DB");
            Toast.makeText(getActivity(), "Successfully deleted", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "onDeleteItemButtonClicked: Failed deleting " + countriesArrayList.get(pos).getName() + " from DB");
            Toast.makeText(getActivity(), "Deleting failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void onSaveItemButtonClicked(int pos) {
        boolean success = databaseHelper.addCountry(tableSavedCountries, countriesArrayList.get(pos));
        accessDatabase(1);

        if (success) {
            Log.i(TAG, "onSaveItemButtonClicked: Successfully saved " + countriesArrayList.get(pos).getName() + " to DB");
            Toast.makeText(getActivity(), "Successfully saved", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "onSaveItemButtonClicked: Failed saving " + countriesArrayList.get(pos).getName() + " to DB");
            Toast.makeText(getActivity(), "Saving failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void onUpdateItemButtonClicked(int pos) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_new_item);
        dialog.setCancelable(true);

        TextView tv_message = dialog.findViewById(R.id.message_tv);
        tv_message.setText(R.string.message_update);

        final Country oldCountry = countriesArrayList.get(pos);

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
                Country newCountry = new Country(
                        et_country.getText().toString(),
                        et_capital.getText().toString(),
                        et_square.getText().toString());

                if (newCountry.getName().trim().equals("") || newCountry.getCapital().trim().equals("") || newCountry.getSquare().trim().equals("")) {
                    Toast.makeText(getActivity(), "No empty fields allowed!", Toast.LENGTH_SHORT).show();
                }
                else if (databaseHelper.checkCountry(tableCountries, newCountry)) {
                    Toast.makeText(getActivity(), "This item already exists!", Toast.LENGTH_SHORT).show();
                }
                else {
                    v.startAnimation(animAlpha);

                    boolean success = databaseHelper.updateCountry(tableCountries, oldCountry, newCountry);
                    accessDatabase(1);

                    if (success) {
                        Log.i(TAG, "onUpdateItemButtonClicked: Successfully updated " + oldCountry.getName() + " to " + newCountry.getName() + " at DB");
                        Toast.makeText(getActivity(), "Successfully updated", Toast.LENGTH_SHORT).show();
                    } else {
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

    private void onAddItemButtonClicked(Country country) {
        boolean success = databaseHelper.addCountry(tableCountries, country);
        accessDatabase(1);

        if (success) {
            Log.i(TAG, "onAddItemButtonClicked: Successfully added " + country.getName() + " to DB");
            Toast.makeText(getActivity(), "Successfully added", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "onAddItemButtonClicked: Failed adding " + country.getName() + " to DB");
            Toast.makeText(getActivity(), "Adding failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_country, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_choose);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_choose);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_new_item);
                dialog.setCancelable(true);

                TextView tv_message = dialog.findViewById(R.id.message_tv);
                tv_message.setText(R.string.message_add);

                final EditText et_country = dialog.findViewById(R.id.et_country);
                final EditText et_capital = dialog.findViewById(R.id.et_capital);
                final EditText et_square = dialog.findViewById(R.id.et_square);

                final Animation animAlpha = AnimationUtils.loadAnimation(dialog.getContext(), R.anim.anim_button_alpha);
                Button button_confirm = dialog.findViewById(R.id.button_confirm);
                button_confirm.setText(R.string.button_text_add);

                View.OnClickListener onButtonAddClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (et_country.getText().toString().equals("") || et_capital.getText().toString().equals("") || et_square.getText().toString().equals("")) {
                            Toast.makeText(getActivity(), "No empty fields allowed!", Toast.LENGTH_SHORT).show();
                        } else {
                            v.startAnimation(animAlpha);

                            Country newCountry = new Country(
                                    et_country.getText().toString(),
                                    et_capital.getText().toString(),
                                    et_square.getText().toString());

                            onAddItemButtonClicked(newCountry);
                            dialog.cancel();
                        }
                    }
                };

                button_confirm.setOnClickListener(onButtonAddClickListener);

                dialog.show();
            }
        };

        floatingActionButton.setOnClickListener(onClickListener);

        databaseHelper = new DatabaseHelper(getActivity());
        countriesArrayList = new ArrayList<>();
        adapter = new RecyclerViewAdapter(getActivity(), countriesArrayList);
        adapter.setRecyclerViewItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        accessDatabase(1); // 0 - set, 1 - get;

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
        AsynchronousTask task = new AsynchronousTask(adapter, countriesArrayList, databaseHelper, tableCountries);
        task.execute(ops[i]);
    }
}
