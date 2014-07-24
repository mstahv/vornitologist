package com.vornitologist.tb;

import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.android.AndroidDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.iphone.IPhoneDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vaadin.testbench.TestBench;

public class TBUtils {

    public static WebDriver getChromeDriver() {
        // Define where crome driver is installed
        System.setProperty("webdriver.chrome.driver",
                "/usr/local/bin/chromedriver");
        return TestBench.createDriver(new ChromeDriver());
    }

    /**
     * Returns a new iphone driver pointing to localhost:3001 ~ ios webdriver
     * running in a simulator on the same machine as the junit execution.
     * 
     * Add the driver url if you have a real device or a simulator on a separate
     * machine
     * 
     * @return
     */
    public static WebDriver getIPhoneDriver() {
        try {
            return TestBench.createDriver(new IPhoneDriver());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static WebDriver getIPhoneDriverFromGrid() {
        try {
            return TestBench.createDriver(new RemoteWebDriver(new URL(
                    "http://mmb:4444/wd/hub/"),
                    DesiredCapabilities.iphone()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static WebDriver getAndroidDriver() {
        return TestBench.createDriver(new AndroidDriver());
    }

}
