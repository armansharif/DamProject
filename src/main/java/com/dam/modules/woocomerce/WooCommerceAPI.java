package com.dam.modules.woocomerce;

import com.dam.commons.utils.BaseCommonUtils;
import com.dam.commons.utils.DateUtils;
import com.dam.modules.telegram.TelegramMessaging;
import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.oauth.OAuthConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WooCommerceAPI {
    private static final String API_URL = "https://kaafbazar.ir/wp-json/wc/v3/products/";
    private static final String CONSUMER_KEY = "ck_ac70036bf9e7addabc55a61abdf1ae26b0f19e55";
    private static final String CONSUMER_SECRET = "cs_bd660ad65c7fde434ddabf24c039ec0d7f23aa54";

    public static String chatIds[] = {"105542472"};

    public static void main(String[] args) {
        String[] navTimes = "08:15:00;15:04:00".split(";");
        String startTime = navTimes[0];
        String endTime = navTimes[1];
        String currentTime = DateUtils.getTimeSecond();
        if (currentTime.compareTo(endTime) <= 0)
            System.out.println("return false;");


        if (startTime.compareTo(currentTime) <= 0 && endTime.compareTo(currentTime) >= 0)
            System.out.println("return false;");

        System.out.println("return true;");

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/dam_sensor_db?useUnicode=yes&characterEncoding=UTF-8");//jdbc:mysql://localhost:3306/dam_sensor_db?useUnicode=yes&characterEncoding=UTF-8
        dataSource.setUsername("vps");
        dataSource.setPassword("vPs@2104D");
        ArrayList pCat = new ArrayList<>();
        pCat.add("'hasas'");
        pCat.add("'payamtozin'");
        pCat.add("'press'");
        pCat.add("'mahak'");
        pCat.add("'sadr'");
        pCat.add("'radin'");
        String condition = "";
        if (pCat.size() > 0) {
            condition = " AND product_cat IN (" + String.join(",", pCat) + ") ";
        }
        JdbcTemplate template = new JdbcTemplate(dataSource);
        List<Map<String, Object>> productList = template.queryForList(" select ps.site_product_id , price,sale_price , domain, customer_key,customer_secret,p.product_name,s.site_name ,product_sku,s.discount,in_stock FROM " +
                " wo_product p,  " +
                " wo_product_site ps, " +
                " wo_site s " +
                " where  " +
                " p.id = ps.product_id  AND   s.id = ps.site_id  " +
              //   " AND site_id=1  " +
              //  " AND p.id between 637 and 1015 " +
                " AND product_sku like '1010060%'" +
                " ;"
        );
        for (int i = 0; i < productList.size(); i++) {
            Map<String, Object> p = productList.get(i);

            updateProduct(p.get("site_product_id").toString(), p.get("sale_price").toString(), p.get("price").toString(), p.get("domain").toString(), p.get("customer_key").toString(), p.get("customer_secret").toString(), p.get("product_name").toString(), p.get("site_name").toString(), p.get("product_sku").toString(), p.get("discount").toString(), p.get("in_stock").toString(),(i+1)+"/"+productList.size());

        }

    }

    public static void updateProduct(String productId, String salePrice, String regularPrice, String domain, String key, String secret, String productName, String siteName, String product_sku, String discount, String inStock,String counter) {
        try {
            TelegramMessaging telegram = new TelegramMessaging();
            // ID محصول که می‌خواهیم قیمت آن را بروزرسانی کنیم
            //  int productId = 4203; // تغییر دهید به ID محصول واقعی


            // قیمت جدیدی که می‌خواهیم تنظیم کنیم


            // ساختن URL
            URL url = new URL("https://" + domain + "/wp-json/wc/v3/products/" + productId);
            String logMsg = counter+ "  ";
            // اتصال به API
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // افزودن اطلاعات احراز هویت (کلید و رمز ووکامرس)
            String auth = key + ":" + secret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + encodedAuth);

            // داده JSON برای بروزرسانی قیمت
            //   String jsonInputString = "{\"regular_price\": \"" + newPrice + "\"}";

            String jsonInputString = "{ \"sku\": \"" + product_sku + "\" \n";
            if (BaseCommonUtils.isNotNull(salePrice) && Integer.parseInt(salePrice) > 0) {
                salePrice = (Integer.parseInt(salePrice) - Integer.parseInt(discount)) + "";
                jsonInputString +=
                        " , \"sale_price\": \"" + salePrice + "\"\n";
            } else {
                salePrice = (Integer.parseInt(regularPrice) - Integer.parseInt(discount)) + "";
                jsonInputString += " , \"sale_price\": \"" + salePrice + "\"\n"; //discount
            }

            if (BaseCommonUtils.isNotNull(regularPrice) && Integer.parseInt(regularPrice) > 0) {
                jsonInputString += " , \"regular_price\": \"" + regularPrice + "\"\n";
            }

            logMsg += " - " + "   قیمت / قیمت ویژه" + salePrice + " / " + regularPrice + " برای   " + productName + "  \n  " + productId + " در سایت " + siteName;
            if (BaseCommonUtils.isNotNull(inStock) && Integer.parseInt(inStock.trim()) == 0) {
                jsonInputString += " , \"manage_stock\": false, \"in_stock\": false, \"stock_status\": \"outofstock\"  \n";

                logMsg += " \n   -   وضعیت موجودی: ناموجود در انبار\n";
            }

            jsonInputString += " }";

            // ارسال درخواست
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // دریافت پاسخ از سرور
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                logMsg +="   با موفقیت بروزرسانی شد.   " ;
                logMsg += " \n ";
                telegram.sendTelegramMSG(logMsg, chatIds);
                System.out.println(logMsg + "   با موفقیت بروزرسانی شد.   ");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                String decodedResponse = decodeUnicode(response.toString());
                // چاپ پاسخ موفقیت‌آمیز
                System.out.println("Response: " + decodedResponse.toString());
            } else {

                System.out.println("بروزرسانی قیمت محصول ناموفق بود. کد پاسخ: " + responseCode);
                System.out.println(logMsg + "   با خطا مواجه شد.   ");
                telegram.sendTelegramMSG(logMsg + "   با خطا مواجه شد.   ", chatIds);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorResponse.append(errorLine.trim());
                    }
                    System.out.println("Error Response: " + errorResponse.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // متد برای تبدیل توالی‌های  u به کاراکترهای واقعی
    public static String decodeUnicode(String input) {
        // regex برای پیدا کردن توالی‌های یونیکد مانند  uXXXX
        Pattern unicodePattern = Pattern.compile("\\\\u([0-9A-Fa-f]{4})");
        Matcher matcher = unicodePattern.matcher(input);
        StringBuffer decodedString = new StringBuffer();

        while (matcher.find()) {
            // تبدیل هر  uXXXX به کاراکتر معادل
            String unicodeChar = String.valueOf((char) Integer.parseInt(matcher.group(1), 16));
            matcher.appendReplacement(decodedString, unicodeChar);
        }
        matcher.appendTail(decodedString);

        return decodedString.toString();
    }
}
