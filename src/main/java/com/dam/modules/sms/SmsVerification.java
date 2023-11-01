package com.dam.modules.sms;

import com.dam.modules.convert.ConvertEnFa;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Component
public class SmsVerification {


    private final ConvertEnFa convertEnFa;


    private String USER_AGENT = "Mozilla/5.0";

    @Autowired
    public SmsVerification(ConvertEnFa convertEnFa) {
        this.convertEnFa = convertEnFa;
    }


    public String sendSmsVerificationGhasedak(String mobile, String vcode) throws IOException {

        String token = "4db977849e35de3b5cd616813d4d4aebe9c214661c33eeb91d05dfced24f4329";
        mobile = mobileNumberCorrection(mobile);
        String urlParameters = "receptor=" + mobile + "&type=1&template=najm&param1=" + vcode;
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "https://api.ghasedak.me/v2/verification/send/simple";
        URL url = new URL(request);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setInstanceFollowRedirects(false);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("charset", "utf-8");
        con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        con.setRequestProperty("apikey", token);
        con.setUseCaches(false);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(postData);
        }

//        String url = "https://api.ghasedak.me/v2/verification/send/simple";
//        String parameterSTR = "receptor=" + mobile + "&type=1&template=najm&param1=" + vcode;
//        URL obj = new URL(url);
//        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//        con.setRequestMethod("POST");
//        con.setRequestProperty("User-Agent", USER_AGENT);
//        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//        con.setRequestProperty("apikey", token);
//        // For POST only - START
//        con.setDoOutput(true);
//        OutputStream os = con.getOutputStream();
//        System.out.println(parameterSTR.getBytes());
//        os.write(parameterSTR.getBytes());
//        os.flush();
//        os.close();
//        // For POST only - END

        int responseCode = con.getResponseCode();
        JSONObject resJson = new JSONObject();

        if (responseCode == HttpURLConnection.HTTP_OK) { //success


            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer responseStr = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                responseStr.append(inputLine);
            }
            in.close();
            // print result
            System.out.println(responseStr.toString());
            JSONObject res = new JSONObject(responseStr.toString());


