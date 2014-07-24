package com.vornitologist.ui;

import java.util.ResourceBundle;

import com.vaadin.addon.touchkit.ui.TabBarView;
import com.vaadin.navigator.View;
import com.vaadin.ui.TabSheet.Tab;
import com.vornitologist.VornitologistUI;
import com.vornitologist.util.Translations;

/**
 * This is the main view for Vornitologist application. It displays a tabbar via
 * one can choose one of the sub views.
 */
public class MainTabsheet extends TabBarView {

    private MapView mapView;
    private LatestObservations latestObservations;
    private ClassificationHierarchy classificationHierarchy;
    private SettingsView settings;

    public MainTabsheet() {

        ResourceBundle tr = Translations.get(VornitologistUI.getApp()
                .getLocale());

        /*
         * Populate main views
         */
        classificationHierarchy = new ClassificationHierarchy();
        Tab tab = addTab(classificationHierarchy);
        tab.setStyleName("birdtab");
        tab.setCaption(tr.getString("Aves"));

        latestObservations = new LatestObservations();
        tab = addTab(latestObservations);
        tab.setStyleName("observationstab");
        tab.setCaption(tr.getString("Observations"));
        mapView = new MapView();
        tab = addTab(mapView);
        tab.setStyleName("maptab");
        tab.setCaption(tr.getString("Map"));
        settings = new SettingsView();
        tab = addTab(settings);
        tab.setStyleName("settingstab");
        tab.setCaption(tr.getString("Settings"));

        /*
         * Make settings view as the default. This would not be best option for
         * a real application, but it also serves as our demos welcome page.
         */
        setSelectedTab(settings);

    }

    /**
     * Latest observation view needs to do some cleanup to let garbage collector
     * to do its job. This is due to our simple in memory "service layer"
     * 
     * @see com.vaadin.ui.AbstractComponentContainer#detach()
     */
    @Override
    public void detach() {
        super.detach();
        latestObservations.cleanup();
    }

    public MapView getMapView() {
        return mapView;
    }

    public ClassificationHierarchy getClassificationHierarchy() {
        return classificationHierarchy;
    }

    public View getSettingsView() {
        return settings;
    }

    public View getLatestObservations() {
        return latestObservations;
    }
}
