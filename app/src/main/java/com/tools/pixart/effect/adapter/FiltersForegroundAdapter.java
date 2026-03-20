package com.tools.pixart.effect.adapter;

import android.graphics.Bitmap;
import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tools.pixart.R;
import com.tools.pixart.effect.callBack.FilterPixItemClickListener;

public class FiltersForegroundAdapter extends RecyclerView.Adapter<FiltersForegroundAdapter.ViewHolder> {
    private List<Bitmap> filterBitmaps;
    private List<String> filterNames;
    private FilterPixItemClickListener filterItemClickListener;

    public FiltersForegroundAdapter(List<Bitmap> filterBitmaps, List<String> filterNames, FilterPixItemClickListener filterItemClickListener) {
        this.filterBitmaps = filterBitmaps;
        this.filterNames = filterNames;
        this.filterItemClickListener = filterItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_neon_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.imageView.setImageBitmap(filterBitmaps.get(position));
        holder.textView.setText(filterNames.get(position));
        holder.itemView.setOnClickListener(v -> {
            if (filterItemClickListener != null) {
                filterItemClickListener.onFilterClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterBitmaps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_filter_icon);
            textView = itemView.findViewById(R.id.tv_filter_name);
        }
    }
}