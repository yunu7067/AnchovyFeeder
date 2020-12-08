package com.example.anchovyfeeder.foodrecommender;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.anchovyfeeder.R;
import com.example.anchovyfeeder.realmdb.FoodObject;

import java.util.ArrayList;

public class FoodRecommenderAdapter extends RecyclerView.Adapter<FoodRecommenderAdapter.ViewHolder>  {
    private ArrayList<FoodObject> mData = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1, desc;

        ViewHolder(View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.food_recommender_item_title);
            desc = itemView.findViewById(R.id.food_recommender_item_data);
        }
    }

    public FoodRecommenderAdapter(ArrayList<FoodObject> list) {
        mData = list;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public FoodRecommenderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.food_recommender_item, parent, false);
        FoodRecommenderAdapter.ViewHolder vh = new FoodRecommenderAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(FoodRecommenderAdapter.ViewHolder holder, int position) {
        FoodObject food = mData.get(position);
        holder.textView1.setText(food.getFOOD_NAME());
        holder.desc.setText("(" + food.getKCAL() + "kcal)");
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
