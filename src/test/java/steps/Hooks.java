package steps;

import base.BaseTest;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

// Takes a screenshot after every Cucumber scenario and embeds it in the report.
public class Hooks {

    @After
    public void afterScenario(Scenario scenario) {
        byte[] png = ((TakesScreenshot) BaseTest.driver).getScreenshotAs(OutputType.BYTES);
        scenario.attach(png, "image/png", scenario.getName());
    }
}
