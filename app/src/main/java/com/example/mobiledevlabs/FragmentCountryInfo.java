package com.example.mobiledevlabs;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class FragmentCountryInfo extends Fragment {
    private TextView tv_country;
    private TextView tv_capital;
    private TextView tv_square;
    private ImageView iv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_country_info, container, false);

        tv_country = view.findViewById(R.id.country_name);
        tv_capital = view.findViewById(R.id.country_capital_name);
        tv_square = view.findViewById(R.id.country_square);
        iv = view.findViewById(R.id.country_flag);

        Bundle bundle = getArguments();

        if (bundle != null) {
            Log.i(getClass().getName(), "getIncomingIntent: opening bundle");
            getIncomingIntent(bundle);
        } else {
            Log.i(getClass().getName(), "getIncomingIntent: empty bundle");
        }

        return view;
    }

    private void getIncomingIntent(Bundle bundle) {
        tv_country.setText(bundle.getString("country"));
        tv_capital.setText(bundle.getString("capital"));

        String s = bundle.getString("square") + " km²";
        tv_square.setText(s);

        if (bundle.getInt("imageID", 0) == 0) {
            iv.setImageResource(R.drawable.user_pic);
        } else {
            iv.setImageResource(bundle.getInt("imageID"));
        }
    }
}
