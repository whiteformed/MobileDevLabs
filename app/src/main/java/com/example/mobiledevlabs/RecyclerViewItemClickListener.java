package com.example.mobiledevlabs;

public interface RecyclerViewItemClickListener {
    void onDeleteItemButtonClicked(int pos);

    void onUpdateItemButtonClicked(int pos);

    void onSaveItemButtonClicked(int pos);
}
