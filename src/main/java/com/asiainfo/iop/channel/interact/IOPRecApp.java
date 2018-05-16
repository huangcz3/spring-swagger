package com.asiainfo.iop.channel.interact;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author migle 2018-4-16
 */
//@SpringBootApplication
//@EnableDiscoveryClient
//@EnableCircuitBreaker
@SpringCloudApplication
public class IOPRecApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext app = SpringApplication.run(IOPRecApp.class, args);
    }
}
