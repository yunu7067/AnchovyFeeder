package com.example.anchovyfeeder.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anchovyfeeder.R;

import java.util.ArrayList;


public class GalleryViewAdapter extends RecyclerView.Adapter<GalleryViewAdapter.GalleryViewHolder> {
    private ArrayList<ArrayList<PhotoItem>> allPhotos;
    private Context context;

    public GalleryViewAdapter(Context context, ArrayList<ArrayList<PhotoItem>> allPhotos) {
        this.context = context;
        this.allPhotos = allPhotos;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_daily_view, null);
        return new GalleryViewAdapter.GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        DailyViewAdapter adapter = new DailyViewAdapter(allPhotos.get(position));



    }

    @Override
    public int getItemCount() {
        return allPhotos.size();
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder {
        protected RecyclerView recyclerView;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);

            this.recyclerView = (RecyclerView) itemView.findViewById(R.id.gallery_daily_view);
        }
    }
}
