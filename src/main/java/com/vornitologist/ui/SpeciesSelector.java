package com.vornitologist.ui;

import java.util.Locale;
import java.util.ResourceBundle;

import com.vaadin.addon.touchkit.ui.HorizontalButtonGroup;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextField;
import com.vornitologist.model.ObservationDB;
import com.vornitologist.model.Species;

public class SpeciesSelector extends CustomField<Species> implements
        ClickListener {
    private Label display = new Label();
    private Button b = new Button("...", this);
    private ResourceBundle tr;
    private Locale locale;
    private CssLayout cssLayout;

    public SpeciesSelector(ResourceBundle tr, Locale locale) {
        this.tr = tr;
        this.locale = locale;
        setStyleName("speciesselector");
    }

    @Override
    protected Component initContent() {
        cssLayout = new CssLayout();
        cssLayout.addLayoutClickListener(new LayoutClickListener() {
            @Override
            public void layoutClick(LayoutClickEvent event) {
               buttonClick(null);
            }
        });
        HorizontalButtonGroup horizontalButtonGroup = new HorizontalButtonGroup();
        horizontalButtonGroup.addComponent(b);
        cssLayout.addComponents(b,display);
        return cssLayout;
    }

    @Override
    public Class<? extends Species> getType() {
        return Species.class;
    }

    @Override
    protected void setInternalValue(Species newValue) {
        if (newValue == null) {
            display.setValue(" --- ");
        } else {
            display.setValue(tr.getString(newValue.getName()));
        }
        super.setInternalValue(newValue);
    }

    @Override
    protected Species getInternalValue() {
        return super.getInternalValue();
    }

    @Override
    public void buttonClick(ClickEvent event) {
        final Popover popover = new Popover();
        int browserWindowWidth = Page.getCurrent().getBrowserWindowWidth();
        if (browserWindowWidth < 600) {
            popover.setWidth("100%");
        } else {
            popover.setWidth("600px");
        }
        popover.setHeight("372px");
        NavigationView navigationView = new NavigationView("Select species");
        popover.setContent(navigationView);

        CssLayout l = new CssLayout();
        navigationView.setContent(l);

        TextField textField = new TextField();
        textField.setWidth("100%");
        textField.setInputPrompt("filter list");
        l.addComponent(textField);

        final Table table = new Table();
        table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
        table.setHeight("300px");
        table.setWidth("100%");
        table.setSelectable(true);
        table.setImmediate(true);

        final IndexedContainer container = (IndexedContainer) ObservationDB
                .getSpeciesContainer(locale);
        table.setContainerDataSource(container);
        l.addComponent(table);

        textField.addTextChangeListener(new TextChangeListener() {

            @Override
            public void textChange(final TextChangeEvent event) {
                container.removeAllContainerFilters();
                container.addContainerFilter(new Filter() {
                    @Override
                    public boolean passesFilter(Object itemId, Item item)
                            throws UnsupportedOperationException {
                        StringBuilder sb = new StringBuilder();
                        for (Object o : item.getItemPropertyIds()) {
                            sb.append(item.getItemProperty(o).getValue()
                                    .toString());
                        }
                        return sb.toString().toLowerCase()
                                .contains(event.getText().toLowerCase());
                    }

                    @Override
                    public boolean appliesToProperty(Object propertyId) {
                        return true;
                    }
                });
            }
        });

        popover.showRelativeTo(getContent());

        if (getValue() != null) {
            table.setValue(getValue());
            table.setCurrentPageFirstItemId(getValue());
        }

        table.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(
                    com.vaadin.data.Property.ValueChangeEvent event) {
                popover.close();
                setValue((Species) event.getProperty().getValue());
            }
        });

    }

}
