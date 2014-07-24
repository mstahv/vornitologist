package com.vornitologist.widgetset.client.theme;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.DoNotEmbed;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface VornitologistBundle extends ClientBundle {
    
    public static final VornitologistBundle INSTANCE = GWT.create(VornitologistBundle.class);

    @Source("vornitologiststyles.css")
    public VornitologistCss css();
    
    @Source("icomoon.woff")
    @DoNotEmbed
    DataResource vIcons();

    @Source("icomoon.ttf")
    @DoNotEmbed
    DataResource vIconsTtf();

    
    @ImageOptions(repeatStyle=RepeatStyle.Horizontal)
    @Source("aboutBackground.png")
    public ImageResource aboutBackground();

}
