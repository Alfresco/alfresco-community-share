package org.alfresco.share;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.testng.annotations.Test;

public class SeleniumValidationTest extends ContextAwareWebTest {

	@Test
	public void checkIfSeleniumIsWorking() throws IOException {
		getBrowser().navigate().to("http://google.com");

		WebDriver augmentedDriver = new Augmenter().augment(getBrowser());
		File source = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(source, new File("./target", source.getName()));
	}

}
