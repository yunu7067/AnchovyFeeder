package com.example.anchovyfeeder;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class AlarmAddOrUpdateDialog extends Dialog {
    //private ArrayList<AlarmListItem> mData = null;
    private ArrayList<AlarmListItem> mData;

    private int mPos = -1;
    private final RecyclerView.Adapter mAdapter;

    public AlarmAddOrUpdateDialog(@NonNull Context context, RecyclerView.Adapter adapter, ArrayList<AlarmListItem> list) {
        super(context);

        //this.mData = MainViewModel.alarmList.getValue();
        this.mAdapter = adapter;
        this.mData = MainViewModel.alarmList.getValue();
    }

    public void setItemPosition(@NonNull int pos) {
        // 알람 리스트의 위치를 설정하면, 아이템을 추가하는 것이 아니라 기존에 있는 아이템을 수정한다.
        this.mPos = pos;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_add_or_update_dialog);

        ImageButton exitButton = findViewById(R.id.dialog_header_exit_button);
        final TextInputLayout textInputLayout = findViewById(R.id.alarm_dialog_alarm_name_layout);
        final TimePicker timePicker = findViewById(R.id.alarm_dialog_time_picker);
        Button saveButton = findViewById(R.id.dialog_save_button);

        textInputLayout.setCounterMaxLength(10);
        textInputLayout.setCounterEnabled(true);

        // Update Item
        if (mPos >= 0) {
            String originalName = mData.get(mPos).getName();
            int originalHour = mData.get(mPos).getHour();
            int originalMinute = mData.get(mPos).getMinute();
            // 알람 이름 설정
            textInputLayout.getEditText().setText(originalName);
            textInputLayout.getEditText().setSelection(originalName.length());
            // 알람 시간 설정
            timePicker.setHour(originalHour);
            timePicker.setMinute(originalMinute);
        }

        exitButton.setOnClickListener(view -> {
            dismiss();
        });

        saveButton.setOnClickListener(view -> {
            Context context = view.getContext();
            String thisName = textInputLayout.getEditText().getText().toString();
            int thisHour = timePicker.getHour();
            int thisMinutetime = timePicker.getMinute();
            boolean isExistSameTime = false;

            for (AlarmListItem item : mData) {
                // 자기 자신 제외
                if ((mPos != -1) && (mData.get(mPos).equals(item)))
                    continue;
                if ((item.getHour() == thisHour) && (item.getMinute() == thisMinutetime)) {
                    Toast.makeText(context, "동일한 시간에 이미 알람이 설정되어 있습니다. 시간을 변경해 주세요.", Toast.LENGTH_SHORT).show();
                    isExistSameTime = true;
                    break;
                }
            }

            if (!isExistSameTime) {
                if ("".equals(thisName)) {
                    textInputLayout.setError("빈칸이면 안됩니다.");
                } else {
                    AlarmListItem item = (mPos >= 0) ? (mData.get(mPos)) : (new AlarmListItem());
                    item.setName(thisName);
                    item.setTime(thisHour, thisMinutetime, 00);

                    if (mPos >= 0) {
                        mData.set(mPos, item);
                    } else {
                        mData.add(item);
                    }
                    MainViewModel.setAlarmList(mData);
                    //Toast.makeText(context, "설정되었습니다.", Toast.LENGTH_SHORT).show();

                    mAdapter.notifyDataSetChanged();
                    dismiss();
                }
            }
        });
    }

}
