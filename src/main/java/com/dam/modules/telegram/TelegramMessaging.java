package com.dam.modules.telegram;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class TelegramMessaging {
    public Integer[] sendTelegramMSG(String msg, String[] chatIds) {
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
                   // e.printStackTrace();
                    errorCount += 1;
                }
            }
        } catch (URISyntaxException e1) {
         //   e1.printStackTrace();
        }
        count[0] = acceptCount;
        count[1] = errorCount;
        return count;
    }
}
