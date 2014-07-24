package com.vornitologist.widgetset.client;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.vaadin.addon.touchkit.gwt.client.offlinemode.OfflineMode;
import com.vaadin.addon.touchkit.gwt.client.ui.VNavigationBar;
import com.vaadin.addon.touchkit.gwt.client.ui.VNavigationView;
import com.vaadin.addon.touchkit.gwt.client.ui.VerticalComponentGroupWidget;
import com.vaadin.client.ui.VButton;
import com.vaadin.client.ui.VOverlay;

/**
 * This is an example of overridden offline functionality in a TouchKit
 * application.
 * <p>
 * As you have realized, writing offline apps is rather odd solution for Vaadin,
 * which tries to simplify your development environment by letting you develop
 * in server side JVM where all great libraries exist and where you don't need
 * to consider things like networking at all. However, in some apps a limited
 * offline app might be handy and in some cases it might be a critical
 * requirement. This example can be used as a basis for creating a portion of
 * app to work in offline mode. Beware that stepping into this direction will
 * throw you deep into the "web development h€11", so consider keeping your
 * offline portions as small and simple as possible.
 * <p>
 * This demo offline mode lets users to fill observations also in offline mode.
 * When user returns to full featured online mode, it asks whether to
 * synchronize these observations to server.
 * <p>
 * How things work from technical POV?
 * <ul>
 * <li>TouchKit intelligently packages "static resources" so that browser caches
 * them more stronger than commonly. This webapp can be started from home screen
 * even without network connection.
 * <li>If TouchKit recognizes that there is no working connection to the server,
 * it opens the offline mode which is in this case defined in this class.
 * <li>VornitolgistOffline mode is built with GWT spices with some Vaadin and
 * TouchKit specific client side widgets and APIs. With them developer can write
 * the offline mode in Java, but note that the code is not run in a JVM so you
 * will have the usual GWT restrictions. Vornitogist shows a limited UI for
 * adding observations.
 * <li>Observations are temporary persisted to HTML5 local storage.
 * <li>The app can be closed, reopend and even the device can be rebooted.
 * Offline persisted observations remain in the memory of the device.
 * <li>When the application can return to full featured server backed mode, it
 * asks if user wishes to synchronize observations from local storage to the
 * server so that all other users van also see them.
 * </ul>
 */
