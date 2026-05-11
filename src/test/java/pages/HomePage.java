package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import utils.ConfigReader;


public class HomePage {

    private static final Logger log = LogManager.getLogger(HomePage.class);

    WebDriver driver;
    WebDriverWait wait;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // --- Locators ---
    By locationButton = By.cssSelector("button[data-district-ui='true']");
    By citySearchBox  = By.cssSelector("input[placeholder*='city' i], input[placeholder*='locality' i]");
    By eventsLink     = By.xpath("//*[normalize-space(.)='Events']");
    By moviesLink     = By.xpath("//*[normalize-space(.)='Movies']");

    // --- Actions ---
    public void selectCity() {
        String city = ConfigReader.get("city");

        wait.until(ExpectedConditions.elementToBeClickable(locationButton)).click();

        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(citySearchBox));
        searchBox.clear();
        searchBox.sendKeys(city);

        By cityOption = By.xpath("//*[normalize-space(text())='" + city + "']");
        wait.until(ExpectedConditions.elementToBeClickable(cityOption)).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(citySearchBox));

        log.info("City selected: " + city);
    }

    public void goToEvents() {
        wait.until(ExpectedConditions.elementToBeClickable(eventsLink)).click();
        wait.until(ExpectedConditions.urlContains("event"));
        log.info("Navigated to Events");
    }

    public void goToMovies() {
        wait.until(ExpectedConditions.elementToBeClickable(moviesLink)).click();
        wait.until(ExpectedConditions.urlContains("movie"));
        log.info("Navigated to Movies");
    }

    public void goHome() {
        driver.get(ConfigReader.get("url"));
        wait.until(ExpectedConditions.elementToBeClickable(eventsLink));
    }
}
