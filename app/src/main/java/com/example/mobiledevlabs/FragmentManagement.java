package com.example.mobiledevlabs;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

public class FragmentManagement extends FragmentManager {
    private ArrayList<Fragment> fragmentArrayList;
    private Context context;
    private FragmentManager fragmentManager;
    private int containerViewId = 0;

    FragmentManagement(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public void showFragment() {

    }

    public void setContainerViewId(int containerViewId) {
        this.containerViewId = containerViewId;
    }

    public int getContainerViewId() {
        return containerViewId;
    }

    public void addFragment(Fragment fragment) {
        if (getContainerViewId() == 0)
            return;

        fragmentManager.beginTransaction().add(containerViewId, fragment).addToBackStack(null).commit();
    }

    public void addFragmentList(ArrayList<Fragment> fragmentArrayList) {
        this.fragmentArrayList = new ArrayList<>(fragmentArrayList);

        for (Fragment fragment:this.fragmentArrayList) {
            addFragment(fragment);
        }
    }
}
