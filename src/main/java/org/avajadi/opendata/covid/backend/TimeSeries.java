package org.avajadi.opendata.covid.backend;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TimeSeries<T> extends HashMap<LocalDate, T> {
    private static final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("d/M");

    public List<T> getValues(List<LocalDate> dates) {
        List<T> values = new ArrayList<T>();
        for (LocalDate date : dates) {
            values.add(get(date));
        }
        return values;
    }


    public List<T> getValues() {
        return getValues(getDates());
    }

    public List<LocalDate> getDates() {
        return keySet().stream().sorted().collect(Collectors.toList());
    }
}
