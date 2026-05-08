package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;


public class ExtentListener implements ITestListener {

    private ExtentReports extent;
    private ExtentTest    test;

    // Runs once before all tests start
    @Override
    public void onStart(ITestContext context) {
        ExtentSparkReporter spark = new ExtentSparkReporter("test-output/ExtentReport-AllTests.html");
        spark.config().setDocumentTitle("District Automation Report");
        spark.config().setReportName("All Test Cases");

        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    @Override
    public void onTestStart(ITestResult result) {
        test = extent.createTest(result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.log(Status.PASS, "Test passed");

        // Take screenshot and attach to the report
        String path = Screenshot.take(result.getMethod().getMethodName() + "_PASS");
        if (path != null) {
            try { test.addScreenCaptureFromPath(path); } catch (Exception ignored) {}
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.log(Status.FAIL, "Test failed: " + result.getThrowable());

        // Take screenshot and attach to the report
        String path = Screenshot.take(result.getMethod().getMethodName() + "_FAIL");
        if (path != null) {
            try { test.addScreenCaptureFromPath(path); } catch (Exception ignored) {}
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.log(Status.SKIP, "Test skipped");
    }

    // Runs once after all tests finish — writes the report to disk
    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}
