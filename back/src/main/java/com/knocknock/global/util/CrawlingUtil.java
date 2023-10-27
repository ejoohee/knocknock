package com.knocknock.global.util;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class CrawlingUtil {
    private WebDriver driver;

    private static final String base_url = "https://www.samsung.com/sec/search/searchresult/?keyword=";
    public void process() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\SSAFY\\chromedriver.exe");
        //크롬 드라이버 셋팅 (드라이버 설치한 경로 입력)
        ChromeOptions options = new ChromeOptions();
        // 브라우저 켜지지 않고 백그라운드 실행
        options.addArguments("--disable-popup-blocking");       //팝업안띄움
        options.addArguments("--disable-gpu");			//gpu 비활성화
        options.addArguments("--blink-settings=imagesEnabled=false"); //이미지 다운 안받음
        options.addArguments("--headless");
        // 원격 접근 허용
        options.addArguments("--remote-allow-origins=*");

        ChromeDriver cdriver = new ChromeDriver(options);
        driver = cdriver;
        //브라우저 선택

//        driver.close();	//탭 닫기
//        driver.quit();	//브라우저 닫기
    }

    public void close() {
        driver.close();	//탭 닫기
        driver.quit();	//브라우저 닫기
    }



    /**
     * data가져오기
     */
    public String getImgLink(String keyword) throws InterruptedException {
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));	//⭐⭐⭐
        //드라이버가 실행된 뒤 최대 10초 기다리겠다.
        StringBuilder sb = new StringBuilder(base_url);
        driver.get(sb.append(keyword).toString());    //브라우저에서 url로 이동한다.
        //Thread.sleep(1000); //브라우저 로딩될때까지 잠시 기다린다.

        webDriverWait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("span.img"))
                //cssSelector로 선택한 부분이 존재할때까지 기다려라
        );

//        List<WebElement> elements = null;
//        try {
//            elements = driver.findElements(By.cssSelector("div.slick-slide.slick-current.slick-active"));
//        } catch (NoSuchElementException e) {
//        }
//        elements = driver.findElements(By.cssSelector("div.slick-slide.slick-current.slick-active"));
//        if(elements.size() == 0) {
//            elements = driver.findElements(By.cssSelector("span.img"));
//        }
//        for (WebElement element : elements) {
//            System.out.println("----------------------------");
//            System.out.println(element.findElement(By.tagName("img")).getAttribute("alt"));	//⭐
//            System.out.println(element.findElement(By.tagName("img")).getAttribute("src"));	//⭐
//        }
        WebElement element = null;
        try {
            element = driver.findElement(By.cssSelector("div.slick-slide.slick-current.slick-active"));
        } catch (NoSuchElementException e) {
            element = driver.findElement(By.cssSelector("span.img"));
        }
        System.out.println("----------------------------");
//        System.out.println(element.findElement(By.tagName("img")).getAttribute("alt"));	//⭐
        System.out.println(element.findElement(By.tagName("img")).getAttribute("src"));	//⭐
        return element.findElement(By.tagName("img")).getAttribute("src");
    }

}
