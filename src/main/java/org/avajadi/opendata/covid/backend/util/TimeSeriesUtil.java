package org.avajadi.opendata.covid.backend.util;

import org.avajadi.opendata.covid.backend.TimeSeries;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TimeSeriesUtil {
    public static TimeSeries<Double> doublingPeriod(TimeSeries<Double> ts) {
        TimeSeries<Double> changeSeries = new TimeSeries<>();
        double previous = 0;
        for (LocalDate date : ts.keySet().stream().sorted().collect(Collectors.toList())) {
            double value = ts.get(date);
            if (0 != previous && 0 != value && previous != value) {
                changeSeries.put(date, Math.log(2)/Math.log(value/previous));
            }
            previous = value;
        }
        return changeSeries;
    }

    public static TimeSeries<Double> averages(TimeSeries<Double> ts, int window) {
        AverageQueue avg = new AverageQueue(window);
        TimeSeries<Double> avgTs = new TimeSeries<>();
        for (LocalDate date : ts.getDates()) {
            avg.add(ts.get(date));
            avgTs.put(date, avg.average());
        }
        return avgTs;
    }

    public TimeSeries<Integer> change(TimeSeries<Integer> ts) {
        TimeSeries<Integer> changeSeries = new TimeSeries<>();
        int previous = 0;
        for (LocalDate date : ts.getDates()) {
            Integer value = ts.get(date);
            if (0 != previous) {
                changeSeries.put(date, value - previous);
            }
            previous = value;
        }
        return changeSeries;
    }

    public static TimeSeries<Double> onlyAfter(TimeSeries<Double> ts, int threshold ) {
        List<LocalDate> localDates = new ArrayList<>(ts.getDates());
        while (!localDates.isEmpty()) {
            if (ts.get(localDates.get(0)) >= threshold) {
                break;
            }
            localDates.remove(0);
        }
        TimeSeries<Double> onlyAfter = new TimeSeries<>();
        for( LocalDate localDate : localDates ){
            onlyAfter.put(localDate, ts.get(localDate));
        }
        return onlyAfter;
    }

    public static void addTo(TimeSeries<Double> ts, LocalDate date, double number) {
        Double current = ts.get(date);
        if (null == current) {
            current = 0.0;
        }
        ts.put(date, current + number);
    }

}
