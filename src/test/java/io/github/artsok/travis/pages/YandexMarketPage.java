package io.github.artsok.travis.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;


public class YandexMarketPage extends AbstractPage {


    private By sectionButton;
    private By subsectionButton;
    private By filterButton;
    private By productBox = By.xpath("//div[contains(@class ,'n-snippet-card')]//span[contains(@class ,'header-text')]");
    private By searchField = By.xpath("//*[@id='header-search']");
    private By searchButton = By.xpath("//button[contains(@class ,'button2')]");


    private List<WebElement> listOfElements;
    public String elementFromList;

    YandexMarketPage(WebDriver driver) {
        super(driver);
    }

    public YandexMarketPage navigateToSection(String section) throws TimeoutException {
        sectionButton = By.xpath(String.format("//a[contains(text(),'%s') and contains(@class ,'link topmenu')]", section));
        clickAndCloseWhenReady(sectionButton);
        return new YandexMarketPage(driver);
    }

    public YandexMarketPage navigateToSubSection(String section) throws TimeoutException {
        subsectionButton = By.xpath(String.format("//a[contains(text(),'%s') and contains(@class ,'link catalog')]", section));
        clickAndCloseWhenReady(subsectionButton);
        return new YandexMarketPage(driver);
    }

    public FilterPage navigateToFilterPage() throws TimeoutException {
        filterButton = By.xpath("//a[contains(text(),'Перейти ко всем фильтрам')]");
        clickAndCloseWhenReady(filterButton);
        return new FilterPage(driver);
    }

    public int countElementsOnPage() throws TimeoutException {
        listOfElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(productBox));
        return listOfElements.size();
    }

    public void getElementFromList(int number) {
        elementFromList = listOfElements.get(number - 1).getAttribute("textContent");

    }

    public void setSearchField() throws TimeoutException {
        WebElement textBox = getWhenVisible(searchField);
        textBox.sendKeys(elementFromList);
    }

    public ProductPage navigateToProductPage() throws TimeoutException {
        clickAndCloseWhenReady(searchButton);
        return new ProductPage(driver);


    }
}
