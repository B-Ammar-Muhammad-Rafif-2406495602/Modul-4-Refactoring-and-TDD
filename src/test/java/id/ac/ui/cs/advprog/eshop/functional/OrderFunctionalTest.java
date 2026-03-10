package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class OrderFunctionalTest {

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    private String baseUrl;
    private ChromeDriver driver;

    @BeforeEach
    void setupTest() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testOrderHistoryPageLoads() {
        driver.get(baseUrl + "/order/history");
        String title = driver.getTitle();
        assertEquals("Order History", title);
    }

    @Test
    void testOrderHistoryFormSubmitWithNoResults() {
        driver.get(baseUrl + "/order/history");

        WebElement input = driver.findElement(By.id("author"));
        input.sendKeys("nonexistentauthor");

        WebElement button = driver.findElement(By.cssSelector("button[type='submit']"));
        button.click();

        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("No orders found") || pageSource.contains("Orders for"));
    }

    @Test
    void testOrderCreatePageLoads() {
        driver.get(baseUrl + "/order/create");
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("Create Order") || pageSource.contains("Product List"));
    }
}