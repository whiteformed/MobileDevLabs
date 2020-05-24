package com.example.mobiledevlabs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class ActivityMain extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    FragmentChooseCountry fragmentChooseCountry;
    FragmentSavedCountries fragmentSavedCountries;
    FragmentCountryInfo fragmentCountryInfo;
    FragmentAboutAuthor fragmentAboutAuthor;

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame, fragment).addToBackStack(null).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            fragmentChooseCountry = new FragmentChooseCountry();
            fragmentSavedCountries = new FragmentSavedCountries();
            fragmentCountryInfo = new FragmentCountryInfo();
            fragmentAboutAuthor = new FragmentAboutAuthor();

            setFragment(fragmentChooseCountry);
        }
        else {
            return;
        }

        Intent intent = getIntent();
        String login = intent.getStringExtra("login");
        String country = intent.getStringExtra("country");
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);
        String locationInfo = String.format("\n%s\n(%.6f, %.6f)", country, latitude, longitude);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.usernameText);
        userName.setText(login);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.choose_country:
                        setFragment(fragmentChooseCountry);
                        break;
                    case R.id.saved_countries:
                        setFragment(fragmentSavedCountries);
                        break;
                    case R.id.country_info:
                        setFragment(fragmentCountryInfo);
                        break;
                    case R.id.about_author:
                        setFragment(fragmentAboutAuthor);
                        break;
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });
    }
}
