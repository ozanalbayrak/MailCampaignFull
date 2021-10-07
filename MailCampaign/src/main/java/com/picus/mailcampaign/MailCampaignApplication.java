package com.picus.mailcampaign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class MailCampaignApplication {

    public static void main(String[] args) {
        SpringApplication.run(MailCampaignApplication.class, args);
    }

}
