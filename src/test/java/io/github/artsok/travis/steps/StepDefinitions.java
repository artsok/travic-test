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
import io.github.artsok.travis.utils.VideoRecorder;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import ru.yandex.qatools.allure.annotations.Attachment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class StepDefinitions {

    private LandingPage landingPage;
    private YandexMarketPage yandexMarketPage;
    private FilterPage filterPage;
    private ProductPage productPage;
    private String exceptionMessage;
    private WebDriver driver;
    private VideoRecorder vr;


    @Before//(value = "@All")
    public void setUp() throws IllegalAccessException, InstantiationException, IOException {
        Files.createDirectories(Paths.get("./video"));
        vr = new VideoRecorder("./video");
        vr.startRecording();

        Stream<Path> paths = Files.find(Paths.get("."), 10, (path, basicFileAttributes) -> path.startsWith("video"));

        log.info(" WTFFFF + " + paths.toString());


        ChromeDriverManager.getInstance().setup();
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        cap.setCapability(ChromeOptions.CAPABILITY, options);
        driver = new ChromeDriver();
    }


    @After//(value = "~@All")
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        vr.stopRecording();
    }


    @Given("^I open the browser and expand to full screen$")
    public void iOpenTheFirefoxBrowserAndExpandToFullScreen() {
        System.out.println("height " + driver.manage().window().getSize().height);
        System.out.println("width " + driver.manage().window().getSize().width);
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