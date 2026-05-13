package steps;

import base.BaseTest;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hooks {

    @Before
    public void setUp() {
        if (BaseTest.driver == null) {
            new BaseTest().openBrowser();
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        byte[] png = ((TakesScreenshot) BaseTest.driver).getScreenshotAs(OutputType.BYTES);
        scenario.attach(png, "image/png", scenario.getName());
    }
}
