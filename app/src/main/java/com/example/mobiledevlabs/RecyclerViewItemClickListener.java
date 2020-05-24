package com.example.mobiledevlabs;

public interface RecyclerViewItemClickListener {
    void onDeleteItemButtonClicked(int pos);

    void onEditItemButtonClicked(int pos);

    void onSaveItemButtonClicked(int pos);
}
