package com.dam.modules.woocomerce;

import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.WooCommerceAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;
import com.icoderman.woocommerce.oauth.SpecialSymbol;
import okhttp3.*;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
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
             System.out.println("asdsdasdasdasd");
        String json="{\n" +
        "  \"price\": \"11100000\",\n" +
        "  \"regular_price\": \"11100000\",\n" +
        "  \"sale_price\": \"11000000\"\n" +
        "}";
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url("https://kaafbazar.ir/wp-json/wc/v3/products/7596")
                    .method("PUT", body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Basic Y2tfYWM3MDAzNmJmOWU3YWRkYWJjNTVhNjFhYmRmMWFlMjZiMGYxOWU1NTpjc19iZDY2MGFkNjVjN2ZkZTQzNGRkYWJmMjRjMDM5ZWMwZDdmMjNhYTU0")
                    .build();
            Response response = client.newCall(request).execute();
            System.out.println("asdsdasdasdasd");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static String percentEncode(String s) {
        final String UTF_8 = "UTF-8";

        try {
            return URLEncoder.encode(s, UTF_8)
                    // OAuth encodes some characters differently:
                    .replace(SpecialSymbol.PLUS.getPlain(), SpecialSymbol.PLUS.getEncoded())
                    .replace(SpecialSymbol.STAR.getPlain(), SpecialSymbol.STAR.getEncoded())
                    .replace(SpecialSymbol.TILDE.getEncoded(), SpecialSymbol.TILDE.getPlain());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
