package runners;

public class CucumberRunner {
    package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

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
    }

}
