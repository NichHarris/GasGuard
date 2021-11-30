package com.example.minicapstone390.Models;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

public class Graphing {

    private XAxis xAxis;
    private BarChart barChart;
    private LineChart lineChart;

    // Initialize graph
    public Graphing(LineChart chart) {
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        this.lineChart = chart;
        this.barChart = null;
    }

    public Graphing(BarChart chart) {
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        this.barChart = chart;
        this.lineChart = null;
    }

    public void setXAxis(ArrayList<Device> deviceData) {
        ArrayList<String> xAxisLabels = new ArrayList<>(deviceData.size());

        for (int i = 0; i < deviceData.size(); i++) {
            if (!xAxisLabels.contains(deviceData.get(i).getDeviceName())) {
                xAxisLabels.add(deviceData.get(i).getDeviceName());
            }
        }

        if (this.lineChart == null) {
            xAxis = lineChart.getXAxis();
        } else {
            xAxis = barChart.getXAxis();
        }

        xAxis.setLabelCount(deviceData.size() + 1, true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value < xAxisLabels.size()) {
                    return xAxisLabels.get((int) value);
                } else {
                    return "";
                }
            }
        });
    }
}
