package com.example.mobiledevlabs;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    private Context context;
    private ArrayList<Country> countriesArrayList;
    private RecyclerViewItemClickListener recyclerViewItemClickListener;

    RecyclerViewAdapter(Context context, ArrayList<Country> countriesArray) {
        this.context = context;
        this.countriesArrayList = countriesArray;
    }

    void setRecyclerViewItemClickListener(RecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.recyclerViewItemClickListener = recyclerViewItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recycler_view_item, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        holder.tv_country.setText(countriesArrayList.get(position).getName());

        if (1 <= holder.tv_country.length() && holder.tv_country.length() <= 5)
            holder.tv_country.setTextColor(Color.parseColor("#F44336"));
        else if (6 <= holder.tv_country.length() && holder.tv_country.length() <= 7)
            holder.tv_country.setTextColor(Color.parseColor("#2196F3"));
        else if (7 < holder.tv_country.length())
            holder.tv_country.setTextColor(Color.parseColor("#5DCF61"));

        holder.tv_capital.setText(countriesArrayList.get(position).getCapital());

        countriesArrayList.get(position).setFlag(R.drawable.user_pic); //temporary
        holder.iv_flag.setImageResource(countriesArrayList.get(position).getFlag());

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewItemClickListener.onItemClickListener(holder.getAdapterPosition());
            }
        };

        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                recyclerViewItemClickListener.onItemLongClickListener(holder.getAdapterPosition());

                return true;
            }
        };

        holder.layout.setOnClickListener(onClickListener);
        holder.layout.setOnLongClickListener(onLongClickListener);
    }

    @Override
    public int getItemCount() {
        return countriesArrayList.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView tv_country;
        TextView tv_capital;
        ImageView iv_flag;
        RelativeLayout layout;

        RecyclerViewHolder(View view) {
            super(view);

            tv_country = view.findViewById(R.id.tv_country);
            tv_capital = view.findViewById(R.id.tv_capital);
            iv_flag = view.findViewById(R.id.iv_flag);
            layout = view.findViewById(R.id.item_card);
        }
    }
}