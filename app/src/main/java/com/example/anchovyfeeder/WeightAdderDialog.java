package com.example.anchovyfeeder;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

public class WeightAdderDialog extends Dialog {

    public WeightAdderDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight_adder_dialog);

        findViewById(R.id.dialog_header_exit_button).setOnClickListener(v -> {dismiss();});
        /*
        final TextInputLayout textInputLayout = findViewById(R.id.alarm_dialog_alarm_name_layout);
        final TimePicker timePicker = findViewById(R.id.alarm_dialog_time_picker);
        Button saveButton = findViewById(R.id.alarm_dialog_save_button);

        */

        /*
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
        });*/
    }

}
