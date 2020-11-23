package com.example.anchovyfeeder;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.anchovyfeeder.realmdb.DailyDataObject;
import com.example.anchovyfeeder.realmdb.WeightObject;
import com.google.android.material.textfield.TextInputLayout;

import io.realm.Realm;

public class WeightAdderDialog extends Dialog {

    public WeightAdderDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight_adder_dialog);

        findViewById(R.id.dialog_header_exit_button).setOnClickListener(v -> {
            dismiss();
        });
        Button saveButton = findViewById(R.id.dialog_save_button);

        final TextInputLayout textInputLayout = findViewById(R.id.textInputLayout);
        final DatePicker datePicker = findViewById(R.id.datePicker);
        saveButton.setOnClickListener(view -> {
            String inputText = textInputLayout.getEditText().getText().toString();
            int selectedYear = datePicker.getYear();
            int selectedMonth = datePicker.getMonth();
            int selectedDate = datePicker.getDayOfMonth();

            if ("".equals(inputText)) {
                textInputLayout.setError("빈칸이면 안됩니다.");
            } else {
                Double inputWeight = Double.valueOf(inputText);
                if (inputWeight > 120.0 || inputWeight < 30.0) {
                    textInputLayout.setError("30Kg ~ 120Kg까지 지원됩니다.");
                } else {
                    /*WeightObject weight = new WeightObject();
                    weight.set(selectedYear, selectedMonth, selectedDate, inputWeight);
                    // 이하 Realm DB에 데이터 검사 후 입력
                    WeightObject temp = MainViewModel.weightsThisMonth.where().equalTo("DATE", weight.getDATE()).findFirst();*/
                    DailyDataObject daily = new DailyDataObject();
                    daily.setDATE(selectedYear, selectedMonth, selectedDate);
                    daily.setWEIGHT(inputWeight);
                    android.util.Log.i("WeightAdderDialog()", "1 " + selectedYear + "/" + selectedMonth + "/" + selectedDate);

                    //WeightObject temp = MainViewModel.weightsThisMonth.where().equalTo("DATE", daily.getDATE()).findFirst();
                    DailyDataObject temp = MainViewModel.DailyDatas.where().equalTo("DATE", daily.getDATE()).findFirst();
                    if (temp == null) {
                        android.util.Log.i("WeightAdderDialog()", "2-1");

                        //Realm realm = MainViewModel.weightsThisMonth.getRealm();
                        Realm realm = MainViewModel.DailyDatas.getRealm();
                        realm.beginTransaction();
                        //WeightObject wit = realm.copyToRealm(weight);
                        DailyDataObject odj = realm.copyToRealm(daily);
                        realm.commitTransaction();
                        dismiss();
                    } else {
                        android.util.Log.i("WeightAdderDialog()", "2-2");

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        // 취소 버튼 클릭시 설정, 왼쪽 버튼입니다.
                        builder.setTitle("해당 날짜에 이미 저장된 몸무게가 있습니다.")
                                .setMessage("덮어씌우시겠습니까?")
                                .setCancelable(false)
                                .setPositiveButton("확인", (dialog, whichButton) -> {
                                    //Realm realm = MainViewModel.weightsThisMonth.getRealm();
                                    Realm realm = MainViewModel.DailyDatas.getRealm();
                                    realm.beginTransaction();
                                    temp.setWEIGHT(inputWeight);
                                    realm.commitTransaction();
                                    dismiss();
                                })
                                .setNegativeButton("취소",  null);
                        AlertDialog dialog = builder.create();    // 알림창 객체 생성
                        dialog.show();    // 알림창 띄우기
                    }
                    //Toast.makeText(view.getContext(), weight.getDATE() + ", " + weight.getWEIGHT() + "Kg", Toast.LENGTH_SHORT).show();
                    Toast.makeText(view.getContext(), daily.getDATE() + ", " + daily.getWEIGHT() + "Kg", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
