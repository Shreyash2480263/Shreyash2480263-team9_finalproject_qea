package utilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotUtils {

    public static String capture(WebDriver driver, String testName) {

        // Create file name with timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String screenshotPath = System.getProperty("user.dir")
                + "/screenshots/" + testName + "_" + timestamp + ".png";

        try {
            // Take screenshot
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // Save screenshot to screenshots folder
            File dest = new File(screenshotPath);
            FileUtils.copyFile(src, dest);

        } catch (Exception e) {
            System.out.println("Screenshot failed: " + e.getMessage());
        }

        return screenshotPath;
    }
}