package utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    public static ExtentReports initReports() {

        // Set report save location
        String reportPath = System.getProperty("user.dir") + "/reports/ExtentReport.html";

        // Create reporter
        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setDocumentTitle("BookMyShow Test Report");
        spark.config().setReportName("Team 9 - Automation Results");

        // Create ExtentReports and attach reporter
        ExtentReports extent = new ExtentReports();
        extent.attachReporter(spark);

        // Add project info
        extent.setSystemInfo("Project", "BookMyShow Automation");
        extent.setSystemInfo("Team", "Team 9");
        extent.setSystemInfo("Browser", "Edge");

        return extent;
    }
}