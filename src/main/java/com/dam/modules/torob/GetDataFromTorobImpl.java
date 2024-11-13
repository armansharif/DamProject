package com.dam.modules.torob;


import com.dam.commons.utils.BaseCommonUtils;
import com.dam.commons.utils.NumberUtils;
import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.WooCommerceAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;
import okhttp3.*;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

@Service
public class GetDataFromTorobImpl implements GetDataFromTorob {

    private static TorobLinksRepository torobLinksRepository;

    //public static String chatIds[] = {"105542472", "2055890344"};
    public static String chatIds[] = {"105542472"};
    public static String managerChatIds[] = {"105542472", "2055890344"};
    public static String justMe[] = {"105542472"};

    @Autowired
    public GetDataFromTorobImpl(TorobLinksRepository torobLinksRepository) {
        this.torobLinksRepository = torobLinksRepository;
    }

    public static void main(String[] args) {
        try {
            getData("");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<BigDecimal> getPriceOfTorob(String link, WebDriver driver) throws MalformedURLException {
        List<BigDecimal> prices = new ArrayList<>();
        //implicit wait
        driver.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
        randomDelay(1, 5);
        driver.get(new URL(link).toString());
        // JavaScript Executor to check ready state
        JavascriptExecutor jvs = (JavascriptExecutor) driver;
        if (jvs.executeScript("return document.readyState").toString().equals("complete")) {
            System.out.println("Page has loaded");
        }

        List<WebElement> items = driver.findElements(By.className("shop-card"));
        boolean isMeNotFirst = false;

        String name = "";
        String price = "0";
        String product = "";
        String rank = "";
        String msg = " " + link + " \n";
        String msgToManager = "";
        if (items.size() > 2) {
            for (int j = 0; j < items.size(); j++) {

                name = "فروشنده:   ";
                price = "قیمت:   ";
                product = "کالا:   ";
                rank = "آگهی";
                if (j > 0) {
                    rank = " رتبه: " + j + " ";
                }
                WebElement divItem = items.get(j);
                if (!divItem.findElements(By.className("name1-guarantee")).isEmpty()) {
                    name += divItem.findElement(By.className("name1-guarantee")).getText();
                } else if (!divItem.findElements(By.className("shop-info")).isEmpty()) {//shop-info
                    name += divItem.findElement(By.className("shop-info")).getText();
                }

                if (!divItem.findElements(By.className("price-credit")).isEmpty()) {
                    price += divItem.findElement(By.className("price-credit")).getText();
                } else if (!divItem.findElements(By.className("purchase-info")).isEmpty()) {
                    price += divItem.findElement(By.className("purchase-info")).getText();
                }
                if (!divItem.findElements(By.className("product-name")).isEmpty()) {
                    product += divItem.findElement(By.className("product-name")).getText();
                    msg += rank + "  \n  " + name + "  \n   " + product + "  \n  " + price + " \n-------------------\n";
                } else if (!divItem.findElements(By.className("product-info")).isEmpty()) {
                    //  product += divItem.findElement(By.className("product-info")).getText();
                    // msg += rank + "  \n  " + name + "  \n   " + product + "  \n  " + price + " \n-------------------\n";
                }
                if (j == 1 && !name.contains("کفِ بازار") && !name.contains("ترازو و باسکول عطایی") && !name.contains("انتخاب تراز") && !name.contains("مرجع تخصصی ترازو حساس")) {
                    msgToManager += " " + "\n" + " بروزرسانی قیمت   " + price + "\n" + "  برای " + " \" " + product + " \" ";
                    isMeNotFirst = true;
                }
            }
            if (isMeNotFirst)
                msgToManager = msgToManager + "\n" + " موافق هستید  ؟" + "\n.\n.\n.\n.\n" + link;
            sendTelegramMSG(msgToManager, justMe);
            // sendTelegramMSG(msg, chatIds);

            //check condition and send propose to manager

        }
        return prices;
    }

    public static void getData(String url) throws MalformedURLException {

        Double minPercentProfit = 0.1;
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/dam_sensor_db?useUnicode=yes&characterEncoding=UTF-8");//jdbc:mysql://localhost:3306/dam_sensor_db?useUnicode=yes&characterEncoding=UTF-8
        dataSource.setUsername("vps");
        dataSource.setPassword("vPs@2104D");
        ArrayList pCat = new ArrayList<>();
     //   pCat.add("'hasas'");
     //   pCat.add("'payamtozin'");
     //   pCat.add("'press'");
     // //  pCat.add("'mahak'");
        pCat.add("'press_sepehr'");
       // pCat.add("'radin'");
        sendTelegramMSG(" شروع استخراج اطلاعات " + String.join(",", pCat) + "  ⬇  ⬇  ⬇  ⬇  ", chatIds);
        String condition = "";
        if (pCat.size() > 0) {
            condition = " AND product_cat IN (" + String.join(",", pCat) + ") ";
        }
        JdbcTemplate template = new JdbcTemplate(dataSource);
       // List<Map<String, Object>> linksMap = template.queryForList(" SELECT url URL,product_cat pc FROM mytable WHERE mergeStatus = 'ادغام شده'   AND inStock=1  " + condition + "  ORDER BY product_cat DESC;");
         List<Map<String, Object>> linksMap = template.queryForList(" SELECT torob_link URL,BASE_PRICE BASE_PRICE,product_id,site_id FROM update_table where product_category = 'press_sepehr';");

//        List<String> links = linksMap.stream().map(obj -> {
//            Map<String, String> bn = (Map) obj;
//            return bn.get("URL");
//        }).collect(Collectors.toList());

        System.clearProperty("webdriver.chrome.driver");
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");
        // ChromeOptions c =new ChromeOptions().addArguments("--remote-allow-origins=*", "--ignore-certificate-errors");
        WebDriver driver = new ChromeDriver(new ChromeOptions().addArguments("--remote-allow-origins=*", "--ignore-certificate-errors"));
//        List<String> links = torobLinksRepository.findAll().stream().map(m -> m.getUrl())
//                .collect(Collectors.toList());

        for (int i = 0; i < linksMap.size(); i++) {
            Map<String, Object> bn = linksMap.get(i);


            try {
                String baseUrl = "";
                String BaseListUR = bn.get("URL").toString();
                String productId = bn.get("product_id").toString();
                String siteId = bn.get("site_id").toString();
                BigDecimal basePrice = new BigDecimal(0);
                if (BaseCommonUtils.isNotNull(bn.get("BASE_PRICE")))
                    basePrice = new BigDecimal(bn.get("BASE_PRICE").toString());

                BigDecimal minAllowedPrice = basePrice.add(basePrice.multiply(new BigDecimal(minPercentProfit)));
                //implicit wait
                driver.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
                randomDelay(1, 5);
                driver.get(new URL(BaseListUR).toString());
                // JavaScript Executor to check ready state
                JavascriptExecutor jvs = (JavascriptExecutor) driver;
                if (jvs.executeScript("return document.readyState").toString().equals("complete")) {
                    System.out.println("Page has loaded");
                }

                // driver.findElements(By.className("shop-card")).get(0).findElement(By.className("name1-guarantee")).getText()
                //
                if (!driver.findElements(By.className("online-show-more-btn")).isEmpty()) {
                    driver.findElement(By.className("online-show-more-btn")).click();
                    randomDelay(1, 2);
                } else if (!driver.findElements(By.className("show-more-btn")).isEmpty()) {
                    driver.findElement(By.className("show-more-btn")).click();
                    randomDelay(1, 2);
                }

                List<WebElement> items = driver.findElements(By.className("shop-card"));

                String name = "";
                String price = "";
                String product = "";
                String rank = "";
                String msg = " " + BaseListUR + " \n";

                if (!driver.findElements(By.className("showcase")).isEmpty()) {
                    WebElement showCase = driver.findElement(By.className("showcase"));
                    String showCaseName = "محصول مرجع:  ";
                    if (!driver.findElements(By.className("name")).isEmpty()) {
                        showCaseName += driver.findElement(By.className("name")).getText();
                        //sendTelegramMSG(showCaseName, "105542472");
                        msg += showCaseName + " \n-------------------\n";
                    } else {
                        showCaseName += driver.findElement(By.className("showcase")).getText();
                        msg += showCaseName + " \n-------------------\n";
                    }

                }
                String msgToManager = "";
                String updateMSG = "";
                boolean isMeNotFirst = false;
                BigDecimal secondPriceInTorob = new BigDecimal(0);
                BigDecimal firstPriceInTorob = new BigDecimal(0);

                if (items.size() > 2) {
                    for (int j = 0; j < 3; j++) {

                        name = "فروشنده:   ";
                        price = "قیمت:   ";
                        product = "کالا:   ";
                        rank = "آگهی";
                        if (j > 0) {
                            rank = " رتبه: " + j + " ";
                        }
                        WebElement divItem = items.get(j);
                        if (!divItem.findElements(By.className("name1-guarantee")).isEmpty()) {
                            name += divItem.findElement(By.className("name1-guarantee")).getText();
                        } else if (!divItem.findElements(By.className("shop-info")).isEmpty()) {//shop-info
                            name += divItem.findElement(By.className("shop-info")).getText();
                        }

                        if (!divItem.findElements(By.className("price-credit")).isEmpty()) {
                            price += divItem.findElement(By.className("price-credit")).getText();
                        } else if (!divItem.findElements(By.className("purchase-info")).isEmpty()) {
                            price += divItem.findElement(By.className("purchase-info")).getText();
                        }
                        if (!divItem.findElements(By.className("product-name")).isEmpty()) {
                            product += divItem.findElement(By.className("product-name")).getText();
                            msg += rank + "  \n  " + name + "  \n   " + product + "  \n  " + price + " \n-------------------\n";
                        } else if (!divItem.findElements(By.className("product-info")).isEmpty()) {
                            //  product += divItem.findElement(By.className("product-info")).getText();
                            // msg += rank + "  \n  " + name + "  \n   " + product + "  \n  " + price + " \n-------------------\n";
                        }
                        if (j == 1 && !name.contains("کفِ بازار") && !name.contains("ترازو و باسکول عطایی") && !name.contains("انتخاب تراز") && !name.contains("مرجع تخصصی ترازو حساس")) {
                            msgToManager += "#SUGGESTED_PRICE \n❓❓❓❓❓❓❓" + "\n" + " آیا با  " + price + "\n" + "  برای " + " \" " + product + " \" ";
                            isMeNotFirst = true;

                            firstPriceInTorob = new BigDecimal(getAllDigit(price));

                            if (firstPriceInTorob.compareTo(minAllowedPrice) > 0) {
                                updateMSG += "#UPDATE_PRICE \n" + " قیمت " + product + " به " + firstPriceInTorob.subtract(new BigDecimal(5000)) + "  کاهش یابد." + "\n\n\n";
                                //update price
                                updateKaafbazarProduct(productId,firstPriceInTorob.subtract(new BigDecimal(5000)).toString(),product);
                            } else {
                                updateMSG = "#INVALID_PRICE \n" + " قیمت " + secondPriceInTorob.subtract(new BigDecimal(50000)) + " برای " + product + " کمتر از حد مجاز هست" + "\n.\n.\n.\n.\n" + BaseListUR;
                            }
                        }
                        if (isMeNotFirst && j == 2 && !name.contains("کفِ بازار") && !name.contains("ترازو و باسکول عطایی") && !name.contains("انتخاب تراز") && !name.contains("مرجع تخصصی ترازو حساس")) {
                            msgToManager += "\n\n\n\n یا \n\n\n\n" + "❓❓❓❓❓❓❓" + "\n" + " آیا با  " + price + "\n" + "  برای " + " \" " + product + " \" ";
                            secondPriceInTorob = new BigDecimal(getAllDigit(price));

                            if (firstPriceInTorob.compareTo(new BigDecimal(0)) > 0 &&
                                    secondPriceInTorob.compareTo(new BigDecimal(0)) > 0) {
                                if (secondPriceInTorob.compareTo(minAllowedPrice) > 0) {
                                    updateMSG += "#UPDATE_PRICE \n" + " قیمت " + product + " به " + secondPriceInTorob.subtract(new BigDecimal(50000)) + "  افزایش یابد." + "\n\n\n";
                                } else {
                                    updateMSG = "#INVALID_PRICE \n" + " قیمت " + secondPriceInTorob.subtract(new BigDecimal(50000)) + " برای " + product + " کمتر از حد مجاز هست" + "\n.\n.\n.\n.\n" + BaseListUR;
                                }
                            }
                        }

                    }
                    if (isMeNotFirst)
                        msgToManager = msgToManager + "\n" + " موافق هستید  ؟" + "\n.\n.\n.\n.\n" + BaseListUR;
                    sendTelegramMSG(msgToManager, managerChatIds);
                    sendTelegramMSG(updateMSG, managerChatIds);
                    // sendTelegramMSG(msg, chatIds);

                    //check condition and send propose to manager


                }
            } catch (Exception ignore) {
            }
        }

        sendTelegramMSG(" اتمام استخراج اطلاعات " + String.join(",", pCat) + " ⬆  ⬆  ⬆  ⬆ ", chatIds);
        driver.quit();
    }


    public static void randomDelay(float min, float max) {


        int random = (int) (max * Math.random() + min);
        try {
            Thread.sleep(random * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Integer[] sendTelegramMSG(String msg, String chatId) {
        Integer[] count = new Integer[2];
        int acceptCount = 0;
        int errorCount = 0;
        String telegramBotId = "6943726583:AAHAB3bAaZekn4UOzl0uyHC8nBrPhNWwriQ";
        try {
            URI uri = new URI("https", "api.telegram.org", "/bot" + telegramBotId + "/sendMessage", "chat_id=" + chatId + "&text=" + msg, null);
            HttpPost post = new HttpPost(uri);
            HttpClient httpclient = HttpClientBuilder.create().build();
            try {
                HttpResponse response = httpclient.execute(post);
                String responseEntity = null;
                if (response != null) {
                    acceptCount += 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
                errorCount += 1;
            }

        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        count[0] = acceptCount;
        count[1] = errorCount;
        return count;
    }

    public static Integer[] sendTelegramMSG(String msg, String[] chatIds) {
        Integer[] count = new Integer[2];
        int acceptCount = 0;
        int errorCount = 0;
        String telegramBotId = "6943726583:AAHAB3bAaZekn4UOzl0uyHC8nBrPhNWwriQ";
        try {
            for (String chatId : chatIds) {
                URI uri = new URI("https", "api.telegram.org", "/bot" + telegramBotId + "/sendMessage", "chat_id=" + chatId + "&text=" + msg, null);
                HttpPost post = new HttpPost(uri);
                HttpClient httpclient = HttpClientBuilder.create().build();
                try {
                    HttpResponse response = httpclient.execute(post);
                    String responseEntity = null;
                    if (response != null) {
                        acceptCount += 1;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    errorCount += 1;
                }
            }
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        count[0] = acceptCount;
        count[1] = errorCount;
        return count;
    }

    public static String getAllDigit(String str) {
        // Converting the given string
        // into a character array
        char[] charArray = str.toCharArray();
        String result = "";

        // Traverse the character array
        for (int i = 0; i < charArray.length; i++) {

            // Check if the specified character is not digit
            // then add this character into result variable
            if (Character.isDigit(charArray[i])) {
                result = result + charArray[i];
            }
        }

        // Return result
        return result;
    }

    public static void updateKaafbazarProduct(String productId, String salePrice, String productName) {
        try {
            OAuthConfig config = new OAuthConfig("http://kaafbazar.ir",
                    "ck_ac70036bf9e7addabc55a61abdf1ae26b0f19e55",
                    "cs_bd660ad65c7fde434ddabf24c039ec0d7f23aa54");
            WooCommerce wooCommerce = new WooCommerceAPI(config, ApiVersionType.V3);


            // Get all with request parameters
//            Map<String, String> params = new HashMap<>();
//            params.put("per_page", percentEncode("100"));
//            params.put("offset", percentEncode("0"));
//            List products = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params);
//
            //  Map product = wooCommerce.get(EndpointBaseType.PRODUCTS.getValue(), 7645);

            String json = "{\n" +
                    "  \"sale_price\": \"" + salePrice + "\"\n" +
                    "}";
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url("https://kaafbazar.ir/wp-json/wc/v3/products/" + productId)
                    .method("PUT", body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Basic Y2tfYWM3MDAzNmJmOWU3YWRkYWJjNTVhNjFhYmRmMWFlMjZiMGYxOWU1NTpjc19iZDY2MGFkNjVjN2ZkZTQzNGRkYWJmMjRjMDM5ZWMwZDdmMjNhYTU0")
                    .build();
            Response response = client.newCall(request).execute();
            System.out.println("update "+ productName + " price to = "+salePrice);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void updateWoocomerceProduct(String productId, String price,String salePrice, String productName,String domain,String key,String secret) {
        try {
            OAuthConfig config = new OAuthConfig("http://"+domain,
                    key,
                    secret);
            WooCommerce wooCommerce = new WooCommerceAPI(config, ApiVersionType.V3);


            // Get all with request parameters
//            Map<String, String> params = new HashMap<>();
//            params.put("per_page", percentEncode("100"));
//            params.put("offset", percentEncode("0"));
//            List products = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params);
//
            //  Map product = wooCommerce.get(EndpointBaseType.PRODUCTS.getValue(), 7645);

            String json = "{\n" +
                    "  \"sale_price\": \"" + salePrice + "\"\n" +
                    "}";
            if(BaseCommonUtils.isNotNull(price)){
                  json = "{\n" +
                        "  \"sale_price\": \"" + salePrice + "\",\n" +
                        "  \"price\": \"" + salePrice + "\"\n" +
                        "}";
            }
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url("https://"+domain+"/wp-json/wc/v3/products/" + productId)
                    .method("PUT", body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Basic Y2tfYWM3MDAzNmJmOWU3YWRkYWJjNTVhNjFhYmRmMWFlMjZiMGYxOWU1NTpjc19iZDY2MGFkNjVjN2ZkZTQzNGRkYWJmMjRjMDM5ZWMwZDdmMjNhYTU0")
                    .build();
            Response response = client.newCall(request).execute();
            System.out.println("update "+ productName + " price to = "+salePrice);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
