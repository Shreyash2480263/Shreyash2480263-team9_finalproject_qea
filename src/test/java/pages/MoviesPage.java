package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class MoviesPage {

    private static final Logger log = LogManager.getLogger(MoviesPage.class);

    WebDriver driver;
    WebDriverWait wait;

    public MoviesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // --- Locators ---
    By filtersButton = By.xpath("//button[contains(.,'Filters') and not(contains(.,'Apply'))]");
    By languageTab   = By.xpath("//*[normalize-space(.)='Language']");
    By languageLabel = By.cssSelector("span.dds-text-base.dds-font-semibold.dds-text-primary.dds-leading-normal");

    // --- Actions ---

    public List<String> getAllLanguages() {
        wait.until(ExpectedConditions.elementToBeClickable(filtersButton)).click();
        wait.until(ExpectedConditions.elementToBeClickable(languageTab)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(languageLabel));

        List<String> languages = new ArrayList<>();
        for (WebElement label : driver.findElements(languageLabel)) {
            String text = label.getText().trim();
            if (text.matches("[A-Za-z]+") && !languages.contains(text)) {
                languages.add(text);
            }
        }

        driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);

        log.info("Languages found: " + languages.size());
        return languages;
    }
}
