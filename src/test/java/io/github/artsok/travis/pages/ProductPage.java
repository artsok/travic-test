package io.github.artsok.travis.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

public class ProductPage extends AbstractPage {

    private By productName = By.xpath("//div[@class='n-title__text']");

    ProductPage(WebDriver driver) {
        super(driver);
    }

    public String getProductName() throws TimeoutException {
        return getWhenVisible(productName).getText();
    }


}
