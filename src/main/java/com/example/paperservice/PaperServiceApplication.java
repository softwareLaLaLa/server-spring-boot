package com.example.paperservice;

import com.example.paperservice.DataProcess.DataInit;
import com.example.paperservice.service.RecommendService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@EnableEurekaClient
@RestController
@SpringBootApplication
public class PaperServiceApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(PaperServiceApplication.class, args);
        DataInit datainit = context.getBean(DataInit.class);
        RecommendService recommendService = context.getBean(RecommendService.class);
        if(false) {
            datainit.initData();
        }
        ScheduledExecutorService service1 = Executors.newSingleThreadScheduledExecutor();
        service1.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                recommendService.refreshHotPaperData();
            }
        }, 1, 1, TimeUnit.DAYS);
        ScheduledExecutorService service2 = Executors.newSingleThreadScheduledExecutor();
        service2.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                recommendService.freshTagData();
            }
        }, 1, 1, TimeUnit.DAYS);
        ScheduledExecutorService service3 = Executors.newSingleThreadScheduledExecutor();
        service3.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                recommendService.clusterPaper(15);
            }
        }, 1, 30, TimeUnit.DAYS);
    }

}
