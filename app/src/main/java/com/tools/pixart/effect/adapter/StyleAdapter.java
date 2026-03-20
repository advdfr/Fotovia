package com.tools.pixart.effect.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import com.tools.pixart.R;
import com.tools.pixart.effect.custom.CustomTextView;
import com.tools.pixart.effect.callBack.PIXStyleClickListener;
import com.tools.pixart.effect.model.PathModelPix;


import java.util.ArrayList;

public class StyleAdapter extends RecyclerView.Adapter<StyleAdapter.CommonHolder> {
    public int selectedPos = 0;
    private final Context context;
    private final PIXStyleClickListener filterItemClickListener;
    private final ArrayList<PathModelPix> arrIcon;

    public StyleAdapter(Context context, ArrayList<PathModelPix> arrIcon, PIXStyleClickListener filterItemClickListener) {
        this.context = context;
        this.filterItemClickListener = filterItemClickListener;
        this.arrIcon = arrIcon;
    }

    @NonNull
    @Override
    public CommonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_item_option, parent, false);
        return new CommonHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonHolder holder, int position) {
        holder.mSelectedBorder.setVisibility(position == selectedPos ? View.VISIBLE : View.GONE);

        PathModelPix item = arrIcon.get(position);
        if(!item.getPathString().equalsIgnoreCase("offLine")){
            Glide.with(context)
                    .asBitmap()
                    .load(item.getPathString())
                    .into(holder.ivImage);
        }else {
            Glide.with(context)
                    .asBitmap()
                    .load(item.getPathInt())
                    .into(holder.ivImage);
        }
    }


    public void setSelectedPos(int pos) {
        int oldPos = this.selectedPos;
        this.selectedPos = pos;
        notifyItemChanged(oldPos);
        notifyItemChanged(selectedPos);
    }

    @Override
    public int getItemCount() {
        return arrIcon.size();
    }

    class CommonHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        CustomTextView tvFilterName;
        CustomTextView mSelectedBorder;

        CommonHolder(View view) {
            super(view);
            ivImage = view.findViewById(R.id.img_filter);
            tvFilterName = view.findViewById(R.id.tv_filter);
            mSelectedBorder = view.findViewById(R.id.selectedBorder);

            view.setOnClickListener(v -> {
                int oldPos = selectedPos;
                selectedPos = getAdapterPosition();
                if (selectedPos != RecyclerView.NO_POSITION) {
                    notifyItemChanged(oldPos);
                    notifyItemChanged(selectedPos);
                    filterItemClickListener.onFilterClicked(selectedPos);
                }
            });
        }
    }
}