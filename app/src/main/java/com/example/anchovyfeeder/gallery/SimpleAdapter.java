package com.example.anchovyfeeder.gallery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.anchovyfeeder.R;
import com.example.anchovyfeeder.realmdb.PhotoObject;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {
    //private static final int COUNT = 100;

    private final Context mContext;
    //private final List<Integer> mItems;
    //private int mCurrentItemId = 0;
    private static ArrayList<PhotoObject> mItems = new ArrayList<PhotoObject>();

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        //public final TextView title;
        public final SimpleDraweeView photo;

        public SimpleViewHolder(View view) {
            super(view);
            //title = (TextView) view.findViewById(R.id.title);
            photo = view.findViewById(R.id.gallery_photo);
        }
    }

    public SimpleAdapter(Context context, ArrayList<PhotoObject> items) {
        mContext = context;
        this.mItems = items;
       /* mItems = new ArrayList<Integer>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            addItem(i);
        }*/
    }

    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.gallery_item, parent, false);

        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        if (!mItems.isEmpty()) {
            //holder.title.setText(mItems.get(position).getPHOTO_URI());
            holder.photo.setImageURI(mItems.get(position).getPHOTO_URIToUri());
            holder.photo.setOnClickListener(_view-> {
                if (position != RecyclerView.NO_POSITION) {
                    Context context = _view.getContext();
                    Intent intent =  new Intent(context, PhotoActivity.class);
                    intent.putExtra("URI", mItems.get(position).getPHOTO_URI());
                    context.startActivity(intent);
                }
            });
        }
    }
/*
    public void addItem(int position) {
        final int id = mCurrentItemId++;
        mItems.add(position, id);
        notifyItemInserted(position);
    }*/

 /*   public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }*/

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}