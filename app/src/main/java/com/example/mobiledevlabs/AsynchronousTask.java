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
    private DatabaseHelper databaseHelper;
    private String table;

    AsynchronousTask(RecyclerViewAdapter adapter, ArrayList<Country> countriesArrayList, DatabaseHelper databaseHelper, String table) {
        this.adapter = adapter;
        this.countriesArrayList = countriesArrayList;
        this.databaseHelper = databaseHelper;
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
            Log.i(getClass().getName(), "Error occurred while opening connection");
            e.printStackTrace();
        }

        try {
            assert urlConnection != null;
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.i(getClass().getName(), "Connection Established");

                BufferedReader input = new BufferedReader(new InputStreamReader
                        (urlConnection.getInputStream()), 8192);
                String line;
                while ((line = input.readLine()) != null) {
                    buffer.append(line);
                }
                input.close();
                data = buffer.toString();
            } else {
                Log.i(getClass().getName(), "Connection Was Not Established");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Country[] countriesArray = gson.fromJson(data, Country[].class);
        Collections.addAll(countriesArrayList, countriesArray);
    }

    private void addDataToDatabase() {
        boolean res = databaseHelper.addDataList(table, countriesArrayList);

        if (res) {
            Log.i(getClass().getName(), "Successfully added " + table + " table to DB");
        }
        else {
            Log.i(getClass().getName(), "Failed while adding "  + table + " table to DB");
        }
    }

    private void getDataFromDatabase() {
        countriesArrayList.clear();
        countriesArrayList.addAll(databaseHelper.getDataList(table));

        if (!countriesArrayList.isEmpty()) {
            Log.i(getClass().getName(), "Successfully read " + table + " table from DB");
        } else {
            Log.i(getClass().getName(), "Failed while reading " + table + " table from DB");
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        adapter.notifyDataSetChanged();

        Log.i(getClass().getName(), "Task Complete!");
    }
}
