package com.vornitologist.tb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.iphone.IPhoneDriver;
import org.openqa.selenium.remote.DriverCommand;

import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.commands.TestBenchCommands;

/**
 * This is an example if an automated integration/acceptance test for a TouchKit
 * application using Vaadin TestBench. The test simulates following user
 * actions:
 * 
 * 1. Open the application, change user name and language
 * 
 * 2. Navigate via all views to birds view
 * 
 * 3. Choose a bird and fill and add an observation of it
 * 
 * 4. Ensure the observation was properly saved from the observations view
 * 
 */
public class ITAddingObservation {

    public static final String TARGET_URL = "http://localhost:5678/";

    private static final String TEST_USER = "Mick Crashtest";

    protected WebDriver driver;

    /**
     * Runs the test with Google Chrome. Chrome has an excellent WebDriver
     * support, there are versions for varios platforms and it uses the same
     * rendering engine as most mobile platforms. That is why it is an excellent
     * help for developers working with mobile application testing.
     * 
     * Also during actual automated testing phase it can be used as a low cost
     * fallback for real mobile devices if one cannot afford to make tests run
     * on real devices or on emulators/simulators.
     */
    @Test
    // @Ignore
    public void testWithChrome() {
        setDriver(TBUtils.getChromeDriver());
        driver.manage().window().setSize(new Dimension(320, 640));
        runParts();
    }

    @Test
    @Ignore
    public void testWithIPhoneSimulator() {
        setDriver(TBUtils.getIPhoneDriver());
        runParts();
    }

    @Test
    @Ignore
    public void testWithIPhoneViaGrid() throws Exception {
        // setDriver(TBUtils.getIPhoneDriverFromGrid());
        IPhoneDriver chrome = new IPhoneDriver() {
            @Override
            public <X> X getScreenshotAs(OutputType<X> target) {
                String value = (String) execute(DriverCommand.SCREENSHOT)
                        .getValue();
                return target.convertFromBase64Png(value);
            }
        };
        setDriver(chrome);
        runParts();
    }

    private void runParts() {
        changeSettings();
        checkViewsRenderProperly();
        fillAnObservation();
        ensureObservationIsPersisted();
        reportExecutionTime();
    }

	private void reportExecutionTime() {
		TestBenchCommands tbd = (TestBenchCommands) TestBench.createDriver(driver);
        long totalTimeSpentRendering = tbd.totalTimeSpentRendering();
        long totalTimeSpentServicingRequests = tbd
                .totalTimeSpentServicingRequests();
        System.out.println(String.format(
                "Processing times:\n*******************\n\t Client: %d \n\t Server: %d ",
                totalTimeSpentRendering, totalTimeSpentServicingRequests));
	}

    /**
     * Opens the application, sets username to TEST_USER, changes language to
     * suomi, ensures language has changed from the caption.
     */
    private void changeSettings() {
        driver.get(TARGET_URL);

        WebElement usernameInput = driver.findElement(By.id("username"));
        usernameInput.clear();
        usernameInput.sendKeys(TEST_USER);

        // Change language to Finnish
        driver.findElement(By.xpath("//label[text() = 'suomi']")).click();

        // assert the caption changed to "Asetukset" from "Settings"
        assertCaptionText("Asetukset");
        
        File screenshotAs = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.FILE);

        /* Custom TestBench additions like screenshot comparison */
        TestBenchCommands testbench = (TestBenchCommands) driver;
        //testbench.compareScreen(screenshotAs)

    }

    /**
     * Opens the observation view and verifies that the recently added entry is
     * a first row. Then opens details for it and verifies other details.
     */
    private void ensureObservationIsPersisted() {
        // change to observations view
        driver.findElement(By.xpath("//span[text() = 'Havainnot']")).click();

        // Get the first row in the body table and ensure it contains both the
        // correct bird
        WebElement firstRow = driver.findElement(By
                .xpath("//table[@class='v-table-table']//tr"));
        String firstRowText = firstRow.getText();
        assertTrue(firstRowText.contains("Teeri"));

        // click on the Teeri text to open popup
        firstRow.findElement(By.xpath(".//*[text() = 'Teeri']")).click();

        String popupText = driver
                .findElement(By.className("v-window-contents")).getText();

        // assert the popup contains proper details about the observation that
        // was just filled
        assertTrue(popupText.contains("Teeri"));
        assertTrue(popupText.contains(TEST_USER));
    }

    /**
     * Navigates to Teeri, opens observation addition view, ensures the right
     * observer from settings view is used and saves an observation.
     */
    private void fillAnObservation() {
        // we are at birds view after checkViewsRenderProperly

        driver.findElement(By.xpath("//*[text() = 'Metsäkanat']")).click();
        // sleep(1000);
        driver.findElement(By.xpath("//*[text() = 'Teeri']")).click();

        // sleep(1000);
        // Click the button on top right corner
        WebElement newButton = driver
                .findElement(By
                        .xpath("//div[@class = 'v-touchkit-navbar-right']//span[@class = 'v-button-wrap']"));
        newButton.click();

        WebElement observerinput = driver.findElement(By.id("observer"));

        String observername = observerinput.getAttribute("value");

        // Ensure the observer input has correct value from the settings
        assertEquals(TEST_USER, observername);

        // save the observation
        driver.findElement(By.xpath("//span[text() = 'Tallenna']")).click();

    }

    /**
     * Checks that tabsheet navigation works correctly. Leaves the app to be on
     * the birds view.
     */
    private void checkViewsRenderProperly() {

        WebElement toolbar = driver.findElement(By
                .className("v-touchkit-tabbar-toolbar"));
        List<WebElement> tabButtons = toolbar.findElements(By
                .className("v-button-caption"));

        tabButtons.get(2).click();

        assertCaptionText("Kartta"); // Map

        tabButtons.get(1).click();
        assertCaptionText("Havainnot"); // Observations

        tabButtons.get(0).click();
        assertCaptionText("Linnut"); // Birds

    }

    private void setDriver(WebDriver chrome) {
        driver = chrome;
    }

    protected void assertCaptionText(String expected) {
        String captionText = driver.findElement(
                By.className("v-touchkit-navbar-caption")).getText();
        assertEquals(expected, captionText);
    }

    @After
    public void tearDown() {
        // Sleeping for demonstration purposes so that the browser won't close
        // too fast.
        sleep(2000);
        driver.quit();
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
