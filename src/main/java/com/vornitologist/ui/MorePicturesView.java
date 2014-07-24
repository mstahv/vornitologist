package com.vornitologist.ui;

import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.addon.touchkit.ui.NavigationManager.NavigationEvent;
import com.vaadin.addon.touchkit.ui.NavigationManager.NavigationEvent.Direction;
import com.vaadin.addon.touchkit.ui.NavigationManager.NavigationListener;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.addon.touchkit.ui.SwipeView;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vornitologist.model.ObservationDB;
import com.vornitologist.model.Species;
import com.vornitologist.util.Translations;
import com.vornitologist.util.WikiImageProxy;

public class MorePicturesView extends Popover {
    private int index = 0;
    private List<Species> list;
    private Species species;

    public MorePicturesView(Species species) {
        Logger.getAnonymousLogger().warning("MorePicturesView");
        this.species = species;
    }
    
    @Override
    public void attach() {
        super.attach();

        final NavigationManager navigationManager = new NavigationManager();

        setContent(navigationManager);

        // These are not actually for correct species.
        list = ObservationDB.getAllSpecies();
        index = list.indexOf(species);

        navigationManager.navigateTo(getView(index));
        try {
            navigationManager.setNextComponent(getView(index+1));
        } catch (Exception e) {}
        try {
            navigationManager.setPreviousComponent(getView(index-1));
        } catch (Exception e) {}

        navigationManager.addNavigationListener(new NavigationListener() {

            @Override
            public void navigate(NavigationEvent event) {
                Direction direction = event.getDirection();
                if (direction == Direction.FORWARD) {
                    index++;
                    if (index <= list.size()-1) {
                        navigationManager.setNextComponent(getView(index + 1));
                    }
                } else {
                    index--;
                    if (index > 1) {
                        navigationManager
                                .setPreviousComponent(getView(index - 1));
                    }
                }
            }
        });

        setSizeFull();

        Notification.show("Swipe left/right to see all photos");

    }

    private SwipeView getView(int i) {
        final Species species = list.get(i);
        
        ResourceBundle tr = Translations.get(getLocale());
        NavigationView navigationView = new NavigationView(tr.getString(species.getName())
               + " " + (i + 1) + "/" + list.size());

        Button c = new Button("",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        close();
                    }
                });
        c.addStyleName("close");
        navigationView.setRightComponent(c);

        final SwipeView swipeView = new SwipeView();
        swipeView.setContent(navigationView);

        Resource imageResource = WikiImageProxy.getImage(UI.getCurrent(),
                species.getName());

        Image image = new Image(null, imageResource);
        image.setWidth("100%");
        Button button = new Button("See details");
        button.addClickListener(new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                close();
                UI.getCurrent().getNavigator().navigateTo(species.getName());
            }
        });
        navigationView.setContent(new CssLayout(image, button));

        return swipeView;
    }

}
