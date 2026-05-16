package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.SportsPage;
import pages.SportsPage.Event;

import java.util.Comparator;
import java.util.List;

public class SportsTest extends BaseTest {

    @Test(priority = 1)
    public void displayWeekendSportsByLowestPrice() throws InterruptedException {
        HomePage home = new HomePage(driver);
        home.selectCity();
        home.goToEvents();

        SportsPage sports = new SportsPage(driver);
        sports.applyWeekendSportsFilter();
        Thread.sleep(3000);
        List<Event> events = sports.getAllEvents();

        Assert.assertFalse(events.isEmpty(), "No sports events found for this weekend.");

        events.sort(Comparator.comparingInt(e -> e.price));

        log.info("Sports events for this weekend:");
        for (Event e : events) {
            log.info(e.name + " - " + e.priceText);
        }
        log.info("Total: " + events.size());
    }
}
