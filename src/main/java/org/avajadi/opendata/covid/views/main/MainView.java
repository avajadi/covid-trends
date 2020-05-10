package org.avajadi.opendata.covid.views.main;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.avajadi.opendata.covid.backend.BackendService;
import org.avajadi.opendata.covid.backend.Country;
import org.avajadi.opendata.covid.views.dashboard.DashboardView;

import java.util.*;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@PWA(name = "Covid Trends", shortName = "Covid Trends")
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MainView extends AppLayout {

    private final Tabs menu;
    //public final static Collection<String> selectableCountries = Arrays.asList(new String[]{"Sweden"});// ,"Norway","Denmark","Finland"});
    public final static Collection<String> selectableCountries = Arrays.asList(new String[]{"Sweden","Norway","Denmark","Finland","Spain","Nigeria","United Kingdom","US"});
    public MainView() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, new DrawerToggle());
        BackendService.getInstance().loadData();
        menu = createMenuTabs();
        addToDrawer(menu);

    }


    private static Tabs createMenuTabs() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private static Tab[] getAvailableTabs() {
        final List<Tab> tabs = new ArrayList<>();
        Country.countries().stream().filter(c->{return selectableCountries.contains(c.getName());}).sorted(Comparator.comparing(Country::getName)).forEach(c->tabs.add(createTab(c.getName())));
        return tabs.toArray(new Tab[tabs.size()]);
    }

    private static Tab createTab(String title) {
        return createTab((new RouterLink(title, DashboardView.class, title)));
    }

    private static Tab createTab(Component content) {
        final Tab tab = new Tab();
        tab.add(content);
        return tab;
    }
    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        selectTab();
    }

    private void selectTab() {
        String target = RouteConfiguration.forSessionScope().getUrl(getContent().getClass());
        Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
            Component child = tab.getChildren().findFirst().get();
            return child instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
        }).findFirst();
        tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
    }
}
