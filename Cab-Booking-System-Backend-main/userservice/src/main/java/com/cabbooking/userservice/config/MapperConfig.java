package com.cabbooking.userservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class MapperConfig {

    private final Random random = new Random();

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Integer generateCode(){
        return 1000 + random.nextInt(9000);
    }
}
