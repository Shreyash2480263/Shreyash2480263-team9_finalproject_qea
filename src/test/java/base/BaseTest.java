package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.time.Duration;
import java.util.Map;

import utils.ConfigReader;


public class BaseTest {

    public static WebDriver driver;
    protected static final Logger log = LogManager.getLogger(BaseTest.class);

    @BeforeSuite
    public void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs",
                Map.of("profile.default_content_setting_values.geolocation", 2));

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(ConfigReader.get("url"));
        log.info("Browser opened");
    }

    @AfterSuite
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
            log.info("Browser closed");
        }
    }
}
