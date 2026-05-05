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
    // Any clickable card that contains a price (₹ or "Free") — matches all sports activity tiles.
    By eventCard      = By.xpath("//a[@href][.//*[contains(text(),'₹') or contains(text(),'Free')]]");

    // --- Actions ---

    public boolean applyWeekendSportsFilter() {
        // Open filter panel
        wait.until(ExpectedConditions.elementToBeClickable(filtersButton)).click();

        // Click "Sort By Date" tab
        wait.until(ExpectedConditions.elementToBeClickable(dateSortOption)).click();

        // Click "Genre" tab — wait for it to be active
        wait.until(ExpectedConditions.elementToBeClickable(genreTab)).click();

        // Look for "Sports" in genre list. Some cities don't have it.
        try {
            WebElement sports = wait.until(ExpectedConditions.elementToBeClickable(sportsOption));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", sports);
            sports.click();
        } catch (TimeoutException e) {
            log.warn("Sports option not found in this city.");
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
            return false;
        }

        // Click "Apply Filters" — then wait for the filter panel to close
        wait.until(ExpectedConditions.elementToBeClickable(applyButton)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(applyButton));

        // Click "This Weekend" chip
        wait.until(ExpectedConditions.elementToBeClickable(weekendChip)).click();

        // Scroll past the filter chips so the event cards section comes into view.

        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 600)");

        // Wait up to 12 seconds for at least one event card to appear in the DOM.
        try {
            new WebDriverWait(driver, Duration.ofSeconds(12))
                    .until(ExpectedConditions.presenceOfElementLocated(eventCard));
        } catch (TimeoutException ignored) {
            log.info("No event cards visible yet — will scroll & re-check.");
        }

        log.info("Sports filter applied for this weekend");
        return true;
    }


    public List<Event> getAllEvents() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Don't go all the way to the top — we want to start scraping from where the cards begin.
        // applyWeekendSportsFilter already scrolled past the filter chips for us.

        List<Event> events = new ArrayList<>();
        Set<String> seenLinks = new HashSet<>();
        long lastScrollY = -1;

        // Scroll up to 25 times. Stop when scroll position stops changing (= bottom of page).
        for (int i = 0; i < 25; i++) {
            // Read every event card currently in the DOM
            for (WebElement card : driver.findElements(eventCard)) {
                Event e = readCard(card, seenLinks);
                if (e != null) events.add(e);
            }

            // Remember scroll position, then scroll one screen down
            long beforeY = ((Number) js.executeScript("return window.pageYOffset")).longValue();
            js.executeScript("window.scrollBy(0, window.innerHeight)");

            // Wait until either the scroll actually happened, OR 2s passes (= we are at the bottom)
            try {
                new WebDriverWait(driver, Duration.ofSeconds(2))
                        .pollingEvery(Duration.ofMillis(300))
                        .until(d -> {
                            long y = ((Number) ((JavascriptExecutor) d).executeScript("return window.pageYOffset")).longValue();
                            return y > beforeY;
                        });
            } catch (TimeoutException ignored) {
                // No scroll happened — we hit the bottom of the page
            }

            long afterY = ((Number) js.executeScript("return window.pageYOffset")).longValue();
            if (afterY == lastScrollY) break;     // Page didn't move — stop
            lastScrollY = afterY;
        }

        js.executeScript("window.scrollTo(0, 0)");
        log.info("Scraped " + events.size() + " events.");
        return events;
    }


    private Event readCard(WebElement card, Set<String> seenLinks) {
        try {
            String link = card.getAttribute("href");
            if (link == null || link.isEmpty() || seenLinks.contains(link)) return null;

            String fullText = card.getText().trim();
            if (fullText.isEmpty()) return null;       // card not rendered yet — retry next scroll

            String[] lines = fullText.split("\\r?\\n");
            if (lines.length < 2) return null;         // not enough info, skip

            // Find the line containing the price
            String priceText  = "";
            int    priceIndex = -1;
            for (int i = 0; i < lines.length; i++) {
                String t = lines[i].trim();
                if (t.startsWith("₹") || t.startsWith("Rs") || t.toLowerCase().contains("free")) {
                    priceText  = t;
                    priceIndex = i;
                    break;
                }
            }

            // Layout: line 0 = date, line 1 = name, line 2 = venue, line 3 = price
            String date  = lines[0].trim();
            String name  = lines.length > 1 ? lines[1].trim() : "";
            String venue = (lines.length > 2 && priceIndex != 2) ? lines[2].trim() : "";

            if (name.isEmpty()) return null;

            seenLinks.add(link);
            return new Event(name, date, venue, priceText);
        } catch (Exception e) {
            return null;
        }
    }

    // --- Inner class to hold one event ---
    public static class Event {
        public String name;
        public String date;
        public String venue;
        public String priceText;
        public int    price;

        public Event(String name, String date, String venue, String priceText) {
            this.name = name;
            this.date = date.isEmpty() ? "-" : date;
            this.venue = venue.isEmpty() ? "-" : venue;
            this.priceText = priceText.isEmpty() ? "N/A" : priceText;
            this.price = parsePrice(priceText);
        }

        private int parsePrice(String text) {
            if (text == null || text.isEmpty()) return Integer.MAX_VALUE;
            if (text.toLowerCase().contains("free")) return 0;
            String digits = text.replaceAll("[^0-9]", "");
            if (digits.isEmpty()) return Integer.MAX_VALUE;
            return Integer.parseInt(digits);
        }
    }
}
