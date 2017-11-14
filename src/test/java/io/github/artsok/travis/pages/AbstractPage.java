package io.github.artsok.travis.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AbstractPage {

    private static final long MAX_LOAD_TIME = 10;

    WebDriver driver;
    WebDriverWait wait;

    AbstractPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, MAX_LOAD_TIME);
    }

    public LandingPage navigateToYandex() {
        driver.get("https://www.yandex.ru");
        return new LandingPage(driver);
    }

    public void closeDriver() {
        driver.quit();
    }

    void clickAndCloseWhenReady(By locator) {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(locator));
        button.click();
        wait.until(ExpectedConditions.stalenessOf(button));
    }


    void clickCheckBoxWhenReady(By locator) {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(locator));
        button.click();
        wait.until(ExpectedConditions.elementToBeSelected(button));
    }


    WebElement getWhenVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}