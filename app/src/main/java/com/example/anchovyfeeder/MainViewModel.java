package com.example.anchovyfeeder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {
    MutableLiveData<AlarmListItem> alarmList = new MutableLiveData<>();
    MutableLiveData<ArrayList<Entry>> calEntry = new MutableLiveData<>();
    MutableLiveData<ArrayList<Entry>> weightEntry = new MutableLiveData<>();

    private void loadUsers() {

    }
}
