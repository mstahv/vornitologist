package com.vornitologist.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Component;
import com.vornitologist.model.ClassificationGroup;
import com.vornitologist.model.ClassificationItem;
import com.vornitologist.model.Species;
import com.vornitologist.util.ClassificatiodDataReader;

/**
 * Navigation manager (with NavigationView) is a great component to organize
 * hierarchical data. ClassificationHierarchy displays the taxonomy of birds
 * from order to species.
 * <p>
 * Views which are displayed in this navigation manager are
 * ClassificationGroupView and SpeciesView.
 * 
 * @see NavigationManager
 * @see SpeciesView
 * @see ClassificationGroupView
 */
@SuppressWarnings("serial")
public class ClassificationHierarchy extends NavigationManager implements
        VornitologistMainView {

    private static ClassificationGroup root;
    private ClassificationGroupView rootView;

    /**
     * Creates a classification hierarchy displaying the birds classification
     * group in the top level view
     */
    public ClassificationHierarchy() {
        rootView = new ClassificationGroupView(getBirds(), true);
        navigateTo(rootView);

        addNavigationListener(new NavigationListener() {

            @Override
            public void navigate(NavigationEvent event) {
                // Make sure Navigator contains correct stuff
                Component currentComponent = getCurrentComponent();
                if (currentComponent instanceof SpeciesView) {
                    SpeciesView sv = (SpeciesView) currentComponent;
                    Page.getCurrent().setUriFragment(
                            "!" + sv.getSpecies().getName(), false);
                } else if (currentComponent instanceof ClassificationGroupView) {
                    ClassificationGroupView gv = (ClassificationGroupView) currentComponent;
                    Page.getCurrent().setUriFragment(
                            "!" + gv.getGroup().getName(), false);
                }

            }
        });
    }

    /**
     * @return the classification group of birds
     */
    private static ClassificationGroup getBirds() {
        if (root == null) {
            try {
                root = ClassificatiodDataReader.readSpecies();
                return root;
            } catch (Exception e) {
                Logger.getAnonymousLogger()
                        .log(Level.SEVERE, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        return root;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

    /**
     * Gets a currently attached or completely new view for given classification
     * name
     * 
     * @param speciesOrGroup
     * @return
     */
    public ClassificationSubView getView(String speciesOrGroup) {
        try {
            ClassificationItem found = getBirds().find(speciesOrGroup);
            if (found instanceof Species) {
                Species s = (Species) found;
                if (getCurrentComponent() instanceof SpeciesView
                        && ((SpeciesView) getCurrentComponent()).getSpecies() == s) {
                    // existing if there is one
                    return (ClassificationSubView) getCurrentComponent();
                }
                return new SpeciesView(s);
            } else {
                ClassificationGroup group = (ClassificationGroup) found;
                if (getCurrentComponent() instanceof SpeciesView) {
                    navigateBack();
                }
                if (getCurrentComponent() instanceof ClassificationGroupView
                        && ((ClassificationGroupView) getCurrentComponent())
                                .getGroup() == group) {
                    return (ClassificationSubView) getCurrentComponent();
                }
                return new ClassificationGroupView(group);
            }
        } catch (Exception e) {
            return rootView;
        }
    }

    public void navigateToSubView(ClassificationSubView view) {
        if (getCurrentComponent() != view) {
            if (view instanceof SpeciesView) {
                SpeciesView sv = (SpeciesView) view;
                navigateToSubView(getView(sv.getSpecies().getParent().getName()));
                navigateTo(sv);
            } else {
                ClassificationGroupView groupView = (ClassificationGroupView) view;
                while (getCurrentComponent() != rootView
                        || getCurrentComponent() == view) {
                    navigateBack();
                }
                navigateTo(groupView);
            }
        }
    }
}
