package com.cabbooking.rideservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RideserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RideserviceApplication.class, args);
	}

}
