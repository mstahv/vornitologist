package com.vornitologist.tb;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;

public class ITBrowseLists extends TestBenchTestCase {

	public static final String TARGET_URL = "http://localhost:5678/";

	@Before
	public void setUp() throws Exception {
		setDriver(TestBench.createDriver(new ChromeDriver()));
	}

	@After
	public void tearDown() throws Exception {
		getDriver().quit();
	}

	@Test
	@Ignore
	public void browseBirdSpecies() {
		goTo(TARGET_URL + "/demo/?restartApplication");
		navigate("//span[@class = 'v-button-caption' and text() = 'Birds']");

		long time = 0;
		for (int loop = 0; loop < 20; loop++) {
			long begin = testBench().totalTimeSpentRendering();
			navigate("//div[contains(@class, 'v-touchkit-navbutton') and text() = 'Waterbird']");
			navigate("//div[contains(@class, 'v-touchkit-navbutton') and text() = 'Bean Goose']");

			verify("//div[@class = 'v-touchkit-navbar-caption']/span[text() = 'Bean Goose']");

			navigate("//div[@class = 'v-touchkit-navbar-right']//span[@class = 'v-button-wrap']"); // add
																							// observation
			verify("//div[@class = 'v-touchkit-navbar-caption']/span[text() = 'New Observation']");
			navigate("//span[@class = 'v-button-caption' and text() = 'Save']"); // save
																					// observation

			navigate("//div[contains(@class, 'v-touchkit-navbutton-back') and text() = 'Waterbird']");
			verify("//div[@class = 'v-touchkit-navbar-caption']/span[text() = 'Waterbird']");

			navigate("//div[contains(@class, 'v-touchkit-navbutton-back') and text() = 'Birds']");
			verify("//div[@class = 'v-touchkit-navbar-caption']/span[text() = 'Birds']");
			verify("//div[@class = 'v-captiontext' and text() = 'Podicipedidae']");
			time += testBench().totalTimeSpentRendering() - begin;
		}
		System.out.println("time was: " + time);
	}

	private void goTo(String url) {
		getDriver().get(url);
	}

	private void navigate(String xpath) {
		getElement(xpath).click();
	}

	private void verify(String xpath) {
		sleep();
		WebElement element = getElement(xpath);
		String text = element.getText();
		Assert.assertFalse(text.isEmpty());
	}

	private void sleep() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private WebElement getElement(String xpath) {
		return getDriver().findElement(By.xpath(xpath));
	}
}
