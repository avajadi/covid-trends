package org.avajadi.opendata.covid.views.dashboard;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.avajadi.opendata.covid.Version;
import org.avajadi.opendata.covid.backend.BackendService;
import org.avajadi.opendata.covid.backend.Country;
import org.avajadi.opendata.covid.backend.TimeSeries;
import org.avajadi.opendata.covid.backend.util.TimeSeriesUtil;
import org.avajadi.opendata.covid.views.main.MainView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Route(value = "", layout = MainView.class)
@PageTitle("covid-19 trends")
@CssImport(value = "styles/views/dashboard/dashboard-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class DashboardView extends VerticalLayout implements HasUrlParameter<String> {


    private final H2 usersH2 = new H2();
    private final H2 eventsH2 = new H2();
    private final H2 conversionH2 = new H2();
    private final List<Series> series;
    private final DateTimeFormatter outDateFormatter = DateTimeFormatter.ofPattern("d/M");
    private final Chart doublingPeriods = new Chart();
    private Country currentCountry = Country.of("Sweden");

    public DashboardView() {
        setId("dashboard-view");
        series = new ArrayList<>();
        Configuration configuration = doublingPeriods.getConfiguration();
        configuration.setTitle("Doubling period of confirmed cases");
        configuration.getChart().setType(ChartType.SPLINE);
        Credits credits = new Credits();
        credits.setText("All data comes from the GitHub repository managed bu Johns Hopkins University");
        credits.setHref("https://github.com/CSSEGISandData/COVID-19");
        configuration.setCredits(credits);
        configuration.setSeries(series);

        add(doublingPeriods);

        Div description = new Div();
        Span descContent = new Span("Doubling period is calculated using the formula ");
        Image formula = new Image();
        formula.setSrc("https://wikimedia.org/api/rest_v1/media/math/render/svg/137261413a0a52ccba07032f1abc0d6338e906ff");
        descContent.add(new Anchor("https://en.wikipedia.org/wiki/Doubling_time", formula));
        description.add(descContent);
        description.addClassName("description");

        add(description);

        Div versionFooter = new Div();
        versionFooter.addClassName("version-footer");
        Span versionContent = new Span("BuildTime:" + Version.getBuildTime());
        versionFooter.add(versionContent);
        add(versionFooter);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        BackendService.getInstance().loadData();
//        List<Series> s = doublingPeriods.getConfiguration().getSeries();
//        System.out.println("Number of series before clear " + series.size() + " [" + s.size() + "]");
        series.clear();
//        System.out.println("Number of series after clear " + series.size() + " [" + s.size() + "]");

        Configuration configuration = doublingPeriods.getConfiguration();
        Set<LocalDate> allDates = new HashSet<>();
//        for( String countryName : MainView.selectableCountries) {
//            Country c = Country.of(countryName);
        List<LocalDate> dates = addCountrySeries(currentCountry, configuration);
        allDates.addAll(dates);
//        }

        XAxis x = new XAxis();
        x.setCrosshair(new Crosshair());
        x.setTitle("Date");

        for (LocalDate date : allDates.stream().sorted().collect(Collectors.toList())) {
            x.addCategory(date.format(outDateFormatter));
        }
        configuration.addxAxis(x);

        YAxis y = new YAxis();
        y.setTitle("Doubling period, days");
        y.setMin(0);
        configuration.addyAxis(y);
    }

    private List<LocalDate> addCountrySeries(Country country, Configuration configuration) {
        // Ignore what was going on before there were 100 cases
        System.out.println("Current country is " + country);
        TimeSeries<Double> threshHeld = TimeSeriesUtil.onlyAfter(BackendService.getInstance().getTimeSeries(country), 100);

        // Apply the doubling period transform on the time series
        TimeSeries<Double> tss = TimeSeriesUtil.doublingPeriod(threshHeld);
        List<Double> doublingPLength = tss.getValues();
        ListSeries dailySeries = new ListSeries(country.getName());
        for (Double value : doublingPLength) {
            dailySeries.addData(value);
        }
        configuration.addSeries(dailySeries);

        // Make another series by applying a 5 day average transform
        TimeSeries<Double> fiveDayAverages = TimeSeriesUtil.averages(tss, 5);
        ListSeries avgSeries = new ListSeries(country + ": 5 day average");

        for (Double average : fiveDayAverages.getValues()) {
            avgSeries.addData(average);
        }
        configuration.addSeries(avgSeries);
        return tss.getDates();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        System.out.println("Got parameter: '" + s + "'");
        Country c = Country.get(s);
        if (null != c) {
            currentCountry = c;
        }
    }
}
