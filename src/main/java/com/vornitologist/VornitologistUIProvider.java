package com.vornitologist;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

public class VornitologistUIProvider extends UIProvider {

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        String userAgent = event.getRequest().getHeader("user-agent").toLowerCase();
        if(userAgent.contains("webkit") || userAgent.contains("firefox") || userAgent.contains("msie 1") || userAgent.contains("trident/7")) {
            return VornitologistUI.class;
        } else {
            return VornitologistFallbackUI.class;
        }
    }

}
