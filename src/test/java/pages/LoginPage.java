package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private static final Logger log = LogManager.getLogger(LoginPage.class);

    WebDriver driver;
    WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // --- Locators ---
    // The profile circle in the top-right of the header
    By signInButton = By.xpath("//*[@id=\"master-header\"]/div/div[1]/div[2]/div[2]/div[2]/div");

    // The mobile number input
    By mobileInput = By.cssSelector("input[inputmode='numeric']");

    By continueBtn = By.xpath("//button[normalize-space(.)='Continue']");

    // The red error text shown below the input
    By errorMessage = By.cssSelector("[class*='dds-text-red']");

    // --- Actions ---


    public void clickSignInButton() {
        wait.until(ExpectedConditions.elementToBeClickable(signInButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(mobileInput));
        log.info("Mobile sign-in modal is open");
    }

    public void enterMobileNumber(String number) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(mobileInput));
        input.clear();
        input.sendKeys(number);
    }

    public void clickContinue() {
        wait.until(ExpectedConditions.elementToBeClickable(continueBtn)).click();
    }

    /* Returns the validation error text shown after submitting an invalid mobile number. */
    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage))
                .getText()
                .trim();
    }
}
