package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SportsPage {

    private static final Logger log = LogManager.getLogger(SportsPage.class);

    WebDriver driver;
    WebDriverWait wait;

    public SportsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // --- Locators ---
    By filtersButton  = By.xpath("//button[contains(.,'Filters') and not(contains(.,'Apply'))]");
    By dateSortOption = By.xpath("//*[normalize-space(.)='Date']");
    By genreTab       = By.xpath("//*[normalize-space(.)='Genre']");
    By sportsOption   = By.xpath("//*[normalize-space(.)='Sports']");
    By applyButton    = By.xpath("//button[contains(.,'Apply Filters')]");
    By weekendChip    = By.xpath("//*[normalize-space(.)='This Weekend']");
    By eventCard      = By.xpath("//div[@class='dds-grid dds-gap-x-3 md:dds-gap-x-4 dds-grid-cols-1 dds-gap-y-8 md:dds-grid-cols-2 lg:dds-grid-cols-3 xl:dds-grid-cols-4 dds-justify-items-center lg:dds-justify-items-start']/a");

    // --- Actions ---

    public void applyWeekendSportsFilter() {
        wait.until(ExpectedConditions.elementToBeClickable(filtersButton)).click();
        wait.until(ExpectedConditions.elementToBeClickable(dateSortOption)).click();
        wait.until(ExpectedConditions.elementToBeClickable(genreTab)).click();

        try {
            WebElement sports = wait.until(ExpectedConditions.elementToBeClickable(sportsOption));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", sports);
            sports.click();
        } catch (TimeoutException e) {
            log.warn("Sports option not found in this city — closing filter panel.");
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
            return;
        }

        wait.until(ExpectedConditions.elementToBeClickable(applyButton)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(applyButton));
        wait.until(ExpectedConditions.elementToBeClickable(weekendChip)).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(eventCard));
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 600)");
        log.info("Sports weekend filter applied.");
    }


    public List<Event> getAllEvents() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        List<Event> events = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (int i = 0; i < 25; i++) {
            for (WebElement card : driver.findElements(eventCard)) {
                Event e = readCard(card, seen);
                if (e != null) events.add(e);
            }

            long before = ((Number) js.executeScript("return window.pageYOffset")).longValue();
            js.executeScript("window.scrollBy(0, window.innerHeight)");

            try {
                new WebDriverWait(driver, Duration.ofSeconds(2))
                        .until(d -> ((Number) ((JavascriptExecutor) d)
                                .executeScript("return window.pageYOffset")).longValue() > before);
            } catch (TimeoutException ignored) {}

            long after = ((Number) js.executeScript("return window.pageYOffset")).longValue();
            if (after == before) break;
        }

        js.executeScript("window.scrollTo(0, 0)");
        log.info("Collected " + events.size() + " events.");
        return events;
    }


    private Event readCard(WebElement card, Set<String> seen) {
        try {
            String link = card.getAttribute("href");
            if (link == null || link.isEmpty() || seen.contains(link)) return null;

            String[] lines = card.getText().trim().split("\\r?\\n");
            if (lines.length < 2) return null;

            String date  = lines[0].trim();
            String name  = lines[1].trim();
            if (name.isEmpty()) return null;

            String venue     = lines.length > 2 ? lines[2].trim() : "";
            String priceText = "";
            for (String line : lines) {
                if (line.startsWith("₹") || line.startsWith("Rs") || line.toLowerCase().contains("free")) {
                    priceText = line.trim();
                    break;
                }
            }

            seen.add(link);
            return new Event(name, date, venue, priceText);
        } catch (Exception e) {
            return null;
        }
    }


    public static class Event {
        public String name;
        public String date;
        public String venue;
        public String priceText;
        public int    price;

        public Event(String name, String date, String venue, String priceText) {
            this.name      = name;
            this.date      = date.isEmpty() ? "-" : date;
            this.venue     = venue.isEmpty() ? "-" : venue;
            this.priceText = priceText.isEmpty() ? "N/A" : priceText;
            this.price     = parsePrice(priceText);
        }

        private int parsePrice(String text) {
            if (text == null || text.isEmpty()) return Integer.MAX_VALUE;
            if (text.toLowerCase().contains("free")) return 0;
            String digits = text.replaceAll("[^0-9]", "");
            return digits.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(digits);
        }
    }
}
