package com.example.mobiledevlabs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    private Context context;
    private ArrayList<Country> countriesArrayList;
    private FragmentManager fragmentManager;
    private RecyclerViewItemClickListener recyclerViewItemClickListener;

    RecyclerViewAdapter(Context context, FragmentManager fragmentManager, ArrayList<Country> countriesArray) {
        this.context = context;
        this.fragmentManager = fragmentManager;
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
                FragmentCountryInfo fragment = new FragmentCountryInfo();

                Bundle bundle = new Bundle();
                bundle.putString("country", countriesArrayList.get(position).getName());
                bundle.putString("capital", countriesArrayList.get(position).getCapital());
                bundle.putString("square", countriesArrayList.get(position).getSquare());
                bundle.putInt("imageID", countriesArrayList.get(position).getFlag());
                fragment.setArguments(bundle);

                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
            }
        };

        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_item);
                dialog.setCancelable(true);

                TextView tv_message = dialog.findViewById(R.id.message_tv);
                tv_message.setText("What would you like to do with this item?");

                TextView tv_country = dialog.findViewById(R.id.tv_country);
                TextView tv_capital = dialog.findViewById(R.id.tv_capital);
                ImageView iv_flag = dialog.findViewById(R.id.image_view);
                tv_country.setText(countriesArrayList.get(holder.getAdapterPosition()).getName());
                tv_capital.setText(countriesArrayList.get(holder.getAdapterPosition()).getCapital());
                iv_flag.setImageResource(countriesArrayList.get(holder.getAdapterPosition()).getFlag());

                final Animation animAlpha = AnimationUtils.loadAnimation(context, R.anim.anim_button_alpha);
                Button button_save = dialog.findViewById(R.id.button_confirm);
                Button button_edit = dialog.findViewById(R.id.button_edit);
                Button button_delete = dialog.findViewById(R.id.button_delete);

                View.OnClickListener onButtonSaveClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(animAlpha);
                        recyclerViewItemClickListener.onSaveItemButtonClicked(holder.getAdapterPosition());
                        dialog.cancel();
                    }
                };

                View.OnClickListener onButtonEditClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(animAlpha);
                        recyclerViewItemClickListener.onUpdateItemButtonClicked(holder.getAdapterPosition());
                        dialog.cancel();
                    }
                };

                View.OnClickListener onButtonDeleteClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(animAlpha);
                        recyclerViewItemClickListener.onDeleteItemButtonClicked(holder.getAdapterPosition());
                        dialog.cancel();
                    }
                };

                button_save.setOnClickListener(onButtonSaveClickListener);
                button_edit.setOnClickListener(onButtonEditClickListener);
                button_delete.setOnClickListener(onButtonDeleteClickListener);

                dialog.show();

                return true;
            }
        };

        holder.frameLayout.setOnClickListener(onClickListener);
        holder.frameLayout.setOnLongClickListener(onLongClickListener);
    }

    @Override
    public int getItemCount() {
        return countriesArrayList.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView tv_country;
        TextView tv_capital;
        ImageView iv_flag;
        FrameLayout frameLayout;

        RecyclerViewHolder(View view) {
            super(view);

            tv_country = view.findViewById(R.id.tv_country);
            tv_capital = view.findViewById(R.id.tv_capital);
            iv_flag = view.findViewById(R.id.image_view);
            frameLayout = view.findViewById(R.id.frame_layout);
        }
    }
}