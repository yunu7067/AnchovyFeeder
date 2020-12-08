package com.example.anchovyfeeder;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.anchovyfeeder.realmdb.DailyDataObject;
import com.example.anchovyfeeder.realmdb.FoodObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;

public class CalorieAdderDialog extends Dialog {
    FoodObject selectedFood;

    public CalorieAdderDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calorie_adder_dialog);

        final DatePicker datePicker = findViewById(R.id.datePicker);
        Button saveButton = findViewById(R.id.dialog_save_button);
        TextView foodNameTextView = findViewById(R.id.foodName);
        ImageButton searchButton = findViewById(R.id.foodSearchButton);
        final EditText foodCount = findViewById(R.id.foodCount);

        // x 버튼
        findViewById(R.id.dialog_header_exit_button).setOnClickListener(view -> {
            dismiss();
        });

        // 음식 검색 버튼
        searchButton.setOnClickListener(view -> {
            new SimpleSearchDialogCompat(getContext(), "검색된 음식", "검색하려는 음식 이름을 입력해 주세요!", null, createData(),
                    (SearchResultListener<FoodObject>) (dialog, item, position) -> {
                        selectedFood = item;
                        foodNameTextView.setText(selectedFood.getTitle());
                        searchButton.setImageDrawable(
                                ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_youtube_searched_for_24));
                        dialog.dismiss();
                    }
            ).show();
        });

        // 저장 버튼
        saveButton.setOnClickListener(view -> {
            int selectedYear = datePicker.getYear();
            int selectedMonth = datePicker.getMonth();
            int selectedDate = datePicker.getDayOfMonth();

            if (selectedFood == null) {
                android.util.Log.i("CalorieAdderDialog()", "1");
                Toast.makeText(getContext(), "음식을 선택하세요", Toast.LENGTH_SHORT).show();
            } else if ("".equals(foodCount.getText().toString()) || Integer.valueOf(foodCount.getText().toString()) < 1 || Integer.valueOf(foodCount.getText().toString()) > 100) {
                android.util.Log.i("CalorieAdderDialog()", "1");
                Toast.makeText(getContext(), "개수를 정확히 입력하세요", Toast.LENGTH_SHORT).show();
            } else {
                android.util.Log.i("CalorieAdderDialog()", "2");
                int count = Integer.valueOf(foodCount.getText().toString());
                Realm realm = MainViewModel.DailyDatas.getRealm();
                DailyDataObject daily = new DailyDataObject();
                daily.setDATE(selectedYear, selectedMonth, selectedDate);
                RealmList<FoodObject> foods = new RealmList<>();
                for (int i = 0; i < count; i++) {
                    try {
                        foods.add((FoodObject) selectedFood.clone());
                    } catch (CloneNotSupportedException e) {
                    }
                }
                daily.setFOODS(foods);
                // 해당 날짜에 DailyDataObject가 존재하는지 확인
                DailyDataObject temp = MainViewModel.DailyDatas.where().equalTo("DATE", daily.getDATE()).findFirst();

                realm.beginTransaction();
                if (temp == null) {
                    android.util.Log.i("CalorieAdderDialog()", "2-1");
                    // 존재하지 않으면 그대로 복사.
                    realm.copyToRealm(daily);
                } else {
                    android.util.Log.i("CalorieAdderDialog()", "2-2");
                    // 존재하면 음식 목록만 추가
                    temp.appendFOODS(foods);
                }
                realm.commitTransaction();
                dismiss();
            }
        });
    }


    private void immediateSearch(String query) {
        Log.i("searchView", "immediateSearch : " + query);
    }

    private ArrayList<FoodObject> createData() {
        return MainViewModel.foodList;
    }

}
