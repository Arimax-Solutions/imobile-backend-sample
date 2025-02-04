package com.I_Mobile_Crazy.I_Mobile_Crazy.configuration.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Configuration
public class ModelMapperConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
