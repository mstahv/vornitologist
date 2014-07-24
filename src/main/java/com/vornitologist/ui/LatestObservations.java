package com.vornitologist.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.data.Container;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vornitologist.VornitologistUI;
import com.vornitologist.model.Observation;
import com.vornitologist.model.ObservationDB;
import com.vornitologist.model.ObservationPoint;
import com.vornitologist.model.Species;
import com.vornitologist.util.Translations;

public class LatestObservations extends NavigationView implements VornitologistMainView, ClickListener {

    private final Button addObservation = new Button(null, this);
    private final Button showObservations = new Button(null, this);

    private ResourceBundle tr;

    private final Table table = new Table() {
        private DateFormat df;

        @Override
        protected String formatPropertyValue(Object rowId, Object colId,
                com.vaadin.data.Property<?> property) {
            if (colId.equals("location")) {
                ObservationPoint value2 = (ObservationPoint) property
                        .getValue();
                return value2.getName();
            } else if (colId.equals("species")) {
                Species species = (Species) property.getValue();
                return tr.getString(species.getName());
            } else if (colId.equals("observationTime")) {
                Date date = (Date) property.getValue();
                if (df == null) {
                    df = SimpleDateFormat.getDateInstance(
                            SimpleDateFormat.SHORT, VornitologistUI.getApp()
                                    .getLocale());
                }
                return df.format(date);
            } else {
                return super.formatPropertyValue(rowId, colId, property);
            }
        };
    };

    @Override
    public void attach() {
        super.attach();
        if (tr == null) {
            buildView();
        }
    }

    private void buildView() {
        tr = Translations.get(getLocale());
        setCaption(tr.getString("Observations"));
        table.setSizeFull();
        populateTable();
        table.setVisibleColumns(new Object[] { "observationTime", "species",
                "location", "count" });
        table.setColumnHeader("species", tr.getString("species"));
        table.setColumnHeader("observationTime",
                tr.getString("observationtime"));
        table.setColumnHeader("location", tr.getString("location"));
        table.setColumnHeader("count", tr.getString("count"));

        // table.setColumnExpandRatio("observationTime", 1);
        table.setColumnExpandRatio("species", 1);
        table.setColumnExpandRatio("location", 0.5f);
        table.setColumnExpandRatio("count", 0.3f);
        setContent(table);
 
        addObservation.setIcon(FontAwesome.PLUS);
        setRightComponent(addObservation);
        
        showObservations.setIcon(FontAwesome.BAR_CHART_O);
        setLeftComponent(showObservations);

        table.addItemClickListener(new ItemClickListener() {
            public void itemClick(ItemClickEvent event) {
                Observation o = (Observation) event.getItemId();
                showObservationDetails(o);
            }

        });

    }

    private void showObservationDetails(final Observation o) {
        final ObservationDetailPopover popover = new ObservationDetailPopover(o);
        popover.showRelativeTo(getNavigationBar());
    }

    private void populateTable() {
        Container observationContainer = ObservationDB
                .getObservationContainer(getUI());
        table.setContainerDataSource(observationContainer);
    }

    public void buttonClick(ClickEvent event) {
        if (addObservation == event.getButton()) {
            Popover popover = new Popover();
            popover.setSizeFull();
            popover.setModal(false);
            popover.setContent(new AddObservationView(null, null));
            UI.getCurrent().addWindow(popover);
        } else if (showObservations == event.getButton()) {
            Popover popover = new Popover();
            popover.setSizeFull();
            popover.setModal(false);
            popover.setContent(new LatestObservationsGraphView());
            UI.getCurrent().addWindow(popover);
        }
    }

    public void cleanup() {
        ObservationDB.unregisterContainer(table.getContainerDataSource());
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub
        
    }

}
