package runners;

import base.BaseTest;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.AfterSuite;

@CucumberOptions(
        features = "src/test/resources/features",
        glue     = {"steps"},
        plugin   = {
                "pretty",
                "html:test-output/cucumber-report.html"
        },
        monochrome = true
)
public class CucumberRunner extends AbstractTestNGCucumberTests {

    @AfterSuite(alwaysRun = true)
    public void tearDown() {
        new BaseTest().closeBrowser();
    }
}

