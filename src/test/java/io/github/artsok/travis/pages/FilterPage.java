package io.github.artsok.travis.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class FilterPage extends AbstractPage {

    private By lowerBoundPriceField = By.xpath("//*[@id='glf-pricefrom-var']");
    private By upperBoundPriceField = By.xpath("//*[@id='glf-priceto-var']");
    private By acceptFiltersButton = By.xpath("//a[contains(@class ,'show-filtered')]");
    private By checkBoxOfManufacturer;

    FilterPage(WebDriver driver) {
        super(driver);
    }

    public FilterPage setLowerBoundPrice(int price) throws TimeoutException {
        WebElement textBox = getWhenVisible(lowerBoundPriceField);
        textBox.sendKeys(String.valueOf(price));
        return new FilterPage(driver);
    }

    public FilterPage setUpperBoundPrice(int price) throws TimeoutException {
        WebElement textBox = getWhenVisible(upperBoundPriceField);
        textBox.sendKeys(String.valueOf(price));
        return new FilterPage(driver);
    }

    public FilterPage setManufacturers(List<String> manufacturers) throws TimeoutException {
        for (String i : manufacturers) {
            try {
                checkBoxOfManufacturer = By.xpath(String.format("//label[@class='checkbox__label' and contains(text(),'%s')]", i));
                clickCheckBoxWhenReady(checkBoxOfManufacturer);
            } catch (TimeoutException e) {
            }
        }
        return new FilterPage(driver);
    }

    public YandexMarketPage navigateToYandexMarketPage() throws TimeoutException {
        clickAndCloseWhenReady(acceptFiltersButton);
        return new YandexMarketPage(driver);
    }
}