public class VornitologistOfflineMode extends VOverlay implements OfflineMode,
        RepeatingCommand {

    private TextBox countBox;
    private ListBox speciesBox;
    private VButton addObservation;
    private Species selectedSpecies;
    private Integer count;

    private int localCount = -1;
    private Label currentlyStoredLabel;
    private VButton goOnlineButton;
    private Label networkStatus;
    private FlowPanel panel;

    private void buildUi(final List<Species> birds) {
        addStyleName("v-window");
        addStyleName("v-touchkit-offlinemode");
        Style style = getElement().getStyle();
        style.setZIndex(30001); // Make sure this is over the indicator


        final ListBox listBox = new ListBox();
        listBox.setHeight("25px");
        listBox.getElement().getStyle().setMarginTop(10, Unit.PX);
        listBox.getElement().getStyle().setMarginBottom(10, Unit.PX);
        listBox.addItem("-- Select species --");
        for (Species species : birds) {
            listBox.addItem(species.getDisplayString(), species.getId());
        }

        listBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                if (listBox.getSelectedIndex() == 0) {
                    selectedSpecies = null;
                } else {
                    selectedSpecies = birds.get(listBox.getSelectedIndex() - 1);
                    // countBox.setFocus(true);
                }
                updateFormState();
            }
        });
        speciesBox = listBox;

        // FIXME can't use NumberFieldWidget as it extends VTextField which
        // cannot be used separately (This was actually possible in V6...)
        countBox = new TextBox();
        countBox.setStyleName("v-touchkit-numberfield");
        countBox.getElement().setPropertyString("type", "number");
        countBox.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    public void execute() {
                        String value = countBox.getValue();
                        String sanitetized = value.replaceAll("[^0-9]", "");
                        if (!sanitetized.equals(value)) {
                            countBox.setValue(sanitetized);
                        }
                        value = sanitetized;
                        try {
                            count = Integer.parseInt(value);
                        } catch (Exception e) {
                            count = null;
                        }
                        updateFormState();
                    }
                });
            }
        });

        addObservation = new VButton();
        addObservation.setText("Add");
        addObservation.setEnabled(false);
        addObservation.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Observation obs = new Observation();
                obs.setSpeciesId(selectedSpecies.getId());
                obs.setCount(count);
                OfflineDataService.localStoreObservation(obs);
                localCount++;
                updateLocalCount();
                resetForm();
            }

        });

        /*
         * We'll mostly use TouchKit's client side components to build to UI and
         * some of TouchKit's style names to build the offline UI. This way we
         * can get similar look and feel with the rest of the application.
         */
        VNavigationView navigationView = new VNavigationView();
        setWidget(navigationView);
        navigationView.setHeight("100%");
        VNavigationBar navigationBar = new VNavigationBar();
        navigationBar.setCaption("Vornitologist is offline");
        navigationView.setNavigationBar(navigationBar);

        /*
         * FlowPanel is the simples GWT panel, pretty similar to CssLayout in
         * Vaadin. We can use it with some Vaadin stylenames to get e.g.
         * similarly themed margin widths.
         */
        panel = new FlowPanel();

        Label label = new Label("Offline Mode");
        label.setStyleName("v-label-grey-title");
        panel.add(label);

        VerticalComponentGroupWidget p = new VerticalComponentGroupWidget();
        p.add(new HTML(
                "<p>Vornitologist is currently offline and functionality is limited. "
                        + "Although the collaborative features cannot work offline, you can still add new observations. "
                        + "The added observations will be synchronized with other users the next time you go online.</p>"));

        panel.add(p);

        label = new Label("Add observations");
        label.setStyleName("v-label-grey-title");
        panel.add(label);
        p = new VerticalComponentGroupWidget();

        speciesBox.setWidth("100%");
        p.add(speciesBox);
        p.updateCaption(speciesBox, "Select species:", null, "100.0%", "v-caption");

        countBox.setWidth("100%");
        p.add(countBox);
        p.updateCaption(countBox, "Count:", null, "100.0%",  "v-caption");

        panel.add(p);
        
        panel.add(addObservation);

        label = new Label("Unsynchronized observations");
        label.setStyleName("v-label-grey-title");
        panel.add(label);
        p = new VerticalComponentGroupWidget();

        currentlyStoredLabel = null;
        updateLocalCount();
        p.add(currentlyStoredLabel);

        panel.add(p);

        showRestartButton();

        navigationView.setContent(panel);

        currentlyStoredLabel.getElement().getStyle().setPaddingTop(10, Unit.PX);
        currentlyStoredLabel.getElement().getStyle()
                .setPaddingBottom(10, Unit.PX);
        
        setShadowEnabled(false);
        show();
        getElement().getStyle().setWidth(100, Unit.PCT);
        getElement().getStyle().setHeight(100, Unit.PCT);
        getElement().getFirstChildElement().getStyle().setHeight(100, Unit.PCT);
    }

    private void resetForm() {
        speciesBox.setSelectedIndex(0);
        countBox.setText("");
        selectedSpecies = null;
        updateFormState();
    }

    protected void updateLocalCount() {
        if (localCount == -1) {
            localCount = OfflineDataService.getStoredObservations();
        }
        if (currentlyStoredLabel == null) {
            currentlyStoredLabel = new Label();
        }
        currentlyStoredLabel.setText("You currently have " + localCount
                + " unsynchronized observations in local storage.");
    }

    private void updateFormState() {
        boolean readyToAddObservation = true;

        if (selectedSpecies == null) {
            readyToAddObservation = false;
        } else {
            speciesBox.getElement().getStyle().setBackgroundColor("");
        }

        if (count == null) {
            readyToAddObservation = false;
        } else {
            countBox.getElement().getStyle().setBackgroundColor(null);
        }

        addObservation.setEnabled(readyToAddObservation);

    }

    private void goOnline() {
        Window.Location.reload();
    }

    private void showRestartButton() {

        Label label = new Label("Connection status");
        label.setStyleName("v-label-grey-title");
        panel.add(label);
        VerticalComponentGroupWidget vVerticalComponentGroup = new VerticalComponentGroupWidget();
        vVerticalComponentGroup
                .addStyleName("v-touchkit-verticalcomponentgroup");

        goOnlineButton = new VButton();
        goOnlineButton.setText("Go online");
        goOnlineButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                goOnline();
            }
        });

        goOnlineButton.setVisible(false);

        networkStatus = new Label();
        networkStatus.getElement().getStyle().setPaddingTop(10, Unit.PX);
        networkStatus.getElement().getStyle().setPaddingBottom(10, Unit.PX);

        Scheduler.get().scheduleFixedPeriod(this, 1000);

        vVerticalComponentGroup.add(networkStatus);
        vVerticalComponentGroup.add(goOnlineButton);

        panel.add(vVerticalComponentGroup);
    }

    @Override
    public boolean deactivate() {
        // Don't get out off offline mode automatically as user may be actively
        // filling an observation
        return false;
    }

    public boolean execute() {
        if (isActive()) {
            if (networkStatus != null) {
                if (isNetworkOnline()) {
                    networkStatus.setText("Your network connection is online.");
                    networkStatus.getElement().getStyle().setColor("green");
                    goOnlineButton.setVisible(true);
                } else {
                    networkStatus.setText("Your network connection is down.");
                    networkStatus.getElement().getStyle().setColor("");
                    goOnlineButton.setVisible(false);
                }
            }
            return true;
        }
        return false;
    }

    private static native boolean isNetworkOnline()
    /*-{
        return $wnd.navigator.onLine;
    }-*/;

    @Override
    public boolean isActive() {
        return isShowing();
    }

    @Override
    public void activate(ActivationReason event) {
        /*
         * OfflineDataService has async api that returns available species from
         * a cached resources.
         */
        OfflineDataService.getSpecies(new OfflineDataService.Callback() {
            public void setSpecies(final List<Species> birds) {
                buildUi(birds);
            }
        });
        
    }

}