            JSONObject result = res.getJSONObject("result");
            String rString = result.getString("message");
            if (rString.equalsIgnoreCase("success")) {
                resJson.put("code", 200);
                resJson.put("status", "success");
                resJson.put("message", "Verification code sent successfully.");

            } else {

                resJson.put("code", 401);
                resJson.put("status", "fail");
                resJson.put("message", "Unfortunately, there is a problem");

            }


        } else {
            StringBuffer responseStr = new StringBuffer();
            if (con.getErrorStream() != null) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getErrorStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    responseStr.append(inputLine);
                }
                in.close();
                // print result
                System.out.println(responseStr.toString());
            }
            resJson.put("code", 401);
            resJson.put("status", "fail");
            resJson.put("message", "Unfortunately, there is a problem" + " /n " + responseStr.toString());

            // sms send fail
        }
        return resJson.toString();
    }

    public String sendSmsVerificationSMSIR(String mobile, String vcode) throws IOException {

        String token = this.getToken();
        mobile = mobileNumberCorrection(mobile);


        String jsonParameterSTR = "{"
                + " \"ParameterArray\":["
                + "{ \"Parameter\": \"VerificationCode\",\"ParameterValue\": \"" + vcode + "\"}"
                + "],"
                + "\"Mobile\":\"" + mobile + "\","
                + "\"TemplateId\":\"21827\""
                + "}";
        String url = "http://RestfulSms.com/api/UltraFastSend";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("x-sms-ir-secure-token", token);
        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        System.out.println(jsonParameterSTR.getBytes());
        os.write(jsonParameterSTR.getBytes());
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        JSONObject resJson = new JSONObject();

        if (responseCode == HttpURLConnection.HTTP_CREATED) { //success


            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer responseStr = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                responseStr.append(inputLine);
            }
            in.close();
            // print result
            System.out.println(responseStr.toString());
            JSONObject res = new JSONObject(responseStr.toString());


            boolean IsSuccessful = res.getBoolean("IsSuccessful");

            if (IsSuccessful) {
                resJson.put("code", 200);
                resJson.put("status", "success");
                resJson.put("message", "Verification code sent successfully.");

            } else {
                resJson.put("code", 401);
                resJson.put("status", "fail");
                resJson.put("message", "Unfortunately, there is a problem");

            }


        } else {
            resJson.put("code", 401);
            resJson.put("status", "fail");
            resJson.put("message", "Unfortunately, there is a problem");

            // sms send fail
        }
        return resJson.toString();
    }

    public int generateCode() {
        Random r = new Random(System.currentTimeMillis());
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    }

    public String mobileNumberCorrection(String number) {

        String mobile = number.trim();
        mobile = mobile.replaceAll("\\s+", "").replace("+98", "");
        mobile = mobile.replace("+98", "");
        if (mobile.startsWith("0098")) {
            mobile = mobile.replaceFirst("0098", "");
        }
        if (mobile.startsWith("98")) {
            mobile = mobile.replaceFirst("98", "");
        }
        if (!mobile.startsWith("0")) {
            mobile = "0" + mobile;
        }

        return convertEnFa.arabicToDecimal(mobile);
    }

    public static String getToken() throws IOException {
        String token = "";
        URL obj = new URL("http://RestfulSms.com/api/Token");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        String POST_PARAMS = "{ "
                + "  \"UserApiKey\": \"65c612af201dbbdd9c6012af\", "
                + "  \"SecretKey\": \"kalashahr123!@#\" "
                + "}";
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_CREATED) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            JSONObject ret = new JSONObject(response.toString());
            token = ret.getString("TokenKey");
        } else {
            System.out.println("POST request not worked");
        }
        return token;
    }


    public String sendSmsGenerateCMCOM(String mobile) throws IOException {

        String token = "450231c3-9441-4350-afe9-d63610694b52";
        mobile = mobileNumberCorrection(mobile);


        String jsonParameterSTR = "{" +
                "\"recipient\": \"" + mobile + "\"," +
                "\"sender\":\"InspectMark\"}";
        String url = "https://api.cmtelecom.com/v1.0/otp/generate";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("X-CM-ProductToken", token);
        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        System.out.println(jsonParameterSTR.getBytes());
        os.write(jsonParameterSTR.getBytes());
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        JSONObject resJson = new JSONObject();
        String idOfCM = "";
        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer responseStr = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                responseStr.append(inputLine);
            }
            in.close();
            // print result
            System.out.println(responseStr.toString());
            JSONObject res = new JSONObject(responseStr.toString());


            idOfCM = res.getString("id");

            if (idOfCM.length() > 0) {
                resJson.put("code", 200);
                resJson.put("status", "success");
                resJson.put("message", "Verification code sent successfully.");

            } else {
                resJson.put("code", 401);
                resJson.put("status", "fail");
                resJson.put("message", "Unfortunately, there is a problem");

            }


        } else {
            resJson.put("code", 401);
            resJson.put("status", "fail");
            resJson.put("message", "Unfortunately, there is a problem");

            // sms send fail
        }
        return idOfCM;
    }


    public boolean verificationCMCOM(String id, String code) throws IOException {

        String token = "450231c3-9441-4350-afe9-d63610694b52";

        String jsonParameterSTR = "{" +
                "\"id\": \"" + id + "\"," +
                "\"code\":\"" + code + "\"}";
        String url = "https://api.cmtelecom.com/v1.0/otp/verify";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("X-CM-ProductToken", token);
        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        System.out.println(jsonParameterSTR.getBytes());
        os.write(jsonParameterSTR.getBytes());
        os.flush();
        os.close();
        // For POST only - END
        boolean isValid = false;
        int responseCode = con.getResponseCode();
        JSONObject resJson = new JSONObject();
        String idOfCM = "";
        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer responseStr = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                responseStr.append(inputLine);
            }
            in.close();
            // print result
            System.out.println(responseStr.toString());
            JSONObject res = new JSONObject(responseStr.toString());


            isValid = res.getBoolean("valid");


        } else {
            resJson.put("code", 401);
            resJson.put("status", "fail");
            resJson.put("message", "Unfortunately, there is a problem");

            // sms send fail
        }
        return isValid;
    }


}
