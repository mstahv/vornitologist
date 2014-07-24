package com.vornitologist.tb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.junit.Test;

public class ITSmoke {
    /**
     * Ensures a valid vaadin kickstart page is returned from deployment url.
     * 
     * @throws IOException
     */
    @Test
    public void smokeTest() throws IOException {

        URL url = new URL(ITAddingObservation.TARGET_URL);

        InputStream openStream = url.openStream();

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(openStream));

        String line;
        boolean isInitPage = false;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("Widget")) {
                isInitPage = true;
            }
        }
        bufferedReader.close();
        org.junit.Assert.assertEquals(true, isInitPage);

    }

}
