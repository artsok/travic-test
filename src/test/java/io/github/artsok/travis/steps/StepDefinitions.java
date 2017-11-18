package io.github.artsok.travis.steps;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.github.artsok.travis.pages.FilterPage;
import io.github.artsok.travis.pages.LandingPage;
import io.github.artsok.travis.pages.ProductPage;
import io.github.artsok.travis.pages.YandexMarketPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Assert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import ru.yandex.qatools.allure.annotations.Attachment;

import java.util.List;


public class StepDefinitions {

    private LandingPage landingPage;
    private YandexMarketPage yandexMarketPage;
    private FilterPage filterPage;
    private ProductPage productPage;
    private String exceptionMessage;
    private WebDriver driver;


    @Before//(value = "@All")
    public void setUp() throws IllegalAccessException, InstantiationException {
        System.out.println("Инициализация");
        Class<? extends WebDriver> driverClass = ChromeDriver.class;
        WebDriverManager.getInstance(driverClass).setup();
        driver = driverClass.newInstance();

    }


    @After//(value = "~@All")
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        System.out.println("Выход ");
    }


    @Given("^I open the browser and expand to full screen$")
    public void iOpenTheFirefoxBrowserAndExpandToFullScreen() {
        driver.manage().window().maximize();
    }

    @And("^I open yandex\\.ru$")
    public void iOpenYandexRu() {
        landingPage = new LandingPage(driver);
        landingPage.navigateToYandex();

    }

    @When("^I select \"([^\"]*)\"\\(market\\.yandex\\.ru\\)$")
    public void iSelectTheSectionMarketYandexRu(String section) throws TimeoutException {
        try {
            yandexMarketPage = landingPage.navigateToYandexMarket(section);
        } catch (TimeoutException e) {
            exceptionMessage = getExceptionMessage("section Маркет wasn't loaded, change MAX_LOAD_TIME for page");
            throw e;
        }
    }

    @And("^I select the section \"([^\"]*)\"$")
    public void iSelectTheSubsection(String section) throws TimeoutException {
        try {
            yandexMarketPage.navigateToSection(section);
        } catch (TimeoutException e) {
            exceptionMessage = getExceptionMessage("section Компьютеры wasn't loaded, change MAX_LOAD_TIME for page");
            throw e;
        }
    }

    @And("^I select the subsection \"([^\"]*)\"$")
    public void iSelectTheSubsubsection(String section) throws TimeoutException {
        try {
            yandexMarketPage.navigateToSubSection(section);
        } catch (TimeoutException e) {
            exceptionMessage = getExceptionMessage("section Ноутбуки wasn't loaded, change MAX_LOAD_TIME for page");
            throw e;
        }
    }

    @And("^I go to advanced search$")
    public void iGoToAdvancedSearch() throws TimeoutException {
        try {
            filterPage = yandexMarketPage.navigateToFilterPage();
        } catch (TimeoutException e) {
            exceptionMessage = getExceptionMessage("element Перейти ко всем фильтрам wasn't loaded, change MAX_LOAD_TIME for page");
            throw e;
        }
    }

    @And("^I set the search price parametr from (\\d+) rubles$")
    public void iSetTheSearchPriceParametrFrom(int price) throws TimeoutException {
        try {
            filterPage.setLowerBoundPrice(price);
        } catch (TimeoutException e) {
            exceptionMessage = getExceptionMessage("element Поле нижней границы цены wasn't loaded, change MAX_LOAD_TIME for page");
            throw e;
        }
    }

    @And("^I set the search price parametr to (\\d+) rubles$")
    public void iSetTheSearchPriceParametrTo(int price) throws TimeoutException {
        try {
            filterPage.setUpperBoundPrice(price);
        } catch (TimeoutException e) {
            exceptionMessage = getExceptionMessage("element Поле верхней границы цены wasn't loaded, change MAX_LOAD_TIME for page");
            throw e;
        }
    }

    @And("^I choose the manufacturers (.*)$")
    public void iChooseTheManufacturersHPLenovo(List<String> manufacturers) throws TimeoutException {
        try {
            filterPage.setManufacturers(manufacturers);
        } catch (TimeoutException e) {
            exceptionMessage = getExceptionMessage("element Производители wasn't loaded, change MAX_LOAD_TIME for page");
            throw e;
        }
    }

    @And("^I click the Apply button$")
    public void iClickTheApplyButton() throws TimeoutException {
        try {
            yandexMarketPage = filterPage.navigateToYandexMarketPage();
        } catch (TimeoutException e) {
            exceptionMessage = getExceptionMessage("element Применить фильтры wasn't loaded, change MAX_LOAD_TIME for page");
            throw e;
        }
    }

    @Then("^I check that the items on page (\\d+)$")
    public void iCheckThatTheItemsOnPage(int quantity) throws TimeoutException {
        try {
            Assert.assertEquals(quantity, yandexMarketPage.countElementsOnPage());
        } catch (TimeoutException e) {
            exceptionMessage = getExceptionMessage("element Список товаров wasn't loaded, change MAX_LOAD_TIME for page");
            throw e;
        } catch (AssertionError e) {
            exceptionMessage = getExceptionMessage("expected and actual results not equal, change MAX_LOAD_TIME for page");
            throw e;
        }
    }

    @And("^I remember the item number (\\d+) from the list$")
    public void rememberTheItemNumberFromTheList(int number) throws TimeoutException {
        yandexMarketPage.getElementFromList(number);
    }

    @And("^I enter the stored value in the search string$")
    public void iEnterTheStoredValueInTheSearchString() throws TimeoutException {
        try {
            yandexMarketPage.setSearchField();
        } catch (TimeoutException e) {
            exceptionMessage = getExceptionMessage("element Поле поиска wasn't loaded, change MAX_LOAD_TIME for page");
            throw e;
        }
    }

    @And("^I find and verify that the name of the product corresponds to the stored value$")
    public void iFindAndVerifyThatTheNameOfTheProductCorrespondsToTheStoredValue() throws TimeoutException {
        try {
            productPage = yandexMarketPage.navigateToProductPage();
            Assert.assertEquals(productPage.getProductName(), yandexMarketPage.elementFromList);
        } catch (TimeoutException e) {
            exceptionMessage = getExceptionMessage("element Имя товара wasn't loaded, change MAX_LOAD_TIME for page");
            throw e;
        } catch (AssertionError e) {
            exceptionMessage = getExceptionMessage("expected and actual results not equal, change MAX_LOAD_TIME for page");
            throw e;
        }
    }

    @Attachment(type = "text/plain")
    private static String getExceptionMessage(String message) {
        return message;
    }


}