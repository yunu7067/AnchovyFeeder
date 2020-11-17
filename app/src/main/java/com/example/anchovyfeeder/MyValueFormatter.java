package com.example.anchovyfeeder;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class MyValueFormatter extends ValueFormatter {
    private DecimalFormat format = new DecimalFormat("###,##0.#");
    private String unit = null;

    public void setUnit(String unit) {
        this.unit = " " + unit;
    }

    @Override
    public String getPointLabel(Entry entry) {
        //return super.getPointLabel(entry);
        return format.format(entry.getY()) + unit;
    }

    @Override
    public String getBarLabel(BarEntry barEntry) {
        return super.getBarLabel(barEntry);
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return super.getAxisLabel(value, axis);
    }
}