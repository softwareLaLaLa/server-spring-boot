package com.example.paperservice;

import com.example.paperservice.controller.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@EnableEurekaClient
@RestController
@SpringBootApplication
public class PaperServiceApplication {

    @Autowired
    static RecommendService recommendService;

    public static void main(String[] args) {
        SpringApplication.run(PaperServiceApplication.class, args);
        ScheduledExecutorService service1 = Executors.newSingleThreadScheduledExecutor();
        service1.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                recommendService.refreshHotPaperData();
            }
        }, 0, 1, TimeUnit.DAYS);
        ScheduledExecutorService service2 = Executors.newSingleThreadScheduledExecutor();
        service2.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                recommendService.freshTagData();
            }
        }, 0, 1, TimeUnit.DAYS);
        ScheduledExecutorService service3 = Executors.newSingleThreadScheduledExecutor();
        service3.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                recommendService.clusterPaper();
            }
        }, 0, 30, TimeUnit.DAYS);
    }

}
