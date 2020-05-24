package com.example.mobiledevlabs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SqlDBHelper extends SQLiteOpenHelper {
    private static final String database_name = "sql_database";
    private static final String table_countries = "countries";
    private static final String table_saved_countries = "saved_countries";
    private static final String column_country_id = "id";
    private static final String column_country_name = "name";
    private static final String column_country_capital = "capital";
    private static final String column_country_square = "square";

    private static final String table_users = "users";
    private static final String column_user_id = "id";
    private static final String column_user_login = "login";
    private static final String column_user_password = "password";

    private String sqlCommand;

    SqlDBHelper(@Nullable Context context) {
        super(context, database_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        sqlCommand = "create table " + table_countries + " (" + column_country_id + " integer primary key autoincrement, " + column_country_name + " text, " + column_country_capital + " text, " + column_country_square + " text)";
        db.execSQL(sqlCommand);

        sqlCommand = "create table " + table_saved_countries + " (" + column_country_id + " integer primary key autoincrement, " + column_country_name + " text, " + column_country_capital + " text, " + column_country_square + " text)";
        db.execSQL(sqlCommand);

        sqlCommand = "create table " + table_users + " (" + column_user_id + " integer primary key autoincrement, " + column_user_login + " text, " + column_user_password + " text)";
        db.execSQL(sqlCommand);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addUser(String log, String pw) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("login", log);
        contentValues.put("password", pw);

        long result = db.insert(table_users, null, contentValues);

        db.close();

        return result != -1;
    }

    public boolean getUser(String log, String pw) {
        boolean exists = false;
        SQLiteDatabase db = this.getReadableDatabase();

        sqlCommand = "select * from " + table_users + " where " + column_user_login + " = '" + log + "' and " + column_user_password + " = '" + pw + "'";
        Cursor cursor = db.rawQuery(sqlCommand, null);

        if (cursor.moveToFirst()) {
            exists = true;
        }

        cursor.close();
        db.close();

        return exists;
    }

    public boolean addData(String table, Country country) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(column_country_name, country.getName());
        contentValues.put(column_country_capital, country.getCapital());
        contentValues.put(column_country_square, country.getSquare());

        long result = db.insert(table, null, contentValues);

        db.close();

        return result != -1;

//        sqlCommand = "insert into " + table_countries + " (" + column_name + ", " + column_capital + ", " + column_square + ") values " + "(" + country.getName() + ", " + country.getCapital() + ", " + country.getSquare() + ")";
//        db.execSQL(sqlCommand);

//        db.close();

//        return;
    }

    public boolean addDataList(String table, ArrayList<Country> countryArrayList) {
        for (int i = 0; i < countryArrayList.size(); i++) {
            if (!addData(table, countryArrayList.get(i)))
                return false;
        }

        return true;
    }

    public boolean deleteData(String table, String country_name) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(table, column_country_name + " = '" + country_name + "'", null);
        //sqlCommand = "delete from " + table_countries + " where " + column_country_name + " = '" + country_name + "'";
        //db.execSQL(sqlCommand);

        db.close();

        return true;
    }

    public ArrayList<Country> getDataList(String table) {
        ArrayList<Country> countryArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        sqlCommand = "select * from " + table;
        Cursor cursor = db.rawQuery(sqlCommand, null);

        if (cursor.moveToFirst()) {
            do {
                Country country = new Country(
                        cursor.getString(cursor.getColumnIndex(column_country_name)),
                        cursor.getString(cursor.getColumnIndex(column_country_capital)),
                        cursor.getString(cursor.getColumnIndex(column_country_square)));

                countryArrayList.add(country);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return countryArrayList;
    }

    public static String getTableCountries() {
        return table_countries;
    }

    public static String getTableSavedCountries() {
        return table_saved_countries;
    }

    public static String getTableUsers() {
        return table_users;
    }
}
