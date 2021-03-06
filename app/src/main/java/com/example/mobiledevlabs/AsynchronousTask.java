package com.example.mobiledevlabs;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class AsynchronousTask extends AsyncTask<Integer, Void, Void> {
    private StringBuilder buffer = new StringBuilder();
    private String data = "";
    private RecyclerViewAdapter adapter;
    private ArrayList<Country> countriesArrayList;
    private SqlDatabaseHelper sqlDatabaseHelper;
    private String table;

    private static final String TAG = "AsynchronousTask";

    AsynchronousTask(RecyclerViewAdapter adapter, ArrayList<Country> countriesArrayList, SqlDatabaseHelper sqlDatabaseHelper, String table) {
        this.adapter = adapter;
        this.countriesArrayList = countriesArrayList;
        this.sqlDatabaseHelper = sqlDatabaseHelper;
        this.table = table;
    }

    @Override
    protected Void doInBackground(Integer... op) {
        if (op[0] == 0) {
            parseJSON();
            addDataToDatabase();
        }
        else if (op[0] == 1) {
            getDataFromDatabase();
        }

        return null;
    }

    private void parseJSON() {
        String urls = "https://raw.githubusercontent.com/Lpirskaya/JsonLab/master/GuideNew";
        URL link = null;
        try {
            link = new URL(urls);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection urlConnection = null;
        try {
            assert link != null;
            urlConnection = (HttpURLConnection) link.openConnection();
        } catch (IOException e) {
            Log.i(TAG, "Error occurred while opening connection");
            e.printStackTrace();
        }

        try {
            assert urlConnection != null;
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "Connection Established");

                BufferedReader input = new BufferedReader(new InputStreamReader
                        (urlConnection.getInputStream()), 8192);
                String line;
                while ((line = input.readLine()) != null) {
                    buffer.append(line);
                }
                input.close();
                data = buffer.toString();
            } else {
                Log.i(TAG, "Connection Was Not Established");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Country[] countriesArray = gson.fromJson(data, Country[].class);
        Collections.addAll(countriesArrayList, countriesArray);
    }

    private void addDataToDatabase() {
        boolean res = sqlDatabaseHelper.insertCountryList(table, countriesArrayList);

        if (res) {
            Log.i(TAG, "Successfully added " + table + " table to DB");
        }
        else {
            Log.i(TAG, "Failed while adding "  + table + " table to DB");
        }
    }

    private void getDataFromDatabase() {
        countriesArrayList.clear();
        countriesArrayList.addAll(sqlDatabaseHelper.getCountryList(table));

        if (!countriesArrayList.isEmpty()) {
            Log.i(TAG, "Successfully read " + table + " table from DB");
        } else {
            Log.i(TAG, "Failed while reading " + table + " table from DB");
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        adapter.notifyDataSetChanged();

        Log.i(TAG, "Task Complete!");
    }
}
