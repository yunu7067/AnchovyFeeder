package com.example.anchovyfeeder;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class AlarmAddOrUpdateDialog extends Dialog implements View.OnClickListener {
    private ArrayList<AlarmListItem> mData = null;
    private int mPos = -1;
    private final RecyclerView.Adapter mAdapter;

    public AlarmAddOrUpdateDialog(@NonNull Context context, RecyclerView.Adapter adapter, ArrayList<AlarmListItem> list) {
        super(context);
        this.mData = list;
        this.mAdapter = adapter;
    }

    public void setItemPosition(@NonNull int pos) {
        // 알람 리스트의 위치를 설정하면, 아이템을 추가하는 것이 아니라 기존에 있는 아이템을 수정한다.
        this.mPos = pos;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_add_or_update_dialog);


        ImageButton exitButton = findViewById(R.id.alarm_dialog_alarm_exit_button);
        final TextInputLayout textInputLayout = findViewById(R.id.alarm_dialog_alarm_name_layout);
        final TimePicker timePicker = findViewById(R.id.alarm_dialog_time_picker);
        Button saveButton = findViewById(R.id.alarm_dialog_save_button);

        textInputLayout.setCounterMaxLength(10);
        textInputLayout.setCounterEnabled(true);

        if (mPos >= 0) {
            // 알람 이름 설정
            String text = mData.get(mPos).getName();
            textInputLayout.getEditText().setText(text);
            textInputLayout.getEditText().setSelection(text.length());
            // 알람 시간 설정
            timePicker.setHour(mData.get(mPos).getHour());
            timePicker.setMinute(mData.get(mPos).getMinute());
        }

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int min) {
                //Toast.makeText(timePicker.getContext(), "hour : " + hour + ", min : " + min, Toast.LENGTH_SHORT).show();
            }
        });


        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(), "종료", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toast = textInputLayout.getEditText().getText().toString();
                if ("".equals(toast)) {
                    textInputLayout.setError("빈칸이면 안됩니다.");
                } else {
                    AlarmListItem item = (mPos >= 0) ? (mData.get(mPos)) : (new AlarmListItem());
                    item.setName(toast);
                    item.setTime(timePicker.getHour(), timePicker.getMinute(), 00);
                    if(mPos < 0)
                        mData.add(item);
                    //toast += ", HH : " + timePicker.getHour() + ", MM : " + timePicker.getMinute();
                    //Toast.makeText(view.getContext(), toast, Toast.LENGTH_SHORT).show();
                    Toast.makeText(view.getContext(), "설정되었습니다.", Toast.LENGTH_SHORT).show();

                    mAdapter.notifyDataSetChanged();
                    dismiss();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
    }
}
