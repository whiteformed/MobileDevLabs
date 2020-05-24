package com.example.mobiledevlabs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SqlDatabaseHelper extends SQLiteOpenHelper {
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

    SqlDatabaseHelper(@Nullable Context context) {
        super(context, database_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createTables(SQLiteDatabase db) {
        sqlCommand = "create table " + table_countries + " (" + column_country_id + " integer primary key autoincrement, " + column_country_name + " text, " + column_country_capital + " text, " + column_country_square + " text)";
        db.execSQL(sqlCommand);

        sqlCommand = "create table " + table_saved_countries + " (" + column_country_id + " integer primary key autoincrement, " + column_country_name + " text, " + column_country_capital + " text, " + column_country_square + " text)";
        db.execSQL(sqlCommand);

        sqlCommand = "create table " + table_users + " (" + column_user_id + " integer primary key autoincrement, " + column_user_login + " text, " + column_user_password + " text)";
        db.execSQL(sqlCommand);
    }

    public boolean hasUser(User user) {
        SQLiteDatabase db = this.getReadableDatabase();

        boolean exists = false;

        sqlCommand = "select * from " + table_users + " where "
                + column_user_login + " = '" + user.getUsername().trim() + "' and "
                + column_user_password + " = '" + user.getPassword().trim() + "'";

        Cursor cursor = db.rawQuery(sqlCommand, null);

        if (cursor.moveToFirst()) {
            exists = true;
        }

        cursor.close();
        db.close();

        return exists;
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(column_user_login, user.getUsername().trim());
        contentValues.put(column_user_password, user.getPassword().trim());

        long rowInserted = db.insert(table_users, null, contentValues);

        db.close();

        return rowInserted != -1;
    }

    public boolean insertUserList(ArrayList<User> userArrayList) {
        for (int i = 0; i < userArrayList.size(); i++) {
            if (!insertUser(userArrayList.get(i)))
                return false;
        }

        return true;
    }

    public boolean insertCountry(String table, Country country) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(column_country_name, country.getName().trim());
        contentValues.put(column_country_capital, country.getCapital().trim());
        contentValues.put(column_country_square, country.getSquare().trim());

        long rowInserted = db.insert(table, null, contentValues);

        db.close();

        return rowInserted != -1;
    }

    public boolean insertCountryList(String table, ArrayList<Country> countryArrayList) {
        for (int i = 0; i < countryArrayList.size(); i++) {
            if (!insertCountry(table, countryArrayList.get(i)))
                return false;
        }

        return true;
    }

    public boolean deleteCountry(String table, Country country) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause
                = column_country_name + " = '" + country.getName().trim() + "' and "
                + column_country_capital + " = '" + country.getCapital().trim() + "' and "
                + column_country_square + " = '" + country.getSquare().trim() + "'";

        int rowsAffected = db.delete(table, whereClause, null);

        db.close();

        return rowsAffected != 0;
    }

    public boolean deleteCountryList(String table, ArrayList<Country> countryArrayList) {
        for (int i = 0; i < countryArrayList.size(); i++) {
            if (!deleteCountry(table, countryArrayList.get(i)))
                return false;
        }

        return true;
    }

    public boolean updateCountry(String table, Country oldCountry, Country newCountry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(column_country_name, newCountry.getName().trim());
        contentValues.put(column_country_capital, newCountry.getCapital().trim());
        contentValues.put(column_country_square, newCountry.getSquare().trim());

        String whereClause
                = column_country_name + " = '" + oldCountry.getName().trim() + "' and "
                + column_country_capital + " = '" + oldCountry.getCapital().trim() + "' and "
                + column_country_square + " = '" + oldCountry.getSquare().trim() + "'";

        int rowsAffected = db.update(table, contentValues, whereClause, null);

        return rowsAffected != 0;
    }

    public boolean hasCountry(String table, Country country) {
        SQLiteDatabase db = this.getReadableDatabase();

        boolean exists = false;

        sqlCommand = "select * from " + table + " where "
                + column_country_name + " = '" + country.getName().trim() + "' and "
                + column_country_capital + " = '" + country.getCapital().trim() + "' and "
                + column_country_square + " = '" + country.getSquare().trim() + "'";

        Cursor cursor = db.rawQuery(sqlCommand, null);

        if (cursor.moveToFirst()) {
            exists = true;
        }

        cursor.close();
        db.close();

        return exists;
    }

    public ArrayList<Country> getCountryList(String table) {
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

    public static String getCountriesTableName() {
        return table_countries;
    }

    public static String getSavedCountriesTableName() {
        return table_saved_countries;
    }

    public static String getUsersTableName() {
        return table_users;
    }
}
