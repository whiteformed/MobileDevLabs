package com.example.mobiledevlabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        boolean res = sqlDBHelper.deleteData(tableSavedCountries, savedCountriesArrayList.get(pos));
        performTask(1);

        if (res) {
            Log.i(TAG, "onDeleteItemButtonClicked: Successfully deleted " + savedCountriesArrayList.get(pos).getName() + " from DB");
            Toast.makeText(getActivity(), "Successfully deleted", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "onDeleteItemButtonClicked: Failed deleting " + savedCountriesArrayList.get(pos).getName() + " from DB");
            Toast.makeText(getActivity(), "Deleting failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpdateItemButtonClicked(int pos) {

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

        performTask(1); // get

        SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                performTask(1);
                adapter.notifyDataSetChanged();
            }
        };

        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        return view;
    }

    private void performTask(int i) {
        Integer[] ops = {0, 1};
        AsynchronousTask accessDatabase = new AsynchronousTask(adapter, savedCountriesArrayList, sqlDBHelper, tableSavedCountries);
        accessDatabase.execute(ops[i]);
    }
}
