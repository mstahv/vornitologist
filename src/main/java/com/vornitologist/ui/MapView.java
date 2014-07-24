package com.vornitologist.ui;

import java.util.List;
import java.util.ResourceBundle;

import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;
import org.vaadin.addon.leaflet.LeafletMoveEndEvent;
import org.vaadin.addon.leaflet.LeafletMoveEndListener;
import org.vaadin.addon.leaflet.shared.Bounds;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.addon.touchkit.extensions.Geolocator;
import com.vaadin.addon.touchkit.extensions.PositionCallback;
import com.vaadin.addon.touchkit.gwt.client.vcom.Position;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vornitologist.VornitologistUI;
import com.vornitologist.model.Observation;
import com.vornitologist.model.ObservationDB;
import com.vornitologist.model.ObservationPoint;
import com.vornitologist.util.Translations;

public class MapView extends NavigationView implements VornitologistMainView,
        PositionCallback, LeafletClickListener {

    private VornitologistMap map;
    private Bounds extent;
    private Button locatebutton;
    private LMarker you = new LMarker();

    @Override
    public void attach() {
        super.attach();
        buildView();
    };

    private void buildView() {
        ResourceBundle tr = Translations.get(getLocale());
        setCaption(tr.getString("Map"));

        if (map == null) {
            map = new VornitologistMap();

            map.addMoveEndListener(new LeafletMoveEndListener() {

                @Override
                public void onMoveEnd(LeafletMoveEndEvent event) {
                    extent = event.getBounds();
                    updateMarkers();
                }
            });

            map.setImmediate(true);

            map.setSizeFull();
            map.setZoomLevel(12);
            setContent(map);

            // Default to Vaadin HQ
            you.setPoint(new Point(60.452, 22.301));
            setCenter();

            updateMarkers();
        }

        locatebutton = new Button("Locate yourself", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Geolocator.detect(MapView.this);
                locatebutton.setCaption("Locating...");
            }
        });
        locatebutton.setDisableOnClick(true);
        setLeftComponent(locatebutton);

    }

    public void updateMarkers() {
        ObservationPoint topLeft = new ObservationPoint();
        topLeft.setLatitude(extent.getNorthEastLat());
        topLeft.setLongitude(extent.getSouthWestLon());

        ObservationPoint bottomRight = new ObservationPoint();
        bottomRight.setLatitude(extent.getSouthWestLat());
        bottomRight.setLongitude(extent.getNorthEastLon());

        List<Observation> observations = ObservationDB.getObservations(null,
                topLeft, bottomRight, 15, 1);

        map.removeAllComponents();

        for (Observation observation : observations) {
            ObservationPoint location = observation.getLocation();

            LMarker leafletMarker = new LMarker(location.getLatitude(),
                    location.getLongitude());
            leafletMarker.setIcon(new ThemeResource("birdmarker.png"));
            leafletMarker.setIconSize(new Point(50, 50));
            leafletMarker.setData(observation);
            leafletMarker.addClickListener(this);

            map.addComponent(leafletMarker);
        }

        map.addComponent(you);
    }

    @Override
    public void onSuccess(Position position) {
        you.setPoint(new Point(position.getLatitude(), position.getLongitude()));
        if (you.getParent() == null) {
            map.addComponent(you);
        }

        VornitologistUI app = VornitologistUI.getApp();
        app.setCurrentLatitude(position.getLatitude());
        app.setCurrentLongitude(position.getLongitude());

        setCenter();

        locatebutton.setCaption("Locate yourself");
        locatebutton.setEnabled(true);

    }

    private void setCenter() {
        if (map != null) {
            extent = new Bounds(you.getPoint());
            map.zoomToExtent(extent);
        }
    }

    @Override
    public void onFailure(int errorCode) {
        Notification
                .show("Geolocation request failed. You must grant access for geolocation requests.",
                        Type.ERROR_MESSAGE);
    }

    private void showPopup(Observation data) {
        ObservationDetailPopover observationDetailPopover = new ObservationDetailPopover(
                data);
        observationDetailPopover.showRelativeTo(getNavigationBar());
    }

    public void showObservation(Observation o) {
        map.setCenter(o.getLocation().getLatitude(), o.getLocation()
                .getLongitude());
        map.setZoomLevel(12);
    }

    @Override
    public void onClick(LeafletClickEvent event) {
        Object o = event.getSource();
        if (o instanceof AbstractComponent) {
            Observation data = (Observation) ((AbstractComponent) o).getData();
            showPopup(data);
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub
        
    }
}
