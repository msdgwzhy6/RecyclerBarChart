package com.yxc.barchart;

import android.util.Log;

import com.yxc.chartlib.attrs.BarChartAttrs;
import com.yxc.chartlib.entrys.BarEntry;
import com.yxc.commonlib.util.TimeUtil;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yxc
 * @date 2019/4/10
 */
public class RateTestData {

    // 创建 月视图的数据
    public static List<BarEntry> getMonthEntries(BarChartAttrs attrs, LocalDate localDate, int length, int originEntrySize) {
        List<BarEntry> entries = new ArrayList<>();
        long timestamp = TimeUtil.changZeroOfTheDay(localDate);
        for (int i = originEntrySize; i < originEntrySize + length; i++) {
            if (i > originEntrySize) {
                timestamp = timestamp - TimeUtil.TIME_DAY;
            }
            float mult = 20;
            float value = (float) (Math.random() * 100) + mult;
            value = Math.round(value);
            int type = BarEntry.TYPE_XAXIS_THIRD;
            LocalDate localDateEntry = TimeUtil.timestampToLocalDate(timestamp);
            boolean isLastDayOfMonth = TimeUtil.isLastDayOfMonth(localDateEntry);
            int dayOfYear = localDateEntry.getDayOfYear();
            Log.d("TestData", "dayOfYear:" + dayOfYear + " localDate:" + localDateEntry);
            if (isLastDayOfMonth && (dayOfYear+1) % attrs.xAxisScaleDistance == 0) {
                type = BarEntry.TYPE_XAXIS_SPECIAL;
            } else if (isLastDayOfMonth) {
                type = BarEntry.TYPE_XAXIS_FIRST;
            } else if ((dayOfYear+1) % attrs.xAxisScaleDistance == 0) {
                type = BarEntry.TYPE_XAXIS_SECOND;
            }

            if (TimeUtil.isFuture(localDateEntry)) {
                value = 0;
            }
            BarEntry barEntry = new BarEntry(i, value, timestamp, type);
            barEntry.localDate = localDateEntry;
            entries.add(barEntry);
        }
        Collections.sort(entries);
        return entries;
    }

    //创建Week视图的数据
    public static List<BarEntry> createWeekEntries(LocalDate localDate, int length, int originEntrySize) {
        List<BarEntry> entries = new ArrayList<>();
        long timestamp = TimeUtil.changZeroOfTheDay(localDate);
        for (int i = originEntrySize; i < originEntrySize + length; i++) {
            if (i > originEntrySize) {
                timestamp = timestamp - TimeUtil.TIME_DAY;
            }
            float mult = 20;
            float value = (float) (Math.random() * 100) + mult;
            value = Math.round(value);
            int type = BarEntry.TYPE_XAXIS_SECOND;
            LocalDate localDateEntry = TimeUtil.timestampToLocalDate(timestamp);
            boolean isSunday = TimeUtil.isSunday(localDateEntry);
            if (isSunday) {
                type = BarEntry.TYPE_XAXIS_FIRST;
            }
            if (TimeUtil.isFuture(localDateEntry)){
                value = 0;
            }
            BarEntry barEntry = new BarEntry(i, value, timestamp, type);
            barEntry.localDate = localDateEntry;
            entries.add(barEntry);
        }
        Collections.sort(entries);
        return entries;
    }


    //创建 Day视图的数据
    public static List<BarEntry> createDayEntries(BarChartAttrs attrs, long timestamp, int length, int originEntrySize, boolean zeroValue) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = originEntrySize; i < length + originEntrySize; i++) {
            if (i > originEntrySize){
                timestamp = timestamp - TimeUtil.TIME_HOUR;
            }
            float mult = 40;
            float value = (float) (Math.random() * 100) + mult;
            value = Math.round(value);
            int type = BarEntry.TYPE_XAXIS_THIRD;
            boolean isLastHourOfTheDay = TimeUtil.isLastHourOfTheDay(timestamp);
            LocalDate localDateEntry = TimeUtil.timestampToLocalDate(timestamp);
            int hourOfTheDay = TimeUtil.getHourOfTheDay(timestamp);
            if (isLastHourOfTheDay && (hourOfTheDay + 1) % attrs.xAxisScaleDistance == 0) {
                type = BarEntry.TYPE_XAXIS_SPECIAL;
            } else if (isLastHourOfTheDay) {
                type = BarEntry.TYPE_XAXIS_FIRST;
            } else if ((hourOfTheDay + 1) % attrs.xAxisScaleDistance == 0) {
                type = BarEntry.TYPE_XAXIS_SECOND;
            }
            if (TimeUtil.isFuture(timestamp)){
                value = 0;
            }
            BarEntry barEntry = new BarEntry(i, value, timestamp, type);
            barEntry.localDate = localDateEntry;
            entries.add(barEntry);
        }
        Collections.sort(entries);
        return entries;
    }


    //创建 year视图的数据
    public static List<BarEntry> createYearEntries(LocalDate localDateParams, int length, int originEntrySize) {
        LocalDate localDate = localDateParams;
        List<BarEntry> entries = new ArrayList<>();
        for (int i = originEntrySize; i < length + originEntrySize; i++) {
            if (i > originEntrySize) {
                localDate = localDate.minusMonths(1);
            }
            float mult = 50;
            float value = (float) (Math.random() * 100) + mult;
            value = Math.round(value);
            int type = BarEntry.TYPE_XAXIS_SECOND;
            boolean isLastMonthOfTheYear = TimeUtil.isLastMonthOfTheYear(localDate);
            if (isLastMonthOfTheYear) {
                type = BarEntry.TYPE_XAXIS_FIRST;
            }
            long timestamp = TimeUtil.changZeroOfTheDay(localDate);
            if (TimeUtil.isFuture(localDate)){
                value = 0;
            }
            BarEntry barEntry = new BarEntry(i, value, timestamp, type);
            barEntry.localDate = localDate;
            entries.add(barEntry);
        }
        Collections.sort(entries);
        return entries;
    }

}
