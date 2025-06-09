package com.dam.modules.dam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataSyncJob {


    private final WebClient webClient;

    @Autowired
    public DataSyncJob(JdbcTemplate jdbcTemplate) {
        this.webClient = WebClient.create("http://localhost:8080/dam2"); // آدرس API لوکال
    }

    @Scheduled(fixedRate = 5 * 60 * 1000) // هر ۵ دقیقه یک‌بار
    public void sendData() {
        String sql = "select * from sample_data where created_at_server  between '2024-11-25' and '2024-12-25' " +
                "AND DAY(created_at_server) = DAY(CURDATE()) and data like 'body%'  " +
                "ORDER BY ABS(TIME_TO_SEC(TIMEDIFF(TIME(created_at_server), TIME(NOW())))) " +
                "LIMIT 1;"; // شرط دلخواه شما
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/dam_sensor_db?useUnicode=yes&characterEncoding=UTF-8");//jdbc:mysql://localhost:3306/dam_sensor_db?useUnicode=yes&characterEncoding=UTF-8
        dataSource.setUsername("vps");
        dataSource.setPassword("vPs@2104D");
        JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSource);

        List<String> dataList = jdbcTemplate.query(sql, (rs, rowNum) -> { System.out.println(rs.getString("id")); return rs.getString("data");});

        for (String data : dataList) {
            Map<String, String> body = new HashMap<>();
            body.put("data", data);

//            webClient.post()
//                    .uri("/data")
//                    .header("Content-Type", "application/json")
//                    .bodyValue(body)
//                    .retrieve()
//                    .toBodilessEntity()
//                    .doOnSuccess(response -> System.out.println("✅ Sent: " + data))
//                    .doOnError(error -> System.err.println("❌ Error sending: " + error.getMessage()))
//                    .subscribe();

            webClient.post()
                    .uri("/data")
                    .header("Content-Type", "text/plain") // چون رشته ساده می‌فرستیم
                    .bodyValue(data.replace("body:","")) // فقط خود رشته data، نه Map یا JSON
                    .retrieve()
                    .toBodilessEntity()
                    .doOnSuccess(response -> System.out.println("✅ Sent plain text: " + data))
                    .doOnError(error -> System.err.println("❌ Error sending: " + error.getMessage()))
                    .subscribe();
        }
    }
}
