package com.vornitologist;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vornitologist.ui.ClassificationHierarchy;
import com.vornitologist.ui.ClassificationSubView;
import com.vornitologist.ui.LatestObservations;
import com.vornitologist.ui.MainTabsheet;
import com.vornitologist.ui.MapView;
import com.vornitologist.ui.SettingsView;
import com.vornitologist.ui.VornitologistMainView;
import com.vornitologist.util.Translations;

/**
 * The application class for Vornitologist.
 * <p>
 * Application class takes care of application initialization, setting various
 * configurations and storing application instance wide data (commonly static
 * fields in e.g. Swing applications).
 */
@Theme("vornitologist")
@Widgetset("com.vornitologist.widgetset.VornitologistWidgetset")
@PreserveOnRefresh
@Title("Vornitologist")
public class VornitologistUI extends UI {

    /*
     * Default the location to Vaadin HQ
     */
    private double currentLatitude = 60.452541;
    private double currentLongitude = 22.30083;
    private String user;
    private VornitologistOfflineModeExtension offlineModeSettings;

    public VornitologistUI() {

    }

    @Override
    public void init(VaadinRequest request) {
        // Set a nice default for user for demo purposes: Eräjorma,
        // Skogsbörje...
        setUser(Translations.get(getLocale()).getObject("Willy Wilderness")
                .toString());

        final MainTabsheet mainTabSheet = new MainTabsheet();

        setNavigator(new Navigator(this, new ViewDisplay() {

            @Override
            public void showView(View view) {
                if (view instanceof VornitologistMainView) {
                    VornitologistMainView mainView = (VornitologistMainView) view;
                    mainTabSheet.setSelectedTab(mainView);
                    if (view instanceof ClassificationHierarchy) {
                        ClassificationHierarchy hierarchy = (ClassificationHierarchy) view;
                        hierarchy.getView("Aves");
                    }
                } else {
                    ClassificationHierarchy hierarchy = mainTabSheet
                            .getClassificationHierarchy();
                    if (mainTabSheet.getSelelectedTab().getComponent() != hierarchy) {
                        mainTabSheet.setSelectedTab(hierarchy);
                    }
                    hierarchy.navigateToSubView((ClassificationSubView) view);
                }
            }
        }));

        getNavigator().addProvider(new ViewProvider() {

            @Override
            public String getViewName(String viewAndParameters) {
                if (viewAndParameters.isEmpty()) {
                    return SettingsView.class.getSimpleName();
                }
                return viewAndParameters;
            }

            @Override
            public View getView(String viewName) {

                /*
                 * Handle mainViews
                 */
                if (viewName.equals("Aves")) {
                    return mainTabSheet.getClassificationHierarchy();
                }
                if (viewName.equals(SettingsView.class.getSimpleName())) {
                    return mainTabSheet.getSettingsView();
                }
                if (viewName.equals(MapView.class.getSimpleName())) {
                    return mainTabSheet.getMapView();
                }
                if (viewName.equals(LatestObservations.class.getSimpleName())) {
                    return mainTabSheet.getLatestObservations();
                }

                /*
                 * All other fragment based views are subviews for
                 * classification hierarchy.
                 */
                ClassificationHierarchy classificationHierarchy = mainTabSheet
                        .getClassificationHierarchy();
                if (mainTabSheet.getSelelectedTab().getComponent() != classificationHierarchy) {
                    // ensure right tab
                    mainTabSheet.setSelectedTab(classificationHierarchy);
                }
                String speciesOrGroup = viewName;
                return classificationHierarchy.getView(speciesOrGroup);
            }
        });

        String uriFragment = getPage().getUriFragment();
        if (uriFragment != null && uriFragment.startsWith("!")) {
            getNavigator().navigateTo(getPage().getUriFragment().substring(1));
        }

        setContent(mainTabSheet);

        // Use Vornitologists custom offline mode
        offlineModeSettings = new VornitologistOfflineModeExtension();
        offlineModeSettings.extend(this);
        offlineModeSettings.setPersistentSessionCookie(true);
        offlineModeSettings.setOfflineModeEnabled(true);
        
    }

    public void goOffline() {
        offlineModeSettings.goOffline();
    }

    /**
     * The location information is stored in Application instance to be
     * available for all components. It is detected by the map view during
     * application init, but also used by other maps in the application.
     * 
     * @return the current latitude as degrees
     */
    public double getCurrentLatitude() {
        return currentLatitude;
    }

    /**
     * @return the current longitude as degrees
     * @see #getCurrentLatitude()
     */
    public double getCurrentLongitude() {
        return currentLongitude;
    }

    /**
     * @see #getCurrentLatitude()
     */
    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    /**
     * @see #getCurrentLatitude()
     */
    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    /**
     * A typed version of {@link UI#getCurrent()}
     * 
     * @return the currently active Vornitologist UI.
     */
    public static VornitologistUI getApp() {
        return (VornitologistUI) UI.getCurrent();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
