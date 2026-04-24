package factory;

import java.io.FileInputStream;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class BaseClass {

    public static WebDriver driver;
    public static ExtentReports extent;
    public static ExtentTest extentTest;

    @BeforeSuite
    public void setUp() throws Exception {

        // Read config.properties file
        FileInputStream fis = new FileInputStream(
                System.getProperty("user.dir") + "/src/test/resources/config.properties");
        Properties config = new Properties();
        config.load(fis);

        // Open browser
        String browser = config.getProperty("browser");
        driver = DriverFactory.initDriver(browser);

        // Maximize browser window
        driver.manage().window().maximize();

        // Set wait times
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Open BookMyShow website
        driver.get(config.getProperty("url"));

        // Start ExtentReports
        extent = ExtentManager.initReports();
    }

    @AfterMethod
    public void afterEachTest(ITestResult result) {

        // If test failed — take screenshot
        if (result.getStatus() == ITestResult.FAILURE) {
            String screenshotPath = ScreenshotUtils.capture(driver, result.getName());
            extentTest.fail("Test Failed: " + result.getThrowable().getMessage());
            extentTest.addScreenCaptureFromPath(screenshotPath);
        }

        // If test passed
        if (result.getStatus() == ITestResult.SUCCESS) {
            extentTest.pass("Test Passed");
        }

        // If test skipped
        if (result.getStatus() == ITestResult.SKIP) {
            extentTest.skip("Test Skipped");
        }
    }

    @AfterSuite
    public void tearDown() {

        // Save ExtentReport
        extent.flush();

        // Close browser
        driver.quit();
    }
}