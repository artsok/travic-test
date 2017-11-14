package io.github.artsok.travis.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

public class LandingPage extends AbstractPage {


    private By yandexMarketButton;

    public LandingPage(WebDriver driver) {
        super(driver);
    }

    public YandexMarketPage navigateToYandexMarket(String section) throws TimeoutException {
        yandexMarketButton = By.xpath(String.format("//a[contains(text(),'%s')]", section));
        clickAndCloseWhenReady(yandexMarketButton);
        return new YandexMarketPage(driver);
    }
}