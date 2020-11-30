package com.example.anchovyfeeder.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anchovyfeeder.R;

import java.util.ArrayList;

public class DailyViewAdapter extends RecyclerView.Adapter<DailyViewAdapter.DailyViewHolder> {
    private ArrayList<PhotoItem> dailyPhotos;

    public DailyViewAdapter(ArrayList<PhotoItem> photos) {
        this.dailyPhotos = photos;
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_daily_view, null);
        return new DailyViewAdapter.DailyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyViewHolder dailyViewHolder, int position) {
        dailyViewHolder.imageView.setImageURI(dailyPhotos.get(position).getSrc());

    }

    @Override
    public int getItemCount() {
        return dailyPhotos.size();
    }

    public class DailyViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;

        public DailyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.gallery_photo);
        }
    }
}

