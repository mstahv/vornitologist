package com.vornitologist.widgetset.client.theme;

import com.vaadin.addon.touchkit.gwt.client.ThemeLoader;

public class VornitologistThemeLoader extends ThemeLoader {

    @Override
    public void load() {
        // Load default TouchKit theme...
        super.load();
        // ... and Vornitologist specific additions from own client bundle
        VornitologistBundle.INSTANCE.css().ensureInjected();
    }

}
