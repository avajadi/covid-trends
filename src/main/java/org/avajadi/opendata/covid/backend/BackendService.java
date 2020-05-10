package org.avajadi.opendata.covid.backend;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import org.avajadi.opendata.covid.backend.util.TimeSeriesUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class BackendService {
    private static final String CONFIRMED_CASES_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("M/d/yy");
    private static final int REGION = 0;
    private static final int COUNTRY = 1;
    private static final int DATA_START = 4;
    private static BackendService INSTANCE;
    private final List<LocalDate> dates = new ArrayList<>();
    private Map<Country, TimeSeries<Double>> countryTimeSeriesMap;
    private Instant lastLoaded;

    private BackendService() {
        loadData();
    }

    public static BackendService getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new BackendService();
        }
        return INSTANCE;
    }

    public void loadData() {
        // If lastLoaded is set to today, don't reload data
        if (null != lastLoaded && LocalDate.now().isEqual(lastLoaded.atZone(ZoneId.systemDefault()).toLocalDate())) {
            return;
        }
        countryTimeSeriesMap = new HashMap<>();
        BufferedReader in = null;
        try {
            URL confirmedURL = new URL(CONFIRMED_CASES_URL);
            in = new BufferedReader(
                    new InputStreamReader(confirmedURL.openStream()));
            CSVReader csvReader = new CSVReader(in);
            List<String[]> all = csvReader.readAll();
            //Drop headers, after extracting dates
            extractDates(all.remove(0));
            for (String[] entry : all) {
                Country country = Country.of(entry[COUNTRY]);
                readDataIntoTimeSeries(country, entry, dates);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != in)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lastLoaded = Instant.now();
    }

    private void extractDates(String[] header) {
        for (int i = DATA_START; i < header.length; i++) {
            dates.add(LocalDate.parse(header[i], datePattern));
        }
    }

    private void readDataIntoTimeSeries(Country country, String[] entry, List<LocalDate> dates) {
        TimeSeries<Double> ts = countryTimeSeriesMap.get(country);
        if (null == ts) {
            ts = new TimeSeries();
            countryTimeSeriesMap.put(country, ts);
        }
        for (int i = DATA_START; i < entry.length && i < dates.size(); i++) {
            try {
                LocalDate date = dates.get(i);
                Double value = Double.valueOf(entry[i]);
                TimeSeriesUtil.addTo(ts, date, value);
            } catch (NumberFormatException e) {
                System.out.println("NFE column " + i + " value " + entry[i]);
            }
        }
    }

    public TimeSeries getTimeSeries(String countryName) {
        Country country = Country.of(countryName);
        return getTimeSeries(country);
    }

    public TimeSeries getTimeSeries(Country country) {
        return countryTimeSeriesMap.get(country);
    }

    public List<LocalDate> getDates() {
        return dates;
    }

}
