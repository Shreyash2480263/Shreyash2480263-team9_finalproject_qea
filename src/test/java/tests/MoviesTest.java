package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.MoviesPage;

import java.util.List;

// TC2: Extract all movie languages and display them.
public class MoviesTest extends BaseTest {

    @Test(priority = 2)
    public void displayAllMovieLanguages() {
        HomePage home = new HomePage(driver);
        home.goHome();
        home.goToMovies();

        MoviesPage moviesPage = new MoviesPage(driver);
        List<String> languages = moviesPage.getAllLanguages();

        Assert.assertFalse(languages.isEmpty(), "No movie languages found.");

        log.info("Movie languages available on district.in:");
        for (int i = 0; i < languages.size(); i++) {
            log.info((i + 1) + ". " + languages.get(i));
        }
        log.info("Total languages: " + languages.size());
    }
}
